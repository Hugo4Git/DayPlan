package com.hdy.plan

import android.content.Context
import androidx.room.Room
import com.hdy.plan.data.AppDb
import com.hdy.plan.data.TasksRepositoryImpl
import com.hdy.plan.domain.TasksRepository

object AppGraph {
    private lateinit var appContext: Context
    private val db: AppDb by lazy {
        Room.databaseBuilder(appContext, AppDb::class.java, "app.db").build()
    }

    val tasksRepo: TasksRepository by lazy { TasksRepositoryImpl(db.taskDao()) }

    fun init(context: Context) { appContext = context.applicationContext }
}
