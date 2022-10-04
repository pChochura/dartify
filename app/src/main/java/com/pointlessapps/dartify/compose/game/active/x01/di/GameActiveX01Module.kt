package com.pointlessapps.dartify.compose.game.active.x01.di

import com.pointlessapps.dartify.compose.game.active.x01.ui.GameActiveX01ViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val gameActiveX01Module = module {
    viewModel {
        GameActiveX01ViewModel()
    }
}
