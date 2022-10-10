package com.pointlessapps.dartify.domain.game.x01.di

import com.pointlessapps.dartify.domain.game.x01.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.ScoreRepositoryImpl
import com.pointlessapps.dartify.domain.game.x01.usecase.CalculateMaxNumberOfDoublesUseCase
import com.pointlessapps.dartify.domain.game.x01.usecase.CalculateMinNumberOfThrowsUseCase
import com.pointlessapps.dartify.domain.game.x01.usecase.ShouldAsForNumberOfDoublesUseCase
import com.pointlessapps.dartify.domain.game.x01.usecase.ValidateScoreUseCase
import org.koin.dsl.module

internal val gameX01Module = module {
    single<ScoreRepository> {
        ScoreRepositoryImpl(
            scoreDataSource = get(),
        )
    }

    factory {
        ValidateScoreUseCase(
            scoreRepository = get(),
        )
    }
    factory {
        ShouldAsForNumberOfDoublesUseCase(
            scoreRepository = get(),
        )
    }
    factory {
        CalculateMaxNumberOfDoublesUseCase(
            scoreRepository = get(),
        )
    }
    factory {
        CalculateMinNumberOfThrowsUseCase(
            scoreRepository = get(),
        )
    }
}
