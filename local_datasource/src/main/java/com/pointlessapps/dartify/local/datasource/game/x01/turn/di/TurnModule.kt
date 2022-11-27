package com.pointlessapps.dartify.local.datasource.game.x01.turn.di

import com.pointlessapps.dartify.datasource.game.x01.turn.TurnDataSource
import com.pointlessapps.dartify.local.datasource.game.x01.turn.LocalTurnDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val turnModule = module {
    singleOf(::LocalTurnDataSource).bind(TurnDataSource::class)
}
