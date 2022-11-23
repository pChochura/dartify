package com.pointlessapps.dartify.domain.game.x01.model

sealed interface InputScore {
    fun score(): Int

    data class Turn(val score: Int) : InputScore {
        override fun score() = score
    }

    data class Dart(val scores: List<Int>) : InputScore {
        override fun score() = scores.sum()

        fun withFixedSize(size: Int): InputScore {
            if (scores.size == size) {
                return this
            }

            return copy(
                scores = (0 until size).map {
                    scores.getOrElse(it) { 0 }
                },
            )
        }
    }
}
