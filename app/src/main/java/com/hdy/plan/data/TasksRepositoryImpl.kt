package com.hdy.plan.data

import com.hdy.plan.domain.Task
import com.hdy.plan.domain.TasksRepository
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private fun LocalTime.toMinutes() = hour * 60 + minute
private fun minutesToLocalTime(mins: Int) = LocalTime.of(mins / 60, mins % 60)

class TasksRepositoryImpl(
    private val dao: TaskDao
) : TasksRepository {
    override fun observe(): Flow<List<Task>> =
        dao.observeAll().map { list ->
            list.map { Task(it.id, it.text, minutesToLocalTime(it.timeMinutes)) }
        }

    override suspend fun add(emptyText: String, time: LocalTime) {
        dao.insert(TaskEntity(text = emptyText, timeMinutes = time.toMinutes()))
    }

    override suspend fun remove(id: Long) {
        dao.delete(id)
    }

    override suspend fun updateText(id: Long, text: String) {
        dao.updateText(id, text)
    }

    override suspend fun updateTime(id: Long, time: LocalTime) {
        dao.updateTime(id, time.toMinutes())
    }
}
