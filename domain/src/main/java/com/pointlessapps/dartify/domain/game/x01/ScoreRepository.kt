package com.pointlessapps.dartify.domain.game.x01

import com.pointlessapps.dartify.datasource.game.x01.ScoreDataSource
import com.pointlessapps.dartify.domain.game.x01.model.OutMode

interface ScoreRepository {
    /**
     * Validates if the score is possible to be thrown after [numberOfThrows]
     * and if the [scoreLeft] is possible to be checked out with selected [outMode]
     */
    fun validateScore(
        score: Int,
        scoreLeft: Int,
        numberOfThrows: Int,
        outMode: OutMode,
    ): Boolean

    /**
     * Checks if the [outMode] is equal to [OutMode.Double] and if so, performs a check
     * for the [scoreLeft] to ensure it is possible to be checked out on a double throw,
     * and finally checks if the [score] is possible to be checked out with selected [OutMode.Double]
     */
    fun shouldAsForNumberOfDoubles(
        score: Int,
        scoreLeft: Int,
        numberOfThrows: Int,
        outMode: OutMode,
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
    fun calculateMinNumberOfThrows(score: Int, outMode: OutMode): Int

    /**
     * Returns true if the current [score] can be checked out after [numberOfThrows] throws
     * with selected [outMode]
     */
    fun isCheckoutPossible(
        score: Int,
        numberOfThrows: Int,
        outMode: OutMode,
    ): Boolean
}

internal class ScoreRepositoryImpl(
    private val scoreDataSource: ScoreDataSource,
) : ScoreRepository {

    override fun validateScore(score: Int, scoreLeft: Int, numberOfThrows: Int, outMode: OutMode) =
        (outMode != OutMode.Double || scoreLeft > 1 || scoreLeft == 0) &&
                score in scoreDataSource.getPossibleScoresFor(numberOfThrows)

    override fun shouldAsForNumberOfDoubles(
        score: Int,
        scoreLeft: Int,
        numberOfThrows: Int,
        outMode: OutMode,
    ) = scoreLeft <= SCORE_TO_ASK_FOR_DOUBLES && when (outMode) {
        OutMode.Double -> score in scoreDataSource.getPossibleDoubleOutScoresFor(numberOfThrows)
        else -> false
    }

    @Suppress("MagicNumber")
    override fun calculateMaxNumberOfDoubles(score: Int) = (1..3).associateWith { throws ->
        (throws downTo 1).find { doubles ->
            score in scoreDataSource.getPossibleDoubleScoresFor(throws, doubles)
        } ?: 1
    }

    @Suppress("MagicNumber")
    override fun calculateMinNumberOfThrows(score: Int, outMode: OutMode) = when (outMode) {
        OutMode.Straight -> (1..3).find {
            score in scoreDataSource.getPossibleOutScoresFor(it)
        }
        OutMode.Double -> (1..3).find {
            score in scoreDataSource.getPossibleDoubleOutScoresFor(it)
        }
        OutMode.Master -> (1..3).find {
            score in scoreDataSource.getPossibleMasterOutScoresFor(it)
        }
    } ?: 3

    override fun isCheckoutPossible(
        score: Int,
        numberOfThrows: Int,
        outMode: OutMode,
    ) = score in when (outMode) {
        OutMode.Straight -> scoreDataSource.getPossibleOutScoresFor(numberOfThrows)
        OutMode.Double -> scoreDataSource.getPossibleDoubleOutScoresFor(numberOfThrows)
        OutMode.Master -> scoreDataSource.getPossibleMasterOutScoresFor(numberOfThrows)
    }
}
