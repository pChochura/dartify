package com.pointlessapps.dartify.compose.game.setup.di

import com.pointlessapps.dartify.compose.game.setup.players.di.playersModule
import com.pointlessapps.dartify.compose.game.setup.x01.di.gameSetupX01Module

internal val gameSetupModules = listOf(
    gameSetupX01Module,
    playersModule,
)
