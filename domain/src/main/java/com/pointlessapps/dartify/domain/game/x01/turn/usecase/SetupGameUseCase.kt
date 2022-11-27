package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.database.game.model.ActiveGame
import com.pointlessapps.dartify.domain.database.game.usecase.LoadGameUseCase
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository
import com.pointlessapps.dartify.domain.game.x01.turn.model.MatchResolutionStrategy
import com.pointlessapps.dartify.domain.model.GameMode
import com.pointlessapps.dartify.domain.model.Player

class SetupGameUseCase(
    private val turnRepository: TurnRepository,
    private val loadGameUseCase: LoadGameUseCase,
) {

    fun setupNewGame(
        players: List<Player>,
        startingScore: Int,
        inMode: GameMode,
        numberOfSets: Int,
        numberOfLegs: Int,
        matchResolutionStrategy: MatchResolutionStrategy,
    ) = turnRepository.setup(
        players,
        startingScore,
        inMode,
        numberOfSets,
        numberOfLegs,
        matchResolutionStrategy,
    )

    fun loadGame(gameId: Long, gameType: ActiveGame.Type) = loadGameUseCase(gameId, gameType)

    fun resetGame() = turnRepository.reset()
}
