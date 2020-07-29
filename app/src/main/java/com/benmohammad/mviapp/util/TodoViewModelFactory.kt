package com.benmohammad.mviapp.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.benmohammad.mviapp.injection.Injection
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

        throw IllegalArgumentException("unknown model class: $modelClass")
    }

    companion object : SingletonHolderSingleArg<TodoViewModelFactory, Context>(
        ::TodoViewModelFactory
    )
}