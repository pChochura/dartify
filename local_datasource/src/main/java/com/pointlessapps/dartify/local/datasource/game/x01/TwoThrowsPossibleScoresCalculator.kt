package com.pointlessapps.dartify.local.datasource.game.x01

internal class TwoThrowsPossibleScoresCalculator(
    oneThrowPossibleScoresCalculator: OneThrowPossibleScoresCalculator,
) {

    private val oneThrowPossibleScores = oneThrowPossibleScoresCalculator.oneThrowPossibleScores
    private val oneThrowPossibleDoubleOutScores =
        oneThrowPossibleScoresCalculator.oneThrowPossibleDoubleOutScores
    private val oneThrowPossibleMasterOutScores =
        oneThrowPossibleScoresCalculator.oneThrowPossibleMasterOutScores

    val twoThrowsPossibleScores by lazy {
        oneThrowPossibleScores.flatMap { first ->
            oneThrowPossibleScores.map { second -> first + second }
        }.toSet()
    }

    val twoThrowsPossibleOutScores by lazy {
        twoThrowsPossibleScores - 0
    }

    val twoThrowsPossibleDoubleOutScores by lazy {
        oneThrowPossibleScores.flatMap { first ->
            oneThrowPossibleDoubleOutScores.map { second -> first + second }
        }.toSet()
    }

    val twoThrowsPossibleMasterOutScores by lazy {
        oneThrowPossibleScores.flatMap { first ->
            oneThrowPossibleMasterOutScores.map { second -> first + second }
        }.toSet()
    }

    val twoThrowsOneDoublePossibleScores by lazy {
        twoThrowsPossibleDoubleOutScores
    }

    val twoThrowsTwoDoublesPossibleScores by lazy {
        oneThrowPossibleDoubleOutScores.flatMap { first ->
            oneThrowPossibleDoubleOutScores.map { second -> first + second }
        }.toSet()
    }
}
