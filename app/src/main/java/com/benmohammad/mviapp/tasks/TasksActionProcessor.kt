package com.benmohammad.mviapp.tasks

import com.benmohammad.mviapp.data.source.TasksRepository
import com.benmohammad.mviapp.tasks.TaskAction.*
import com.benmohammad.mviapp.tasks.TasksResult.*
import com.benmohammad.mviapp.util.pairWithDelay
import com.benmohammad.mviapp.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

class TasksActionProcessor(
    private val tasksRepository: TasksRepository,
    private val schedulerProvider: BaseSchedulerProvider
) {

    private val loadTaskProcessor =
        ObservableTransformer<LoadTasksAction, LoadTaskResult> {actions ->
            actions.flatMap { action ->
                tasksRepository.getTasks(action.forceUpdate)
                    .toObservable()
                    .map{ tasks -> LoadTaskResult.Success(tasks, action.filterType)}
                    .cast(LoadTaskResult::class.java)
                    .onErrorReturn(LoadTaskResult::Failure)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .startWith(LoadTaskResult.InFlight)
            }
        }


    private val activateTaskProcessor =
        ObservableTransformer<ActivateTaskAction, ActivateTaskResult> { actions ->
            actions.flatMap { action ->
                tasksRepository.activateTask(action.task)
                    .andThen(tasksRepository.getTasks())
                    .toObservable()
                    .flatMap { tasks ->
                        pairWithDelay(
                            ActivateTaskResult.Success(tasks),
                            ActivateTaskResult.HideUiNotification
                        )
                            .onErrorReturn(ActivateTaskResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .startWith(ActivateTaskResult.InFlight)
                    }
            }
        }


    private val completeTaskProcessor =
        ObservableTransformer<CompleteTaskAction, CompleteTaskResult> { actions ->
            actions.flatMap { action ->
                tasksRepository.completeTask(action.task)
                    .andThen(tasksRepository.getTasks())
                    .toObservable()
                    .flatMap { tasks ->
                        pairWithDelay(
                            CompleteTaskResult.Success(tasks),
                            CompleteTaskResult.HideUiNotification
                        )

                            .onErrorReturn(CompleteTaskResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .startWith(CompleteTaskResult.InFlight)
                    }
            }
        }

    private val clearCompletedTAskProcessor =
        ObservableTransformer<ClearCompletedTaskAction, ClearCompletedTasksResult> { actions ->
            actions.flatMap {
                tasksRepository.clearCompletedTask()
                    .andThen(tasksRepository.getTasks())
                    .toObservable()
                    .flatMap { tasks ->
                        pairWithDelay(
                            ClearCompletedTasksResult.Success(tasks),
                            ClearCompletedTasksResult.HideUiNotification
                        )

                            .onErrorReturn(ClearCompletedTasksResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .startWith(ClearCompletedTasksResult.InFlight)
                    }
            }
        }


    internal var actionProcessor =
        ObservableTransformer<TaskAction, TasksResult> { actions ->
            actions.publish { shared ->
                Observable.merge(
                    shared.ofType(LoadTasksAction::class.java).compose(loadTaskProcessor),
                    shared.ofType(ActivateTaskAction::class.java).compose(activateTaskProcessor),
                    shared.ofType(CompleteTaskAction::class.java).compose(completeTaskProcessor),
                    shared.ofType(ClearCompletedTaskAction::class.java).compose(clearCompletedTAskProcessor))
                    .mergeWith(
                        shared.filter { v ->
                            v !is LoadTasksAction
                                    && v !is ActivateTaskAction
                                    && v !is CompleteTaskAction
                                    && v !is ClearCompletedTaskAction
                        }.flatMap { w ->
                            Observable.error<TasksResult>(
                                IllegalArgumentException("Unknown action type..... $w")
                            )
                        }
                    )

            }
        }

}