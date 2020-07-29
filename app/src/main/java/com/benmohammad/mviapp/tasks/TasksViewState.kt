package com.benmohammad.mviapp.tasks

import com.benmohammad.mviapp.data.Task
import com.benmohammad.mviapp.mvibase.MviView
import com.benmohammad.mviapp.mvibase.MviViewState

data class TasksViewState(
    val isLoading: Boolean,
    val tasksFilterType: TasksFilterType,
    val tasks: List<Task>,
    val error: Throwable?,
    val uiNotification: UiNotification?
): MviViewState {

    enum class UiNotification {
        TASK_COMPLETE,
        TASK_ACTIVATED,
        COMPLETE_TASKS_CLEARED,
    }

    companion object {
        fun idle(): TasksViewState {
            return TasksViewState(
                isLoading = false,
                tasksFilterType = TasksFilterType.ALL_TASKS,
                tasks = emptyList(),
                error = null,
                uiNotification = null
            )
        }
    }
}