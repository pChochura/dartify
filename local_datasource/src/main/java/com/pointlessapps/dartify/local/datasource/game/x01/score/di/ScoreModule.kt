package com.pointlessapps.dartify.local.datasource.game.x01.score.di

import com.pointlessapps.dartify.datasource.game.x01.score.ScoreDataSource
import com.pointlessapps.dartify.local.datasource.game.x01.score.LocalScoreDataSource
import com.pointlessapps.dartify.local.datasource.game.x01.score.OneThrowPossibleScoresCalculator
import com.pointlessapps.dartify.local.datasource.game.x01.score.ThreeThrowsPossibleScoresCalculator
import com.pointlessapps.dartify.local.datasource.game.x01.score.TwoThrowsPossibleScoresCalculator
import org.koin.dsl.module

internal val scoreModule = module {
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
