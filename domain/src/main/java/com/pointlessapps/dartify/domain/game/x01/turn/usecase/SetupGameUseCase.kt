package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.game.x01.model.Player
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository
import com.pointlessapps.dartify.domain.game.x01.turn.model.CurrentState
import com.pointlessapps.dartify.domain.game.x01.turn.model.MatchResolutionStrategy

class SetupGameUseCase(
    private val turnRepository: TurnRepository,
) {

    operator fun invoke(
        players: List<Player>,
        startingScore: Int,
        numberOfSets: Int,
        numberOfLegs: Int,
        matchResolutionStrategy: MatchResolutionStrategy,
    ): CurrentState {
        turnRepository.setStartingScore(startingScore)
        turnRepository.setPlayers(players)
        turnRepository.setMatchResolutionStrategy(
            numberOfSets,
            numberOfLegs,
            matchResolutionStrategy,
        )

        return turnRepository.getStartingState()
    }
}
