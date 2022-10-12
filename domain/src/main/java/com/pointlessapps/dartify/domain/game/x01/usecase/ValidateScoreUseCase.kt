package com.pointlessapps.dartify.domain.game.x01.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.model.GameMode

class ValidateScoreUseCase(
    private val scoreRepository: ScoreRepository,
) {

    operator fun invoke(
        score: Int,
        scoreLeft: Int,
        startingScore: Int,
        inMode: GameMode,
        outMode: GameMode,
        numberOfThrows: Int = DEFAULT_NUMBER_OF_THROWS,
    ) = scoreRepository.validateScore(
        score,
        scoreLeft,
        startingScore,
        numberOfThrows,
        inMode,
        outMode,
    )
}
