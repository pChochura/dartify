package com.pointlessapps.dartify.local.datasource.game.x01.score.di

import com.pointlessapps.dartify.datasource.game.x01.score.ScoreDataSource
import com.pointlessapps.dartify.local.datasource.game.x01.score.LocalScoreDataSource
import com.pointlessapps.dartify.local.datasource.game.x01.score.OneThrowPossibleScoresCalculator
import com.pointlessapps.dartify.local.datasource.game.x01.score.ThreeThrowsPossibleScoresCalculator
import com.pointlessapps.dartify.local.datasource.game.x01.score.TwoThrowsPossibleScoresCalculator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val scoreModule = module {
    singleOf(::LocalScoreDataSource).bind(ScoreDataSource::class)
    singleOf(::OneThrowPossibleScoresCalculator)
    singleOf(::TwoThrowsPossibleScoresCalculator)
    singleOf(::ThreeThrowsPossibleScoresCalculator)
}
