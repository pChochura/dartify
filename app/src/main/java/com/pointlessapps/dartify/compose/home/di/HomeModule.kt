package com.pointlessapps.dartify.compose.home.di

import com.pointlessapps.dartify.compose.home.ui.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val homeModule = module {
    viewModel {
        HomeViewModel()
    }
}
