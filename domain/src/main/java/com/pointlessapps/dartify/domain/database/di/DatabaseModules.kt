package com.pointlessapps.dartify.domain.database.di

import com.pointlessapps.dartify.domain.database.game.di.gameModules
import com.pointlessapps.dartify.domain.database.players.di.playersModule

internal val databaseModules = listOf(playersModule) + gameModules
