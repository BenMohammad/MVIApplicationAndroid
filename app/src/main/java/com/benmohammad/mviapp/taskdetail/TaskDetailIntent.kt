package com.benmohammad.mviapp.taskdetail

import com.benmohammad.mviapp.mvibase.MviIntent

sealed class TaskDetailIntent: MviIntent {

    data class InitialIntent(val taskId: String): TaskDetailIntent()
    data class DeleteTaskIntent(val taskId: String): TaskDetailIntent()
    data class ActivateTaskIntent(val taskId: String): TaskDetailIntent()
    data class CompleteTaskIntent(val taskId: String): TaskDetailIntent()
}