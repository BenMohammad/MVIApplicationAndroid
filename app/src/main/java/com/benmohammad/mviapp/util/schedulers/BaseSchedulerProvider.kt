package com.benmohammad.mviapp.util.schedulers

import io.reactivex.Scheduler

interface BaseSchedulerProvider {

    fun computation(): Scheduler
    fun io(): Scheduler
    fun ui(): Scheduler
}