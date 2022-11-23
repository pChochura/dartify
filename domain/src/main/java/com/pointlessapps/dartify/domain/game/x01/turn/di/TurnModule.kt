package com.pointlessapps.dartify.domain.game.x01.turn.di

import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepositoryImpl
import com.pointlessapps.dartify.domain.game.x01.turn.usecase.*
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val turnModule = module {
    factoryOf(::UndoTurnUseCase)
    factoryOf(::NextTurnUseCase)
    factoryOf(::DoneTurnUseCase)
    factoryOf(::TurnUseCases)

    factoryOf(::AddInputUseCase)
    factoryOf(::FinishLegUseCase)
    factoryOf(::SetupGameUseCase)

    singleOf(::TurnRepositoryImpl).bind(TurnRepository::class)
}
