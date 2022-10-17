package com.pointlessapps.dartify.domain.game.x01.score.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.score.model.GameMode

class IsCheckoutPossibleUseCase(
    private val scoreRepository: ScoreRepository,
) {

    operator fun invoke(
        score: Int,
        outMode: GameMode,
        numberOfThrows: Int = DEFAULT_NUMBER_OF_THROWS,
    ) = scoreRepository.isCheckoutPossible(score, numberOfThrows, outMode)
}
