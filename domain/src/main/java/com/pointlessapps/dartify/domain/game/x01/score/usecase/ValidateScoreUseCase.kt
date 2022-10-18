package com.pointlessapps.dartify.domain.game.x01.score.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.score.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository

class ValidateScoreUseCase(
    private val scoreRepository: ScoreRepository,
    private val turnRepository: TurnRepository,
) {

    operator fun invoke(
        score: Int,
        numberOfThrows: Int = DEFAULT_NUMBER_OF_THROWS,
    ): Boolean {
        val gameState = turnRepository.getGameState()
        val currentPlayer = gameState.player
        val scoreLeft = gameState.playerScores.find {
            it.player.id == currentPlayer.id
        }?.scoreLeft ?: return false

        return scoreRepository.validateScore(
            score,
            scoreLeft - score,
            gameState.startingScore,
            numberOfThrows,
            gameState.inMode,
            currentPlayer.outMode,
        )
    }
}
