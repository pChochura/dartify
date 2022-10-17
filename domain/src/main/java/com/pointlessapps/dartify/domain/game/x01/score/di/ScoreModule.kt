package com.pointlessapps.dartify.domain.game.x01.score.di

import com.pointlessapps.dartify.domain.game.x01.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.ScoreRepositoryImpl
import com.pointlessapps.dartify.domain.game.x01.score.usecase.*
import org.koin.dsl.module

internal val scoreModule = module {
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
        CalculateMaxNumberOfDoublesForThreeThrowsUseCase(
            scoreRepository = get(),
        )
    }
    factory {
        CalculateMinNumberOfThrowsUseCase(
            scoreRepository = get(),
        )
    }
    factory {
        IsCheckoutPossibleUseCase(
            scoreRepository = get(),
        )
    }
}
