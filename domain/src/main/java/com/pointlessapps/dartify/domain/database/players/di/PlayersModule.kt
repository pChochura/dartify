package com.pointlessapps.dartify.domain.database.players.di

import com.pointlessapps.dartify.domain.database.players.PlayersRepository
import com.pointlessapps.dartify.domain.database.players.PlayersRepositoryImpl
import com.pointlessapps.dartify.domain.database.players.usecase.DeletePlayerUseCase
import com.pointlessapps.dartify.domain.database.players.usecase.GetAllPlayersUseCase
import com.pointlessapps.dartify.domain.database.players.usecase.SavePlayerUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val playersModule = module {
    factoryOf(::SavePlayerUseCase)
    factoryOf(::DeletePlayerUseCase)
    factoryOf(::GetAllPlayersUseCase)

    singleOf(::PlayersRepositoryImpl).bind(PlayersRepository::class)
}
