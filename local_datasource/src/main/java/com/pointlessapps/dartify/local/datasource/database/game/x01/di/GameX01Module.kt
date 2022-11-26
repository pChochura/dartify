package com.pointlessapps.dartify.local.datasource.database.game.x01.di

import com.pointlessapps.dartify.datasource.database.game.x01.GameX01DataSource
import com.pointlessapps.dartify.local.datasource.database.AppDatabase
import com.pointlessapps.dartify.local.datasource.database.game.x01.LocalGameX01DataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val gameX01Module = module {
    single { get<AppDatabase>().gameX01Dao() }
    singleOf(::LocalGameX01DataSource).bind(GameX01DataSource::class)
}
