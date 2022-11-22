package com.pointlessapps.dartify.local.datasource.database.di

import com.pointlessapps.dartify.local.datasource.database.AppDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val databaseModule = module {
    singleOf(AppDatabase::get)
}
