package com.pointlessapps.dartify.compose.game.active.x01.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

internal sealed interface InputScore : Parcelable {
    @Parcelize
    data class Turn(val score: Int) : InputScore

    @Parcelize
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
