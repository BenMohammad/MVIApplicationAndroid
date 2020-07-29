package com.benmohammad.mviapp.tasks

import androidx.lifecycle.ViewModel
import com.benmohammad.mviapp.data.Task
import com.benmohammad.mviapp.mvibase.MviViewModel
import com.benmohammad.mviapp.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class TasksViewModel (
    private val actionProcessorHolder: TasksActionProcessorHolder
): ViewModel(), MviViewModel<TasksIntent, TasksViewState> {

    private val intentSubject: PublishSubject<TasksIntent> = PublishSubject.create()
    private val statesObservable: Observable<TasksViewState> = compose()
    private val disposables = CompositeDisposable()

    private val intentFilter: ObservableTransformer<TasksIntent, TasksIntent>
    get() = ObservableTransformer { intents ->
        intents.publish { shared ->
            Observable.merge(
                shared.ofType(TasksIntent.InitialIntent::class.java).take(1),
                shared.notOfType(TasksIntent.InitialIntent::class.java)
            )
        }
    }

    override fun processIntents(intents: Observable<TasksIntent>) {
        disposables.addAll(intents.subscribe(intentSubject::onNext))
    }

    override fun states(): Observable<TasksViewState> = statesObservable

    private fun compose(): Observable<TasksViewState> {
        return intentSubject
            .compose(intentFilter)
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(TasksViewState.idle(), reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: TasksIntent): TaskAction {
        return when (intent) {
            is TasksIntent.InitialIntent -> TaskAction.LoadTasksAction(true, TasksFilterType.ALL_TASKS)
            is TasksIntent.RefreshIntent -> TaskAction.LoadTasksAction(intent.forceUpdate, null)
            is TasksIntent.ActivateTaskIntent -> TaskAction.ActivateTaskAction(intent.task)
            is TasksIntent.CompleteTaskIntent -> TaskAction.CompleteTaskAction(intent.task)
            is TasksIntent.ClearCompletedTaskIntent -> TaskAction.ClearCompletedTaskAction
            is TasksIntent.ChangeFilterIntent -> TaskAction.LoadTasksAction(false, intent.filterType)
        }
    }

    override fun onCleared() {
        disposables.dispose()
    }

    companion object {
        private val reducer = BiFunction { previousState: TasksViewState, result: TasksResult ->
            when (result) {
                is TasksResult.LoadTaskResult -> when (result) {
                    is TasksResult.LoadTaskResult.Success -> {
                        val filterType = result.filterType ?: previousState.tasksFilterType
                        val tasks = filterTasks(result.tasks, filterType)
                        previousState.copy(
                            isLoading = false,
                            tasks = tasks,
                            tasksFilterType = filterType
                        )
                    }
                    is TasksResult.LoadTaskResult.Failure -> previousState.copy(isLoading = false, error = result.error)
                    is TasksResult.LoadTaskResult.InFlight -> previousState.copy(isLoading = true)
                }
                is TasksResult.CompleteTaskResult -> when(result) {
                    is TasksResult.CompleteTaskResult.Success ->
                        previousState.copy(
                            uiNotification = TasksViewState.UiNotification.TASK_COMPLETE,
                            tasks = filterTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is TasksResult.CompleteTaskResult.Failure -> previousState.copy(error = result.error)
                    is TasksResult.CompleteTaskResult.InFlight -> previousState
                    is TasksResult.CompleteTaskResult.HideUiNotification ->
                        if(previousState.uiNotification == TasksViewState.UiNotification.TASK_COMPLETE) {
                            previousState.copy(uiNotification = null)
                        } else {
                            previousState
                        }
                }
                is TasksResult.ActivateTaskResult -> when (result) {
                    is TasksResult.ActivateTaskResult.Success ->
                        previousState.copy(
                            uiNotification = TasksViewState.UiNotification.TASK_ACTIVATED,
                            tasks = filterTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is TasksResult.ActivateTaskResult.Failure -> previousState.copy(error = result.error)
                    is TasksResult.ActivateTaskResult.InFlight -> previousState
                    is TasksResult.ActivateTaskResult.HideUiNotification ->
                        if(previousState.uiNotification == TasksViewState.UiNotification.TASK_ACTIVATED) {
                            previousState.copy(uiNotification = null)
                        } else {
                            previousState
                        }
                }
                is TasksResult.ClearCompletedTasksResult -> when (result) {
                    is TasksResult.ClearCompletedTasksResult.Success ->
                        previousState.copy(
                            uiNotification = TasksViewState.UiNotification.COMPLETE_TASKS_CLEARED,
                            tasks = filterTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is TasksResult.ClearCompletedTasksResult.Failure -> previousState.copy(error = result.error)
                    is TasksResult.ClearCompletedTasksResult.InFlight -> previousState
                    is TasksResult.ClearCompletedTasksResult.HideUiNotification ->
                        if(previousState.uiNotification == TasksViewState.UiNotification.COMPLETE_TASKS_CLEARED) {
                            previousState.copy(uiNotification = null)
                        } else {
                            previousState
                        }
                }
            }
        }
    }
}


        private fun filterTasks(
            tasks: List<Task>,
            filterType: TasksFilterType
        ): List<Task> {
            return when (filterType) {
                TasksFilterType.ALL_TASKS -> tasks
                TasksFilterType.ACTIVE_TASKS -> tasks.filter(Task::active)
                TasksFilterType.COMPLETE_TASK -> tasks.filter(Task::completed)
            }
        }
