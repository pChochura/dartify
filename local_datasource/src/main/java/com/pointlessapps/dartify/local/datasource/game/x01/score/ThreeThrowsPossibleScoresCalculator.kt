package com.pointlessapps.dartify.local.datasource.game.x01.score

internal class ThreeThrowsPossibleScoresCalculator(
    oneThrowPossibleScoresCalculator: OneThrowPossibleScoresCalculator,
    twoThrowsPossibleScoresCalculator: TwoThrowsPossibleScoresCalculator,
) {

    private val oneThrowPossibleScores = oneThrowPossibleScoresCalculator.oneThrowPossibleScores
    private val oneThrowPossibleDoubleOutScores =
        oneThrowPossibleScoresCalculator.oneThrowPossibleDoubleOutScores
    private val oneThrowPossibleMasterOutScores =
        oneThrowPossibleScoresCalculator.oneThrowPossibleMasterOutScores
    private val twoThrowsPossibleScores = twoThrowsPossibleScoresCalculator.twoThrowsPossibleScores

    val threeThrowsPossibleScores by lazy {
        twoThrowsPossibleScores.flatMap { firstAndSecond ->
            oneThrowPossibleScores.map { third -> firstAndSecond + third }
        }.toSet()
    }

    val threeThrowsPossibleOutScores by lazy {
        threeThrowsPossibleScores - 0
    }

    val threeThrowsPossibleDoubleOutScores by lazy {
        twoThrowsPossibleScores.flatMap { firstAndSecond ->
            oneThrowPossibleDoubleOutScores.map { third -> firstAndSecond + third }
        }.toSet()
    }

    val threeThrowsPossibleMasterOutScores by lazy {
        twoThrowsPossibleScores.flatMap { firstAndSecond ->
            oneThrowPossibleMasterOutScores.map { third -> firstAndSecond + third }
        }.toSet()
    }
}
