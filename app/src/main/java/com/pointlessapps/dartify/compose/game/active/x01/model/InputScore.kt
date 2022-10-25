package com.pointlessapps.dartify.compose.game.active.x01.model

internal sealed interface InputScore {
    data class Turn(val score: Int) : InputScore
    data class Dart(val scores: List<Int>) : InputScore {
        companion object {
            const val MAX_NUMBER_OF_THROWS = 3
        }
    }
}

internal fun InputScore?.score() = when (this) {
    is InputScore.Turn -> score
    is InputScore.Dart -> scores.sum()
    else -> 0
}
