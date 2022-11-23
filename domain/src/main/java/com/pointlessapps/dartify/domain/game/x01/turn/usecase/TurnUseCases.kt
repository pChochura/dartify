package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.game.x01.model.InputScore

class TurnUseCases(
    private val nextTurnUseCase: NextTurnUseCase,
    private val doneTurnUseCase: DoneTurnUseCase,
    private val undoTurnUseCase: UndoTurnUseCase,
) {
    fun nextTurn() = nextTurnUseCase()
    fun doneTurn(inputScore: InputScore) = doneTurnUseCase(inputScore)
    fun undoTurn() = undoTurnUseCase()
}