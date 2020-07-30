package com.benmohammad.mviapp.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.benmohammad.mviapp.addedittask.AddEditTaskActionProcessorHolder
import com.benmohammad.mviapp.addedittask.AddEditTaskViewModel
import com.benmohammad.mviapp.injection.Injection
import com.benmohammad.mviapp.stats.StatisticsActionProcessorHolder
import com.benmohammad.mviapp.stats.StatisticsViewModel
import com.benmohammad.mviapp.taskdetail.TaskDetailActionProcessorHolder
import com.benmohammad.mviapp.taskdetail.TaskDetailViewModel
import com.benmohammad.mviapp.tasks.TasksActionProcessorHolder
import com.benmohammad.mviapp.tasks.TasksViewModel

class TodoViewModelFactory private constructor(
    private val applicationContext: Context
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass == TasksViewModel::class.java) {
            return TasksViewModel(
                TasksActionProcessorHolder(
                    Injection.provideTaskRepository(applicationContext),
                    Injection.provideSchedulerProvider())) as T
        }
        if(modelClass == TaskDetailViewModel::class.java) {
            return TaskDetailViewModel(
                TaskDetailActionProcessorHolder(
                    Injection.provideTaskRepository(applicationContext),
                    Injection.provideSchedulerProvider())) as T
        }
        if(modelClass == AddEditTaskViewModel::class.java) {
            return AddEditTaskViewModel(
                AddEditTaskActionProcessorHolder(
                    Injection.provideTaskRepository(applicationContext),
                    Injection.provideSchedulerProvider())) as T
        }

        if(modelClass == StatisticsViewModel::class.java) {
            return StatisticsViewModel(
                StatisticsActionProcessorHolder(
                    Injection.provideTaskRepository(applicationContext),
                    Injection.provideSchedulerProvider())) as T
        }


        throw IllegalArgumentException("unknown model class: $modelClass")
    }

    companion object : SingletonHolderSingleArg<TodoViewModelFactory, Context>(
        ::TodoViewModelFactory
    )
}