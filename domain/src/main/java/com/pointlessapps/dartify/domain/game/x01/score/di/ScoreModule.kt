package com.pointlessapps.dartify.domain.game.x01.score.di

import com.pointlessapps.dartify.domain.game.x01.score.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.score.ScoreRepositoryImpl
import com.pointlessapps.dartify.domain.game.x01.score.usecase.IsCheckoutPossibleUseCase
import com.pointlessapps.dartify.domain.game.x01.score.usecase.ValidateScoreUseCase
import com.pointlessapps.dartify.domain.game.x01.score.usecase.ValidateSingleThrowUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val scoreModule = module {
    factoryOf(::ValidateSingleThrowUseCase)
    factoryOf(::ValidateScoreUseCase)
    factoryOf(::IsCheckoutPossibleUseCase)

    singleOf(::ScoreRepositoryImpl).bind(ScoreRepository::class)
}
