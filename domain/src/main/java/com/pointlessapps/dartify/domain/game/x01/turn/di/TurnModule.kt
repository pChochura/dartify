package com.pointlessapps.dartify.domain.game.x01.turn.di

import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepositoryImpl
import com.pointlessapps.dartify.domain.game.x01.turn.usecase.*
import org.koin.dsl.module

internal val turnModule = module {
    factory {
        UndoTurnUseCase(
            turnRepository = get(),
        )
    }
    factory {
        NextTurnUseCase(
            turnRepository = get(),
        )
    }
    factory {
        AddInputUseCase(
            turnRepository = get(),
        )
    }
    factory {
        FinishLegUseCase(
            turnRepository = get(),
        )
    }
    factory {
        SetupGameUseCase(
            turnRepository = get(),
        )
    }

    single<TurnRepository> {
        TurnRepositoryImpl(
            turnDataSource = get(),
        )
    }
}
