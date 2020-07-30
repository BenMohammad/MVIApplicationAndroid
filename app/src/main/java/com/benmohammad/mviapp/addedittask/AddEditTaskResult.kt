package com.benmohammad.mviapp.addedittask

import com.benmohammad.mviapp.data.Task
import com.benmohammad.mviapp.mvibase.MviResult

sealed class AddEditTaskResult: MviResult {

    sealed class PopulateTAskResult: AddEditTaskResult() {
        data class Success(val task: Task): PopulateTAskResult()
        data class Failure(val error: Throwable): PopulateTAskResult()
        object InFlight: PopulateTAskResult()
    }

    sealed class CreateTaskResult: AddEditTaskResult() {
        object Success: CreateTaskResult()
        object Empty: CreateTaskResult()
    }

    object UpdateTaskResult: AddEditTaskResult()
}