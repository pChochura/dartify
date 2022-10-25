package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.model.InputScore
import com.pointlessapps.dartify.domain.game.x01.score.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository
import com.pointlessapps.dartify.domain.game.x01.turn.model.DoneTurnEvent
import kotlin.math.max
import kotlin.math.min

class DoneTurnUseCase(
    private val turnRepository: TurnRepository,
    private val scoreRepository: ScoreRepository,
) {

    operator fun invoke(inputScore: InputScore) = when (inputScore) {
        is InputScore.Dart -> doneTurn(
            inputScore.scores.sum(),
            inputScore.scores.size,
            inputScore.scores.takeLastWhile { it % 2 == 0 }.size,
        )
        is InputScore.Turn -> doneTurn(
            inputScore.score,
            DEFAULT_NUMBER_OF_THROWS,
        )
    }

    private fun doneTurn(
        score: Int,
        numberOfThrows: Int? = null,
        numberOfDoubles: Int? = null,
    ): DoneTurnEvent {
        val gameState = turnRepository.getGameState()
        val currentPlayerScore = requireNotNull(
            gameState.playerScores.find {
                it.player.id == gameState.player.id
            },
        )
        val shouldAskForNumberOfDoubles = scoreRepository.shouldAskForNumberOfDoubles(
            score = currentPlayerScore.scoreLeft,
            scoreLeft = currentPlayerScore.scoreLeft - score,
            numberOfThrows = numberOfThrows ?: DEFAULT_NUMBER_OF_THROWS,
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
            minNumberOfThrows = max(minNumberOfThrows, numberOfThrows ?: 1),
            maxNumberOfDoubles = maxNumberOfDoubles.mapValues { (key, value) ->
                if (key == numberOfThrows && numberOfDoubles != null) {
                    return@mapValues min(value, numberOfDoubles)
                }

                return@mapValues value
            },
        )
    }
}
