package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository

class NextTurnUseCase(
    private val turnRepository: TurnRepository,
) {

    operator fun invoke() = turnRepository.nextTurn()
}
