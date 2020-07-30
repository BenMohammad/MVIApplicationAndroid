package com.benmohammad.mviapp.addedittask

import com.benmohammad.mviapp.mvibase.MviAction

sealed class AddEditTaskAction: MviAction {

    data class PopulateTaskAction(val taskId: String): AddEditTaskAction()

    data class CreateTaskAction(val title: String, val description: String): AddEditTaskAction()

    data class UpdateTaskAction(
        val taskId: String,
        val title: String,
        val description: String
    ): AddEditTaskAction()

    object SkipMe : AddEditTaskAction()
}