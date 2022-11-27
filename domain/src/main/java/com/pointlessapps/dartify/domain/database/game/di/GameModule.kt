package com.pointlessapps.dartify.domain.database.game.di

import com.pointlessapps.dartify.domain.database.game.GameRepository
import com.pointlessapps.dartify.domain.database.game.GameRepositoryImpl
import com.pointlessapps.dartify.domain.database.game.usecase.GetActiveGamesUseCase
import com.pointlessapps.dartify.domain.database.game.usecase.LoadGameUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val gameModule = module {
    factoryOf(::GetActiveGamesUseCase)
    factoryOf(::LoadGameUseCase)

    singleOf(::GameRepositoryImpl).bind(GameRepository::class)
}
