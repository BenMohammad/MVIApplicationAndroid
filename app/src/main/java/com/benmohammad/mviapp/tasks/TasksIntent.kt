package com.benmohammad.mviapp.tasks


import com.benmohammad.mviapp.data.Task
import com.benmohammad.mviapp.mvibase.MviIntent

sealed class TasksIntent: MviIntent {

    object InitialIntent: TasksIntent()

    data class RefreshIntent(val forceUpdate: Boolean): TasksIntent()
    data class ActivateTaskIntent(val task: Task): TasksIntent()
    data class CompleteTaskIntent(val task: Task): TasksIntent()
    object ClearCompletedTaskIntent: TasksIntent()
    data class ChangeFilterIntent(val filterType: TasksFilterType): TasksIntent()
}