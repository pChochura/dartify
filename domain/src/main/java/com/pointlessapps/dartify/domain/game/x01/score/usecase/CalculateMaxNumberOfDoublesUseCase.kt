package com.pointlessapps.dartify.domain.game.x01.score.usecase

import com.pointlessapps.dartify.domain.game.x01.ScoreRepository

class CalculateMaxNumberOfDoublesUseCase(
    private val scoreRepository: ScoreRepository,
) {

    operator fun invoke(score: Int) = scoreRepository.calculateMaxNumberOfDoubles(score)
}
