package com.hdy.plan.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id, timeMinutes")
    fun observeAll(): Flow<List<TaskEntity>>

    @Insert
    suspend fun insert(entity: TaskEntity): Long

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE tasks SET text = :text WHERE id = :id")
    suspend fun updateText(id: Long, text: String)

    @Query("UPDATE tasks SET timeMinutes = :minutes WHERE id = :id")
    suspend fun updateTime(id: Long, minutes: Int)
}
