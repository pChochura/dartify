package com.pointlessapps.dartify.local.datasource.game.x01.di

import com.pointlessapps.dartify.datasource.game.x01.ScoreDataSource
import com.pointlessapps.dartify.local.datasource.game.x01.LocalScoreDataSource
import com.pointlessapps.dartify.local.datasource.game.x01.OneThrowPossibleScoresCalculator
import com.pointlessapps.dartify.local.datasource.game.x01.ThreeThrowsPossibleScoresCalculator
import com.pointlessapps.dartify.local.datasource.game.x01.TwoThrowsPossibleScoresCalculator
import org.koin.dsl.module

internal val gameX01Module = module {
    single<ScoreDataSource> {
        LocalScoreDataSource(
            oneThrowPossibleScoresCalculator = get(),
            twoThrowsPossibleScoresCalculator = get(),
            threeThrowsPossibleScoresCalculator = get(),
        )
    }

    single { OneThrowPossibleScoresCalculator() }
    single {
        TwoThrowsPossibleScoresCalculator(
            oneThrowPossibleScoresCalculator = get(),
        )
    }
    single {
        ThreeThrowsPossibleScoresCalculator(
            oneThrowPossibleScoresCalculator = get(),
            twoThrowsPossibleScoresCalculator = get(),
        )
    }
}
