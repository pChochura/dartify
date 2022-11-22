package com.pointlessapps.dartify.local.datasource.database.di

import com.pointlessapps.dartify.local.datasource.database.players.di.playersModule

internal val databaseModules = listOf(
    databaseModule,
    playersModule,
)