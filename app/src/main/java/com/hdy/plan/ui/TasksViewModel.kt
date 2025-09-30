package com.hdy.plan.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hdy.plan.AppGraph
import com.hdy.plan.domain.Task
import com.hdy.plan.domain.TasksRepository
import java.time.LocalTime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class TasksViewModel(
    private val repo: TasksRepository
) : ViewModel() {
    private val editJobs = mutableMapOf<Long, Job>()

    val items: StateFlow<List<Task>> =
        repo.observe().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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

    // --- Reminders state (UI) ---
    private val _enabledReminders = kotlinx.coroutines.flow.MutableStateFlow<Set<Long>>(emptySet())
    val enabledReminders: StateFlow<Set<Long>> = _enabledReminders

    fun toggleReminder(task: Task) {
        if (_enabledReminders.value.contains(task.id)) {
            cancelReminder(task.id)
        } else {
            scheduleReminder(task)
        }
    }

    private fun scheduleReminder(task: Task) {
        val now = java.time.LocalDateTime.now()
        var trigger = java.time.LocalDateTime.of(java.time.LocalDate.now(), task.time)
        // If time is in the past (or basically now), push to tomorrow to avoid instant fire.
        if (!trigger.isAfter(now.plusSeconds(5))) {
            trigger = trigger.plusDays(1)
        }
        val delayMillis = java.time.Duration.between(now, trigger).toMillis()

        val data = androidx.work.workDataOf(
            TaskReminderWorker.KEY_TASK_ID to task.id,
            TaskReminderWorker.KEY_TEXT to task.text,
            TaskReminderWorker.KEY_TIME to task.time.toString()
        )
        val req = androidx.work.OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delayMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(reminderTag(task.id))
            .build()

        androidx.work.WorkManager.getInstance(AppGraph.appContext)
            .enqueueUniqueWork(
                uniqueWorkName(task.id),
                androidx.work.ExistingWorkPolicy.REPLACE,
                req
            )
        _enabledReminders.value = _enabledReminders.value + task.id
    }

    private fun cancelReminder(id: Long) {
        androidx.work.WorkManager.getInstance(AppGraph.appContext)
            .cancelUniqueWork(uniqueWorkName(id))
        _enabledReminders.value = _enabledReminders.value - id
    }

    private fun uniqueWorkName(id: Long) = "task_reminder_$id"
    private fun reminderTag(id: Long) = "task_id:$id"

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

    // -------- Notification Worker ----------
    class TaskReminderWorker(
        appContext: android.content.Context,
        params: androidx.work.WorkerParameters
    ) : androidx.work.CoroutineWorker(appContext, params) {
        override suspend fun doWork(): Result {
            val id = inputData.getLong(KEY_TASK_ID, -1L)
            val text = inputData.getString(KEY_TEXT) ?: "Task reminder"
            val time = inputData.getString(KEY_TIME) ?: ""

            // Create notification channel (id must be stable)
            val channelId = "task_reminders"
            val nm = applicationContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE)
                    as android.app.NotificationManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val ch = android.app.NotificationChannel(
                    channelId,
                    "Task reminders",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Notifications for scheduled tasks" }
                nm.createNotificationChannel(ch)
            }

            val notif = androidx.core.app.NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Reminder ${if (time.isNotEmpty()) "($time)" else ""}")
                .setContentText(text.ifBlank { "Don’t forget your task." })
                .setAutoCancel(true)
                .setContentIntent(
                    applicationContext.packageManager
                        .getLaunchIntentForPackage(applicationContext.packageName)
                        ?.let { launch ->
                            launch.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            androidx.core.app.TaskStackBuilder.create(applicationContext)
                                .addNextIntentWithParentStack(launch)
                                .getPendingIntent(
                                    id.toInt(),
                                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                                )
                        }
                )
                .build()

            nm.notify(id.toInt(), notif)
            return Result.success()
        }

        companion object {
            const val KEY_TASK_ID = "task_id"
            const val KEY_TEXT = "task_text"
            const val KEY_TIME = "task_time"
        }
    }
}

