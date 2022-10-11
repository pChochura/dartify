package com.pointlessapps.dartify.domain.game.x01.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.ScoreRepository

class CalculateMaxNumberOfDoublesForThreeThrowsUseCase(
    private val scoreRepository: ScoreRepository,
) {

    operator fun invoke(score: Int) =
        scoreRepository.calculateMaxNumberOfDoubles(score)[DEFAULT_NUMBER_OF_THROWS] ?: 1
}
