package com.benmohammad.mviapp.injection

import android.content.Context
import com.benmohammad.mviapp.data.source.TasksRepository
import com.benmohammad.mviapp.data.source.local.TasksLocalDataSource
import com.benmohammad.mviapp.data.source.remote.TasksRemoteDataSource
import com.benmohammad.mviapp.util.schedulers.BaseSchedulerProvider
import com.benmohammad.mviapp.util.schedulers.SchedulerProvider

object Injection {

    fun provideTaskRepository(context: Context): TasksRepository {
        return TasksRepository.getInstance(
            TasksRemoteDataSource,
            TasksLocalDataSource.getInstance(context, provideSchedulerProvider())
        )
    }

    fun provideSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider
}