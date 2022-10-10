package com.pointlessapps.dartify.local.datasource.game.x01.di

import com.pointlessapps.dartify.datasource.game.x01.ScoreDataSource
import com.pointlessapps.dartify.local.datasource.game.x01.LocalScoreDataSource
import org.koin.dsl.module

internal val gameX01Module = module {
    single<ScoreDataSource> {
        LocalScoreDataSource()
    }
}
