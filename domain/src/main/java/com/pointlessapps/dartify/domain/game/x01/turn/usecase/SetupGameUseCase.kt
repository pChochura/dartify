package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.model.Player
import com.pointlessapps.dartify.domain.model.GameMode
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository
import com.pointlessapps.dartify.domain.game.x01.turn.model.MatchResolutionStrategy

class SetupGameUseCase(
    private val turnRepository: TurnRepository,
) {

    operator fun invoke(
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
}
