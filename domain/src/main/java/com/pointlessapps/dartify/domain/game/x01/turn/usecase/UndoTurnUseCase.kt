package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository

class UndoTurnUseCase(
    private val turnRepository: TurnRepository,
) {

    operator fun invoke() = turnRepository.undo()
}
