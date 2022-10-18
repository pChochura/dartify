package com.pointlessapps.dartify.domain.game.x01.score.di

import com.pointlessapps.dartify.domain.game.x01.score.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.score.ScoreRepositoryImpl
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
            turnRepository = get(),
        )
    }
    factory {
        IsCheckoutPossibleUseCase(
            scoreRepository = get(),
        )
    }
}
