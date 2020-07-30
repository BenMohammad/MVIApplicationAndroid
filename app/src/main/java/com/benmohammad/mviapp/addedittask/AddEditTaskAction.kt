package com.benmohammad.mviapp.addedittask

import com.benmohammad.mviapp.mvibase.MviAction

sealed class AddEditTaskAction: MviAction {

    data class PopulateTAskAction(val taskId: String): AddEditTaskAction()

    data class CreateTAskAction(val title: String, val description: String): AddEditTaskAction()

    data class UpdateTAskAction(
        val taskId: String,
        val title: String,
        val description: String
    ): AddEditTaskAction()

    object SkipMe : AddEditTaskAction()
}