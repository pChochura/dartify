package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository

class FinishLegUseCase(
    private val turnRepository: TurnRepository,
) {

    operator fun invoke(numberOfThrows: Int, numberOfThrowsOnDouble: Int) =
        turnRepository.finishLeg(numberOfThrows, numberOfThrowsOnDouble)
}
