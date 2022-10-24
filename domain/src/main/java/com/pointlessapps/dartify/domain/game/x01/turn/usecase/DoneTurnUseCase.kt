package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.score.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository
import com.pointlessapps.dartify.domain.game.x01.turn.model.DoneTurnEvent
import com.pointlessapps.dartify.domain.game.x01.turn.model.InputScore

class DoneTurnUseCase(
    private val turnRepository: TurnRepository,
    private val scoreRepository: ScoreRepository,
) {

    operator fun invoke(score: Int, numberOfThrows: Int = DEFAULT_NUMBER_OF_THROWS): DoneTurnEvent {
        val gameState = turnRepository.getGameState()
        val currentPlayerScore = requireNotNull(
            gameState.playerScores.find {
                it.player.id == gameState.player.id
            },
        )
        val shouldAskForNumberOfDoubles = scoreRepository.shouldAskForNumberOfDoubles(
            score = currentPlayerScore.scoreLeft,
            scoreLeft = currentPlayerScore.scoreLeft - score,
            numberOfThrows = numberOfThrows,
            outMode = gameState.player.outMode,
        )
        val minNumberOfThrows = scoreRepository.calculateMinNumberOfThrows(
            score = currentPlayerScore.scoreLeft,
            outMode = gameState.player.outMode,
        )
        val maxNumberOfDoubles = scoreRepository.calculateMaxNumberOfDoubles(
            score = currentPlayerScore.scoreLeft,
        )

        return turnRepository.doneTurn(
            score = score,
            shouldAskForNumberOfDoubles = shouldAskForNumberOfDoubles,
            minNumberOfThrows = minNumberOfThrows,
            maxNumberOfDoubles = maxNumberOfDoubles,
        )
    }

    operator fun invoke(inputScore: InputScore.Dart): DoneTurnEvent {
        val gameState = turnRepository.getGameState()
        val currentPlayerScore = requireNotNull(
            gameState.playerScores.find {
                it.player.id == gameState.player.id
            },
        )
        val shouldAskForNumberOfDoubles = scoreRepository.shouldAskForNumberOfDoubles(
            score = currentPlayerScore.scoreLeft,
            scoreLeft = currentPlayerScore.scoreLeft - inputScore.scores.sum(),
            numberOfThrows = inputScore.scores.size,
            outMode = gameState.player.outMode,
        )
        val minNumberOfThrows = scoreRepository.calculateMinNumberOfThrows(
            score = currentPlayerScore.scoreLeft,
            outMode = gameState.player.outMode,
        )
        val maxNumberOfDoubles = scoreRepository.calculateMaxNumberOfDoubles(
            score = currentPlayerScore.scoreLeft,
        )

        return turnRepository.doneTurn(
            score = inputScore.scores.sum(),
            shouldAskForNumberOfDoubles = shouldAskForNumberOfDoubles,
            minNumberOfThrows = minNumberOfThrows,
            maxNumberOfDoubles = maxNumberOfDoubles,
        )
    }
}
