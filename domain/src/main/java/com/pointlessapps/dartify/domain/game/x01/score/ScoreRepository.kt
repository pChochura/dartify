package com.pointlessapps.dartify.domain.game.x01.score

import com.pointlessapps.dartify.datasource.game.x01.score.ScoreDataSource
import com.pointlessapps.dartify.domain.game.x01.SCORE_TO_ASK_FOR_DOUBLES
import com.pointlessapps.dartify.domain.game.x01.score.model.GameMode

interface ScoreRepository {
    /**
     * Validates if the score is possible to be thrown after [numberOfThrows]
     * and if the [scoreLeft] is possible to be checked out with selected [outMode],
     * and if the score is correct for the check in with the selected [inMode]
     */
    fun validateScore(
        score: Int,
        scoreLeft: Int,
        startingScore: Int,
        numberOfThrows: Int,
        inMode: GameMode,
        outMode: GameMode,
    ): Boolean

    /**
     * Checks if the [outMode] is equal to [GameMode.Double] and if so, performs a check
     * for the [scoreLeft] to ensure it is possible to be checked out on a double throw,
     * and finally checks if the [score] is possible to be checked out with selected [GameMode.Double]
     */
    fun shouldAsForNumberOfDoubles(
        score: Int,
        scoreLeft: Int,
        numberOfThrows: Int,
        outMode: GameMode,
    ): Boolean

    /**
     * Returns a map of [Int] -> [Int] that represents association of number of throws to the
     * number of possible double throws
     */
    fun calculateMaxNumberOfDoubles(score: Int): Map<Int, Int>

    /**
     * Returns minimal number of throws that is required for the [score] to be checked out
     * with selected [outMode]
     */
    fun calculateMinNumberOfThrows(score: Int, outMode: GameMode): Int

    /**
     * Returns true if the current [score] can be checked out after [numberOfThrows] throws
     * with selected [outMode]
     */
    fun isCheckoutPossible(
        score: Int,
        numberOfThrows: Int,
        outMode: GameMode,
    ): Boolean
}

internal class ScoreRepositoryImpl(
    private val scoreDataSource: ScoreDataSource,
) : ScoreRepository {

    override fun validateScore(
        score: Int,
        scoreLeft: Int,
        startingScore: Int,
        numberOfThrows: Int,
        inMode: GameMode,
        outMode: GameMode,
    ): Boolean {
        val checkInSatisfied =
            inMode == GameMode.Straight || score != startingScore - scoreLeft || score != 1
        val checkOutSatisfied = outMode == GameMode.Straight || scoreLeft > 1 || scoreLeft == 0
        return score in scoreDataSource.getPossibleScoresFor(numberOfThrows) &&
                checkInSatisfied && checkOutSatisfied
    }

    override fun shouldAsForNumberOfDoubles(
        score: Int,
        scoreLeft: Int,
        numberOfThrows: Int,
        outMode: GameMode,
    ) = scoreLeft <= SCORE_TO_ASK_FOR_DOUBLES && when (outMode) {
        GameMode.Double -> score in scoreDataSource.getPossibleDoubleOutScoresFor(numberOfThrows)
        else -> false
    }

    @Suppress("MagicNumber")
    override fun calculateMaxNumberOfDoubles(score: Int) = (1..3).associateWith { throws ->
        (throws downTo 1).find { doubles ->
            score in scoreDataSource.getPossibleDoubleScoresFor(throws, doubles)
        } ?: 1
    }

    @Suppress("MagicNumber")
    override fun calculateMinNumberOfThrows(score: Int, outMode: GameMode) = when (outMode) {
        GameMode.Straight -> (1..3).find {
            score in scoreDataSource.getPossibleOutScoresFor(it)
        }
        GameMode.Double -> (1..3).find {
            score in scoreDataSource.getPossibleDoubleOutScoresFor(it)
        }
        GameMode.Master -> (1..3).find {
            score in scoreDataSource.getPossibleMasterOutScoresFor(it)
        }
    } ?: 3

    override fun isCheckoutPossible(
        score: Int,
        numberOfThrows: Int,
        outMode: GameMode,
    ) = score in when (outMode) {
        GameMode.Straight -> scoreDataSource.getPossibleOutScoresFor(numberOfThrows)
        GameMode.Double -> scoreDataSource.getPossibleDoubleOutScoresFor(numberOfThrows)
        GameMode.Master -> scoreDataSource.getPossibleMasterOutScoresFor(numberOfThrows)
    }
}