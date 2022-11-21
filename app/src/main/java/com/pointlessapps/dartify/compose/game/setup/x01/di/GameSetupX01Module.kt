package com.pointlessapps.dartify.compose.game.setup.x01.di

import com.pointlessapps.dartify.compose.game.setup.x01.ui.GameSetupX01ViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

internal val gameSetupX01Module = module {
    viewModelOf(::GameSetupX01ViewModel)
}
