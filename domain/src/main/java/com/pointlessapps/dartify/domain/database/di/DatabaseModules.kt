package com.pointlessapps.dartify.domain.database.di

import com.pointlessapps.dartify.domain.database.game.x01.di.gameX01Module
import com.pointlessapps.dartify.domain.database.players.di.playersModule

internal val databaseModules = listOf(playersModule, gameX01Module)
