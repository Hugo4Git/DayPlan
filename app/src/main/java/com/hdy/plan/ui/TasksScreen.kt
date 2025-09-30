package com.hdy.plan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TasksScreen(
    vm: TasksViewModel = viewModel(factory = TasksViewModel.Companion.factory())
) {
    val list by vm.items.collectAsStateWithLifecycle()
    var pickerForId by remember { mutableStateOf<Long?>(null) }
    var initialTime by remember { mutableStateOf(LocalTime.now()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.add() }, containerColor = MaterialTheme.colorScheme.secondary) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (list.isEmpty()) {
                    Text(
                        text = "Tap + to add a note",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    list.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .widthIn(max = 520.dp)
                                    .fillMaxWidth()
                                    .shadow(8.dp, shape = MaterialTheme.shapes.medium),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {

                                    AssistChip(
                                        onClick = {
                                            pickerForId = item.id
                                            initialTime = item.time
                                        },
                                        label = {
                                            Text(
                                                text = item.time.format(
                                                    DateTimeFormatter.ofPattern(
                                                        "HH:mm"
                                                    )
                                                ),
                                                fontSize = 20.sp,
                                            )
                                        },
                                        border = null,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(start = 8.dp, top = 2.dp)
                                    )

                                    var localText by rememberSaveable(item.id) { mutableStateOf(item.text) }
                                    LaunchedEffect(item.text) {
                                        localText = item.text
                                    }
                                    TextField(
                                        value = localText,
                                        onValueChange = { localText = it; vm.onEdit(item.id, it) },
                                        placeholder = {
                                            Text(
                                                "Write something…",
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = 12.dp,
                                                end = 12.dp,
                                                top = 48.dp,
                                                bottom = 12.dp
                                            ),
                                        shape = MaterialTheme.shapes.small,
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent
                                        ),
                                    )

                                    IconButton(
                                        onClick = { vm.remove(item.id) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete"
                                        )
                                    }
                                }
                            }
                    }
                    if (pickerForId != null) {
                        TimePicker(
                            initial = initialTime,
                            onPicked = { picked ->
                                vm.updateTime(pickerForId!!, picked)
                                pickerForId = null
                            },
                            onDismiss = { pickerForId = null }
                        )
                    }
                    Spacer(Modifier.height(56.dp))
                }
            }
        }
    }
}