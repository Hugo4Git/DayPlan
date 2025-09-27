package com.hdy.plan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hdy.plan.ui.theme.PlanTheme
import androidx.compose.material3.AssistChip
import androidx.compose.ui.graphics.Color
import com.hdy.plan.ui.theme.ThemeFamily
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanTheme(
                theme = ThemeFamily.NATURE_CALM,
                useDynamicColor = false,
                darkTheme = null
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Boxes()
                }
            }
        }
    }
}

@Composable
fun Boxes() {
    data class BoxItem(
        val text: MutableState<TextFieldValue>,
        val time: MutableState<LocalTime> = mutableStateOf(LocalTime.now())
    )

    val items = remember { mutableStateListOf<BoxItem>() }

    fun addItem() {
        items += BoxItem(mutableStateOf(TextFieldValue("")))
    }

    fun removeItem(index: Int) {
        items.removeAt(index)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { addItem() }, containerColor = MaterialTheme.colorScheme.secondary) {
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
                if (items.isEmpty()) {
                    Text(
                        text = "Tap + to add a note",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    items.forEachIndexed { index, item ->
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
                                        item.time.value = LocalTime.now()
                                        // TODO: Open your time picker and set item.time.value accordingly
                                    },
                                    label = {
                                        Text(
                                            text = item.time.value.format(DateTimeFormatter.ofPattern("HH:mm")),
                                            fontSize = 20.sp
                                        )
                                    },
                                    border = null,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(2.dp)
                                )

                                TextField(
                                    value = item.text.value,
                                    onValueChange = { item.text.value = it },
                                    placeholder = { Text("Write something…", color = MaterialTheme.colorScheme.onSurface) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, end = 12.dp, top = 48.dp, bottom = 12.dp),
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
                                    onClick = { removeItem(index) },
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
                    Spacer(Modifier.height(56.dp))
                }
            }
        }
    }
}