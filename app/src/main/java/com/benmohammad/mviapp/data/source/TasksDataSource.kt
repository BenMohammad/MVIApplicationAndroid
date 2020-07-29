package com.benmohammad.mviapp.data.source

import com.benmohammad.mviapp.data.Task
import com.benmohammad.mviapp.data.source.local.TasksPersistenceContract
import io.reactivex.Completable
import io.reactivex.Single

interface TasksDataSource {

    fun getTasks(forceUpdate: Boolean): Single<List<Task>> {
        if(forceUpdate) refreshTasks()
        return getTasks()
    }


    fun getTasks(): Single<List<Task>>

    fun getTask(taskID: String): Single<Task>

    fun saveTask(task: Task): Completable

    fun completeTask(task: Task): Completable

    fun completeTask(taskId: String): Completable

    fun activateTask(task: Task): Completable

    fun activateTask(taskId: String): Completable

    fun clearCompletedTask(): Completable

    fun refreshTasks()

    fun deleteAllTask()

    fun deleteTask(taskId: String): Completable
}