package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.game.x01.model.InputScore
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository

class FinishLegUseCase(
    private val turnRepository: TurnRepository,
) {

    operator fun invoke(inputScore: InputScore, numberOfThrows: Int, numberOfThrowsOnDouble: Int) =
        turnRepository.finishLeg(inputScore, numberOfThrows, numberOfThrowsOnDouble)
}
