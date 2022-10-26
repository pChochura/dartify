package com.pointlessapps.dartify.local.datasource.game.x01.score

internal class OneThrowPossibleScoresCalculator {

    private val oneThrowRange = (1..20).toSet()

    val oneThrowPossibleDoubleOutScores by lazy {
        (oneThrowRange.map { it * 2 } + 50).toSet()
    }

    val oneThrowPossibleMasterOutScores by lazy {
        oneThrowPossibleDoubleOutScores + oneThrowRange.map { it * 3 }
    }

    val oneThrowPossibleOutScores by lazy {
        oneThrowRange + oneThrowPossibleMasterOutScores
    }

    val oneThrowPossibleScores by lazy {
        oneThrowPossibleOutScores + 0
    }
}
