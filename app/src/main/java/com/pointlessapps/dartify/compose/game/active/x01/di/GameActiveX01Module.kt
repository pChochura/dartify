package com.pointlessapps.dartify.compose.game.active.x01.di

import com.pointlessapps.dartify.compose.game.active.x01.ui.GameActiveX01ViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val gameActiveX01Module = module {
    viewModel {
        GameActiveX01ViewModel(
            validateScoreUseCase = get(),
            isCheckoutPossibleUseCase = get(),
            shouldAsForNumberOfDoublesUseCase = get(),
            calculateMaxNumberOfDoublesUseCase = get(),
            calculateMinNumberOfThrowsUseCase = get(),
            calculateMaxNumberOfDoublesForThreeThrowsUseCase = get(),
            nextTurnUseCase = get(),
            addInputUseCase = get(),
            undoTurnUseCase = get(),
            finishLegUseCase = get(),
            setupGameUseCase = get(),
        )
    }
}
