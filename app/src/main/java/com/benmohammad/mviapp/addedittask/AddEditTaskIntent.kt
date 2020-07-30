package com.benmohammad.mviapp.addedittask

import com.benmohammad.mviapp.mvibase.MviIntent

sealed class AddEditTaskIntent: MviIntent {

    data class  InitialIntent(val taskId: String?): AddEditTaskIntent()

    data class SaveTask(
        val taskId: String?,
        val title: String,
        val description: String
    ): AddEditTaskIntent()
}