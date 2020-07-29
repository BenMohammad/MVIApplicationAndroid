package com.benmohammad.mviapp.data.source.remote

import com.benmohammad.mviapp.data.Task
import com.benmohammad.mviapp.data.source.TasksDataSource
import io.reactivex.Completable
import io.reactivex.Single

object TasksRemoteDataSource: TasksDataSource {


    override fun getTasks(): Single<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun getTask(taskID: String): Single<Task> {
        TODO("Not yet implemented")
    }

    override fun saveTask(task: Task): Completable {
        TODO("Not yet implemented")
    }

    override fun completeTask(task: Task): Completable {
        TODO("Not yet implemented")
    }

    override fun completeTask(taskId: String): Completable {
        TODO("Not yet implemented")
    }

    override fun activateTask(task: Task): Completable {
        TODO("Not yet implemented")
    }

    override fun activateTask(taskId: String): Completable {
        TODO("Not yet implemented")
    }

    override fun clearCompletedTask(): Completable {
        TODO("Not yet implemented")
    }

    override fun refreshTasks() {
        TODO("Not yet implemented")
    }

    override fun deleteAllTask() {
        TODO("Not yet implemented")
    }

    override fun deleteTask(taskId: String): Completable {
        TODO("Not yet implemented")
    }
}