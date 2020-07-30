package com.benmohammad.mviapp.stats

import com.benmohammad.mviapp.mvibase.MviAction

sealed class StatisticsAction: MviAction {

    object LoadStatisticsAction: StatisticsAction()
}