package com.benmohammad.mviapp.tasks

import com.benmohammad.mviapp.data.Task
import com.benmohammad.mviapp.mvibase.MviAction

sealed class TaskAction : MviAction {

    data class LoadTasksAction(
        val forceUpdate: Boolean,
        val filterType: TasksFilterType?
    ): TaskAction()

    data class ActivateTAskAction(val task: Task): TaskAction()
    data class CompleteTAskAction(val task: Task): TaskAction()
    object ClearCompletedTAskAction: TaskAction()
}