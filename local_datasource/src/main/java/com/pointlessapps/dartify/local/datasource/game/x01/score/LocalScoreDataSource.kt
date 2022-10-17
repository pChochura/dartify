package com.pointlessapps.dartify.local.datasource.game.x01.score

import com.pointlessapps.dartify.datasource.game.x01.score.ScoreDataSource

@Suppress("MagicNumber")
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

    override fun getPossibleDoubleScoresFor(numberOfThrows: Int, numberOfDoubles: Int) =
        when (numberOfThrows) {
            1 -> when (numberOfDoubles) {
                1 -> oneThrowPossibleScoresCalculator.oneThrowOneDoublePossibleScores
                else -> emptySet()
            }
            2 -> when (numberOfDoubles) {
                1 -> twoThrowsPossibleScoresCalculator.twoThrowsOneDoublePossibleScores
                2 -> twoThrowsPossibleScoresCalculator.twoThrowsTwoDoublesPossibleScores
                else -> emptySet()
            }
            3 -> when (numberOfDoubles) {
                1 -> threeThrowsPossibleScoresCalculator.threeThrowsOneDoublePossibleScores
                2 -> threeThrowsPossibleScoresCalculator.threeThrowsTwoDoublesPossibleScores
                3 -> threeThrowsPossibleScoresCalculator.threeThrowsThreeDoublesPossibleScores
                else -> emptySet()
            }
            else -> emptySet()
        }
}
