package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository

class AddInputUseCase(
    private val turnRepository: TurnRepository,
) {

    operator fun invoke(
        score: Int,
        numberOfThrows: Int = DEFAULT_NUMBER_OF_THROWS,
        numberOfThrowsOnDouble: Int,
    ) {
        turnRepository.addInput(score, numberOfThrows, numberOfThrowsOnDouble)
    }
}
