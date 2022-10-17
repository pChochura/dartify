package com.pointlessapps.dartify.local.datasource.game.x01.turn.di

import com.pointlessapps.dartify.datasource.game.x01.move.TurnDataSource
import com.pointlessapps.dartify.local.datasource.game.x01.turn.LocalTurnDataSource
import org.koin.dsl.module

internal val turnModule = module {
    single<TurnDataSource> {
        LocalTurnDataSource()
    }
}
