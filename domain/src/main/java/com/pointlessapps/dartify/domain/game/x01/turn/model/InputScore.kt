package com.pointlessapps.dartify.domain.game.x01.turn.model

sealed interface InputScore {
    data class Turn(val score: Int) : InputScore
    data class Dart(val scores: List<Int>) : InputScore
}
