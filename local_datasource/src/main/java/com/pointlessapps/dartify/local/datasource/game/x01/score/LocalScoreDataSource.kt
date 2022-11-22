package com.pointlessapps.dartify.local.datasource.game.x01.score

import com.pointlessapps.dartify.datasource.game.x01.score.ScoreDataSource

internal class LocalScoreDataSource(
    private val oneThrowPossibleScoresCalculator: OneThrowPossibleScoresCalculator,
    private val twoThrowsPossibleScoresCalculator: TwoThrowsPossibleScoresCalculator,
    private val threeThrowsPossibleScoresCalculator: ThreeThrowsPossibleScoresCalculator,
) : ScoreDataSource {

    override fun getPossibleScoresFor(numberOfThrows: Int) = when (numberOfThrows) {
        1 -> oneThrowPossibleScoresCalculator.oneThrowPossibleScores
        2 -> twoThrowsPossibleScoresCalculator.twoThrowsPossibleScores
        3 -> threeThrowsPossibleScoresCalculator.threeThrowsPossibleScores
        else -> emptySet()
    }

    override fun getPossibleOutScoresFor(numberOfThrows: Int) = when (numberOfThrows) {
        1 -> oneThrowPossibleScoresCalculator.oneThrowPossibleOutScores
        2 -> twoThrowsPossibleScoresCalculator.twoThrowsPossibleOutScores
        3 -> threeThrowsPossibleScoresCalculator.threeThrowsPossibleOutScores
        else -> emptySet()
    }

    override fun getPossibleDoubleOutScoresFor(numberOfThrows: Int) = when (numberOfThrows) {
        1 -> oneThrowPossibleScoresCalculator.oneThrowPossibleDoubleOutScores
        2 -> twoThrowsPossibleScoresCalculator.twoThrowsPossibleDoubleOutScores
        3 -> threeThrowsPossibleScoresCalculator.threeThrowsPossibleDoubleOutScores
        else -> emptySet()
    }

    override fun getPossibleMasterOutScoresFor(numberOfThrows: Int) = when (numberOfThrows) {
        1 -> oneThrowPossibleScoresCalculator.oneThrowPossibleMasterOutScores
        2 -> twoThrowsPossibleScoresCalculator.twoThrowsPossibleMasterOutScores
        3 -> threeThrowsPossibleScoresCalculator.threeThrowsPossibleMasterOutScores
        else -> emptySet()
    }

    override fun isScorePossibleToDoubleOutWith(
        score: Int,
        numberOfThrows: Int,
        numberOfDoubles: Int,
    ): Boolean {
        when (numberOfThrows) {
            3 -> when (numberOfDoubles) {
                3 -> if (
                    score in oneThrowPossibleScoresCalculator.oneThrowPossibleDoubleOutScores &&
                    oneThrowPossibleScoresCalculator.oneThrowPossibleDoubleOutScores.find {
                        isScorePossibleToDoubleOutWith(score - it, 2, 2)
                    } != null
                ) {
                    return true
                }
                2 -> if (
                    score in twoThrowsPossibleScoresCalculator.twoThrowsPossibleDoubleOutScores &&
                    oneThrowPossibleScoresCalculator.oneThrowPossibleScores.find {
                        isScorePossibleToDoubleOutWith(score - it, 2, 2)
                    } != null
                ) {
                    return true
                }
                1 -> if (score in threeThrowsPossibleScoresCalculator.threeThrowsPossibleDoubleOutScores) {
                    return true
                }
            }
            2 -> when (numberOfDoubles) {
                2 -> if (
                    score in oneThrowPossibleScoresCalculator.oneThrowPossibleDoubleOutScores &&
                    oneThrowPossibleScoresCalculator.oneThrowPossibleDoubleOutScores.find {
                        isScorePossibleToDoubleOutWith(score - it, 1, 1)
                    } != null
                ) {
                    return true
                }
                1 -> if (score in twoThrowsPossibleScoresCalculator.twoThrowsPossibleDoubleOutScores) {
                    return true
                }
            }
            1 -> if (
                numberOfDoubles == 1 &&
                score in oneThrowPossibleScoresCalculator.oneThrowPossibleDoubleOutScores
            ) {
                return true
            }
        }

        return false
    }
}
