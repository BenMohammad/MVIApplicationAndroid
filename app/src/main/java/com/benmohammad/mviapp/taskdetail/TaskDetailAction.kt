package com.benmohammad.mviapp.taskdetail

import com.benmohammad.mviapp.mvibase.MviAction

sealed class TaskDetailAction: MviAction {

    data class PopulateTaskAction(val taskId: String): TaskDetailAction()
    data class DeleteTaskAction(val taskId: String): TaskDetailAction()
    data class ActivateTaskAction(val taskId: String): TaskDetailAction()
    data class CompleteTaskAction(val taskId: String): TaskDetailAction()
}