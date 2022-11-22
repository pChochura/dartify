package com.pointlessapps.dartify.local.datasource.database.players.di

import com.pointlessapps.dartify.datasource.database.players.PlayersDataSource
import com.pointlessapps.dartify.local.datasource.database.AppDatabase
import com.pointlessapps.dartify.local.datasource.database.players.LocalPlayersDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val playersModule = module {
    single { get<AppDatabase>().playersDao() }
    singleOf(::LocalPlayersDataSource).bind(PlayersDataSource::class)
}
