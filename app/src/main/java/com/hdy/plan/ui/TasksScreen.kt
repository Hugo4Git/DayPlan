package com.hdy.plan.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.hdy.plan.domain.Task
import androidx.core.net.toUri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.KeyboardCapitalization
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.AlarmManager
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun TasksScreen(
    vm: TasksViewModel = viewModel(factory = TasksViewModel.Companion.factory()),
    onFabLongClick: () -> Unit = {}
) {
    val list by vm.items.collectAsStateWithLifecycle()
    val enabledReminders by vm.enabledReminders.collectAsStateWithLifecycle()
    var pickerForId by remember { mutableStateOf<Long?>(null) }
    var initialTime by remember { mutableStateOf(LocalTime.now()) }
    val context = LocalContext.current
    var pendingToggle by remember { mutableStateOf<Task?>(null) }

    val requestExactAlarmPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        pendingToggle?.let { task ->
            val am = ContextCompat.getSystemService(context, AlarmManager::class.java)
            val exactGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                    am?.canScheduleExactAlarms() == true
            if (exactGranted) {
                vm.toggleReminder(task)
            }
            pendingToggle = null
        }
    }

    val requestNotifPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        pendingToggle?.let { task ->
            if (!granted) {
                // User denied notifications
                pendingToggle = null
                return@let
            }
            // Notifications granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val am = ContextCompat.getSystemService(context, AlarmManager::class.java)
                val canExact = am?.canScheduleExactAlarms() == true
                if (!canExact) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                    requestExactAlarmPermission.launch(intent)
                    return@let
                }
            }
            vm.toggleReminder(task)
            pendingToggle = null
        }
    }

    val haptics = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    Scaffold(
        floatingActionButton = {
            FabWithLongClick(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    vm.add()
                },
                onLongClick = onFabLongClick
            )
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
                    .padding(horizontal = 24.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    },
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
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(start = 8.dp, top = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AssistChip(
                                            onClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                pickerForId = item.id
                                                initialTime = item.time
                                            },
                                            label = {
                                                Text(
                                                    text = item.time.format(
                                                        DateTimeFormatter.ofPattern("HH:mm")
                                                    ),
                                                    fontSize = 20.sp,
                                                )
                                            },
                                            border = null,
                                        )
                                        IconButton(
                                            onClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.ToggleOn)
                                                pendingToggle = item

                                                if (Build.VERSION.SDK_INT >= 33) {
                                                    val notifGranted = ContextCompat.checkSelfPermission(
                                                        context, Manifest.permission.POST_NOTIFICATIONS
                                                    ) == PackageManager.PERMISSION_GRANTED
                                                    if (!notifGranted) {
                                                        requestNotifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                        return@IconButton
                                                    }
                                                }

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    val am = ContextCompat.getSystemService(context, AlarmManager::class.java)
                                                    val canExact = am?.canScheduleExactAlarms() == true
                                                    if (!canExact) {
                                                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                                            data = "package:${context.packageName}".toUri()
                                                        }
                                                        requestExactAlarmPermission.launch(intent)
                                                        return@IconButton
                                                    }
                                                }

                                                vm.toggleReminder(item)
                                                pendingToggle = null
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (enabledReminders.contains(item.id))
                                                    Icons.Filled.Notifications
                                                else
                                                    Icons.Outlined.Notifications,
                                                contentDescription = "Toggle reminder"
                                            )
                                        }
                                    }

                                    var localText by rememberSaveable(item.id) { mutableStateOf(item.text) }
                                    LaunchedEffect(item.text) {
                                        localText = item.text
                                    }
                                    TextField(
                                        value = localText,
                                        onValueChange = { localText = it; vm.onEdit(item.id, it) },
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            capitalization = KeyboardCapitalization.Sentences
                                        ),
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
                                        onClick = {
                                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            vm.remove(item.id)
                                        },
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