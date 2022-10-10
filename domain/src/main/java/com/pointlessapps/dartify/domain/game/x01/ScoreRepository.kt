package com.pointlessapps.dartify.domain.game.x01

import com.pointlessapps.dartify.datasource.game.x01.ScoreDataSource
import com.pointlessapps.dartify.domain.game.x01.model.OutMode

interface ScoreRepository {
    fun validateScore(
        score: Int,
        scoreLeft: Int,
        numberOfThrows: Int,
        outMode: OutMode,
    ): Boolean

    fun shouldAsForNumberOfDoubles(
        score: Int,
        scoreLeft: Int,
        numberOfThrows: Int,
        outMode: OutMode,
    ): Boolean

    fun calculateMaxNumberOfDoubles(score: Int): Int
    fun calculateMinNumberOfThrows(score: Int, outMode: OutMode): Int

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
    override fun calculateMaxNumberOfDoubles(score: Int) = (3 downTo 1)
        .find { score in scoreDataSource.getPossibleDoubleScoresFor(it) } ?: 1

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
