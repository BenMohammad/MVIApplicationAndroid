package com.benmohammad.mviapp.stats

import com.benmohammad.mviapp.data.Task
import com.benmohammad.mviapp.data.source.TasksRepository
import com.benmohammad.mviapp.stats.StatisticsAction.LoadStatisticsAction
import com.benmohammad.mviapp.stats.StatisticsResult.LoadStatisticsResult
import com.benmohammad.mviapp.util.flatMapIterable
import com.benmohammad.mviapp.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.lang.IllegalArgumentException

class StatisticsActionProcessorHolder(
    private val tasksRepository: TasksRepository,
    private val schedulerProvider: BaseSchedulerProvider
) {

    private val loadStatisticsProcessor =
        ObservableTransformer<LoadStatisticsAction, LoadStatisticsResult> {
            actions ->
            actions.flatMap {
                tasksRepository.getTasks()
                    .flatMapIterable()
                    .publish<LoadStatisticsResult.Success>{shared ->
                        Single.zip<Int, Int, LoadStatisticsResult.Success>(
                            shared.filter(Task::active).count().map(Long::toInt),
                            shared.filter(Task::completed).count().map(Long::toInt),
                            BiFunction{activeCount, completedCount ->
                                LoadStatisticsResult.Success(activeCount, completedCount)
                            }
                        ).toObservable()
                    }
                    .cast(LoadStatisticsResult::class.java)
                    .onErrorReturn(LoadStatisticsResult::Failure)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .startWith(LoadStatisticsResult.InFlight)
            }
        }

    var actionProcessor =
        ObservableTransformer<StatisticsAction, StatisticsResult> { actions ->
            actions.publish {
                shared ->
                shared.ofType(LoadStatisticsAction::class.java).compose(loadStatisticsProcessor)
                    .cast(StatisticsResult::class.java)
                    .mergeWith(
                        shared.filter { v -> v !is LoadStatisticsAction }
                            .flatMap { w ->
                                Observable.error<LoadStatisticsResult>(
                                    IllegalArgumentException("Unknown Action type: $w")
                                )
                            }
                    )
            }
        }
}