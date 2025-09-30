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
    fun remove(id: Long) = viewModelScope.launch { repo.remove(id) }
    fun updateText(id: Long, text: String) = viewModelScope.launch { repo.updateText(id, text) }
    fun updateTime(id: Long, time: LocalTime) = viewModelScope.launch { repo.updateTime(id, time) }

    companion object {
        fun factory() = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TasksViewModel(AppGraph.tasksRepo) as T
            }
        }
    }

    fun onEdit(id: Long, text: String, delayMs: Long = 250) {
        editJobs[id]?.cancel()
        editJobs[id] = viewModelScope.launch {
            delay(delayMs)
            repo.updateText(id, text)
        }
    }

    override fun onCleared() {
        editJobs.values.forEach { it.cancel() }
        super.onCleared()
    }
}

