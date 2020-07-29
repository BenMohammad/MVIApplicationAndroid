package com.benmohammad.mviapp.data.source

import androidx.annotation.VisibleForTesting
import com.benmohammad.mviapp.data.Task
import com.benmohammad.mviapp.util.SingletonHolderDoubleArg
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

open class TasksRepository private constructor(
    private val tasksRemoteDataSource: TasksDataSource,
    private val taskLocalDataSource: TasksDataSource
): TasksDataSource {

    @VisibleForTesting
    var cachedTasks: MutableMap<String, Task>? = null

    @VisibleForTesting
    var cacheIsDirty = false


    private fun getAndCacheLocalTasks(): Single<List<Task>> {
        return taskLocalDataSource.getTasks()
            .flatMap { tasks ->
                Observable.fromIterable(tasks)
                    .doOnNext{task -> cachedTasks!!.put(task.id, task)}
                    .toList()
            }
    }

    private fun getAndSaveRemoteTasks(): Single<List<Task>> {
        return tasksRemoteDataSource.getTasks()
            .flatMap { tasks ->
                Observable.fromIterable(tasks)
                    .doOnNext { task ->
                        taskLocalDataSource.saveTask(task)
                        cachedTasks!!.put(task.id, task)
                    }.toList()
            }
            .doOnSuccess { cacheIsDirty = false }
    }



    override fun getTasks(): Single<List<Task>> {
        if(cachedTasks != null && !cacheIsDirty) {
            return Observable.fromIterable(cachedTasks!!.values).toList()
        } else if(cachedTasks == null) {
            cachedTasks = LinkedHashMap()
        }

        val remoteTasks = getAndSaveRemoteTasks()

        return if(cacheIsDirty) {
            remoteTasks
        } else {
            val localTasks = getAndCacheLocalTasks()
            Single.concat(localTasks, remoteTasks)
                .filter{tasks -> !tasks.isEmpty()}
                .firstOrError()
        }
    }

    override fun getTask(taskId: String): Single<Task> {
        val cachedTask = getTaskWithId(taskId)
        if(cachedTask != null) {
            return Single.just(cachedTask)
        }

        val localTask = getTaskWithIdFromLocalRepository(taskId)
        val remoteTask = tasksRemoteDataSource.getTask(taskId)
            .doOnSuccess { task ->
                taskLocalDataSource.saveTask(task)
                cachedTasks!!.put(task.id, task)
            }
        return Single.concat(localTask, remoteTask).firstOrError()
    }

    private fun getTaskWithIdFromLocalRepository(taskId: String): Single<Task> {
        return taskLocalDataSource.getTask(taskId)
            .doOnSuccess { task -> cachedTasks!!.put(taskId, task)}
    }

    private fun getTaskWithId(id: String): Task? = cachedTasks?.get(id)

    override fun saveTask(task: Task): Completable {
        tasksRemoteDataSource.saveTask(task)
        taskLocalDataSource.saveTask(task)

        if(cachedTasks == null) {
            cachedTasks = LinkedHashMap()
        }

        cachedTasks!!.put(task.id, task)
        return Completable.complete()
    }

    override fun completeTask(task: Task): Completable {
        tasksRemoteDataSource.completeTask(task)
        taskLocalDataSource.completeTask(task)

        val completedTask = Task(title = task.title!!, description = task.description, id = task.id, completed = true)

        if(cachedTasks == null) {
            cachedTasks = LinkedHashMap()
        }

        cachedTasks!!.put(task.id, completedTask)
        return Completable.complete()
    }

    override fun completeTask(taskId: String): Completable {
        val taskWithId = getTaskWithId(taskId)
        return if (taskWithId != null) {
            completeTask(taskWithId)
        } else {
            Completable.complete()
        }
    }

    override fun activateTask(task: Task): Completable {
        tasksRemoteDataSource.activateTask(task)
        taskLocalDataSource.activateTask(task)

        val activeTask = Task(title = task.title!!, description = task.description, id = task.id, completed = false)

        if(cachedTasks == null) {
            cachedTasks = LinkedHashMap()
        }

        cachedTasks!!.put(task.id, activeTask)
        return Completable.complete()
    }

    override fun activateTask(taskId: String): Completable {
        val taskWithId = getTaskWithId(taskId)
        return if (taskWithId != null) {
            activateTask(taskWithId)
        } else {
            Completable.complete()
        }
    }

    override fun clearCompletedTask(): Completable {
        tasksRemoteDataSource.clearCompletedTask()
        taskLocalDataSource.clearCompletedTask()

        if(cachedTasks == null) {
            cachedTasks = LinkedHashMap()
        }

        val it = cachedTasks!!.entries.iterator()
        while(it.hasNext()) {
            val entry = it.next()
            if(entry.value.completed) {
                it.remove()
            }
        }
        return Completable.complete()
    }

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun deleteAllTask() {
        tasksRemoteDataSource.deleteAllTask()
        taskLocalDataSource.deleteAllTask()

        if(cachedTasks == null) {
            cachedTasks = LinkedHashMap()
        }

        cachedTasks!!.clear()
    }

    override fun deleteTask(taskId: String): Completable {
        tasksRemoteDataSource.deleteTask(checkNotNull(taskId))
        taskLocalDataSource.deleteTask(checkNotNull(taskId))

        cachedTasks!!.remove(taskId)
        return Completable.complete()
    }

    companion object: SingletonHolderDoubleArg<TasksRepository, TasksDataSource, TasksDataSource>(
        ::TasksRepository
    )

}