package com.pointlessapps.dartify.local.datasource.database.game.di

import com.pointlessapps.dartify.datasource.database.game.GameDataSource
import com.pointlessapps.dartify.local.datasource.database.AppDatabase
import com.pointlessapps.dartify.local.datasource.database.game.LocalGameDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val gameModule = module {
    single { get<AppDatabase>().gameDao() }
    singleOf(::LocalGameDataSource).bind(GameDataSource::class)
}
