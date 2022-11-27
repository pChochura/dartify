package com.pointlessapps.dartify.domain.database.game.usecase

import com.pointlessapps.dartify.domain.database.game.GameRepository

class GetActiveGamesUseCase(
    private val gameRepository: GameRepository,
) {

    operator fun invoke() = gameRepository.getAllActiveGames()
}
