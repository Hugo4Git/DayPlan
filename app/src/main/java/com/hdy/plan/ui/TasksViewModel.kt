package com.hdy.plan.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hdy.plan.AppGraph
import com.hdy.plan.domain.Task
import com.hdy.plan.domain.TasksRepository
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class TasksViewModel(
    private val repo: TasksRepository
) : ViewModel() {
    private val editJobs = mutableMapOf<Long, Job>()

    val items: StateFlow<List<Task>> =
        repo.observe().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _enabledReminders: MutableStateFlow<Set<Long>> = MutableStateFlow(emptySet())
    val enabledReminders: StateFlow<Set<Long>> = _enabledReminders

    init {
        // updating UI bell state
        viewModelScope.launch {
            items.collect { current ->
                runCatching { refreshEnabledFromAlarms(current.map { it.id }) }
                    .onFailure { e ->
                        Log.e("TasksViewModel", "Failed refreshing alarms", e)
                    }
            }
        }
    }

    fun add() = viewModelScope.launch { repo.add() }
    fun remove(id: Long) = viewModelScope.launch {
        // Cancel any scheduled reminder for this task
        cancelReminder(id)
        repo.remove(id)
    }
    fun updateText(id: Long, text: String) = viewModelScope.launch { repo.updateText(id, text) }
    fun updateTime(id: Long, time: LocalTime) = viewModelScope.launch {
        repo.updateTime(id, time)

        // Reschedule the reminder
        if (enabledReminders.value.contains(id)) {
            if (enabledReminders.value.contains(id)) {
                items.value.find { it.id == id }?.let { current ->
                    scheduleReminder(current.copy(time = time))
                }
            }
        }
    }

    fun toggleReminder(task: Task) {
        if (_enabledReminders.value.contains(task.id)) {
            cancelReminder(task.id)
        } else {
            scheduleReminder(task)
        }
    }

    private fun scheduleReminder(task: Task) {
        // ensure single alarm by cancelling any existing first
        cancelReminder(task.id)

        val now = java.time.LocalDateTime.now()
        var trigger = java.time.LocalDateTime.of(java.time.LocalDate.now(), task.time)
        if (!trigger.isAfter(now.plusSeconds(5))) {
            trigger = trigger.plusDays(1)
        }
        val triggerAtMillis = trigger
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val ctx = AppGraph.appContext
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(ctx, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.KEY_TASK_ID, task.id)
            putExtra(TaskReminderReceiver.KEY_TEXT, task.text)
            putExtra(TaskReminderReceiver.KEY_TIME, task.time.toString())
        }
        val pi = PendingIntent.getBroadcast(
            ctx,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // API 31+: check exact-alarm capability; fall back to inexact if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            var canExact = false
            try {
                canExact = am.canScheduleExactAlarms()
                if (canExact) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
                } else {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
                }
            } catch (se: SecurityException) {
                // As a safety net, avoid crashing and still schedule an inexact alarm
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
            }
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
        }

        _enabledReminders.value = _enabledReminders.value + task.id
    }

    private fun cancelReminder(id: Long) {
        val ctx = AppGraph.appContext
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getBroadcast(
            ctx,
            id.toInt(),
            Intent(ctx, TaskReminderReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pi != null) {
            am.cancel(pi)
            pi.cancel()
        }
        _enabledReminders.value = _enabledReminders.value - id
    }

    private fun refreshEnabledFromAlarms(ids: List<Long>) {
        val ctx = AppGraph.appContext
        val enabled = buildSet {
            ids.forEach { id ->
                val pi = PendingIntent.getBroadcast(
                    ctx,
                    id.toInt(),
                    Intent(ctx, TaskReminderReceiver::class.java),
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pi != null) add(id)
            }
        }
        _enabledReminders.value = enabled
    }

    companion object {
        fun factory() = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TasksViewModel(AppGraph.tasksRepo) as T
            }
        }
    }

    fun onEdit(id: Long, text: String, delayMs: Long = 500) {
        editJobs[id]?.cancel()
        editJobs[id] = viewModelScope.launch {
            delay(delayMs)
            repo.updateText(id, text)
            if (enabledReminders.value.contains(id)) {
                items.value.find { it.id == id }?.let { task ->
                    scheduleReminder(task.copy(text = text))
                }
            }
        }
    }

    override fun onCleared() {
        editJobs.values.forEach { it.cancel() }
        super.onCleared()
    }
}

class TaskReminderReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(KEY_TASK_ID, -1L)
        val text = intent.getStringExtra(KEY_TEXT) ?: "Task reminder"
        val time = intent.getStringExtra(KEY_TIME) ?: ""

        val channelId = "task_reminders"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = android.app.NotificationChannel(
                channelId, "Task reminders",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Notifications for scheduled tasks" }
            nm.createNotificationChannel(ch)
        }

        val contentIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP) }
            ?.let { launch ->
                androidx.core.app.TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(launch)
                    .getPendingIntent(
                        id.toInt(),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
            }

        val notif = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Reminder ${if (time.isNotEmpty()) "($time)" else ""}")
            .setContentText(text.ifBlank { "Don’t forget your task." })
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        nm.notify(id.toInt(), notif)
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_TEXT = "task_text"
        const val KEY_TIME = "task_time"
    }
}



