package com.pointlessapps.dartify.domain.game.x01.usecase

import com.pointlessapps.dartify.domain.game.x01.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.model.GameMode

class CalculateMinNumberOfThrowsUseCase(
    private val scoreRepository: ScoreRepository,
) {

    operator fun invoke(score: Int, outMode: GameMode) =
        scoreRepository.calculateMinNumberOfThrows(score, outMode)
}
