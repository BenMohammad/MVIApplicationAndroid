package com.benmohammad.mviapp.mvibase

import io.reactivex.Observable

interface MviViewModel<I : MviIntent, S : MviViewState> {

    fun processIntents()
    fun states(): Observable<S>
}