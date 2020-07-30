package com.benmohammad.mviapp.stats

import com.benmohammad.mviapp.mvibase.MviIntent

sealed class StatisticsIntent: MviIntent {

    object InitialIntent: StatisticsIntent()
}