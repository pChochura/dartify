package com.pointlessapps.dartify.domain.database.game.x01.di

import com.pointlessapps.dartify.domain.database.game.x01.GameX01Repository
import com.pointlessapps.dartify.domain.database.game.x01.GameX01RepositoryImpl
import com.pointlessapps.dartify.domain.database.game.x01.usecase.LoadGameX01UseCase
import com.pointlessapps.dartify.domain.database.game.x01.usecase.SaveCurrentGameUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val gameX01Module = module {
    factoryOf(::SaveCurrentGameUseCase)
    factoryOf(::LoadGameX01UseCase)

    singleOf(::GameX01RepositoryImpl).bind(GameX01Repository::class)
}