package com.pointlessapps.dartify.local.datasource.game.x01.di

import com.pointlessapps.dartify.local.datasource.game.x01.checkout.di.checkoutModule
import com.pointlessapps.dartify.local.datasource.game.x01.turn.di.turnModule
import com.pointlessapps.dartify.local.datasource.game.x01.score.di.scoreModule

internal val gameX01Modules = listOf(
    scoreModule,
    turnModule,
    checkoutModule,
)
