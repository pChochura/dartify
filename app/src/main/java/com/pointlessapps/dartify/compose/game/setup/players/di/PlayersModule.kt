package com.pointlessapps.dartify.compose.game.setup.players.di

import com.pointlessapps.dartify.compose.game.setup.players.ui.SelectPlayersViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

internal val playersModule = module {
    viewModelOf(::SelectPlayersViewModel)
}
