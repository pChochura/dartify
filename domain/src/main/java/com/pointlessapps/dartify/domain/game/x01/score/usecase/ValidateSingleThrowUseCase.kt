package com.pointlessapps.dartify.domain.game.x01.score.usecase

import com.pointlessapps.dartify.domain.game.x01.score.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository

class ValidateSingleThrowUseCase(
    private val scoreRepository: ScoreRepository,
    private val turnRepository: TurnRepository,
) {

    operator fun invoke(score: Int, multiplier: Int, scoreLeft: Int): Boolean {
        val gameState = turnRepository.getGameState()
        val currentPlayer = gameState.player

        return scoreRepository.validateSingleThrow(
            score,
            multiplier,
            scoreLeft - score,
            gameState.startingScore,
            gameState.inMode,
            currentPlayer.outMode,
        )
    }
}
