package com.pointlessapps.dartify.domain.game.x01.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.ScoreRepository

class ValidateScoreUseCase(
    private val scoreRepository: ScoreRepository,
) {

    operator fun invoke(score: Int, numberOfThrows: Int = DEFAULT_NUMBER_OF_THROWS) =
        scoreRepository.validateScore(score, numberOfThrows)
}
