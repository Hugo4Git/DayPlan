package com.hdy.plan.domain

import java.time.LocalTime
import kotlinx.coroutines.flow.Flow

data class Task(
    val id: Long,
    val text: String,
    val time: LocalTime
)

interface TasksRepository {
    fun observe(): Flow<List<Task>>
    suspend fun add(emptyText: String = "", time: LocalTime = LocalTime.now())
    suspend fun remove(id: Long)
    suspend fun updateText(id: Long, text: String)
    suspend fun updateTime(id: Long, time: LocalTime)
}
