package com.pointlessapps.dartify.domain.game.x01.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.model.OutMode

class ValidateScoreUseCase(
    private val scoreRepository: ScoreRepository,
) {

    operator fun invoke(
        score: Int,
        scoreLeft: Int,
        outMode: OutMode,
        numberOfThrows: Int = DEFAULT_NUMBER_OF_THROWS,
    ) = scoreRepository.validateScore(score, scoreLeft, numberOfThrows, outMode)
}
