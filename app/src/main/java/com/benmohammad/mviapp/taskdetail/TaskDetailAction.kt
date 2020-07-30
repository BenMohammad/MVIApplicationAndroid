package com.benmohammad.mviapp.taskdetail

import com.benmohammad.mviapp.mvibase.MviAction

sealed class TaskDetailAction: MviAction {

    data class PopulateTAsAction(val taskId: String): TaskDetailAction()
    data class DeleteTaskAction(val taskId: String): TaskDetailAction()
    data class ActivateTaskAction(val taskId: String): TaskDetailAction()
    data class CompleteTAction(val taskId: String): TaskDetailAction()
}