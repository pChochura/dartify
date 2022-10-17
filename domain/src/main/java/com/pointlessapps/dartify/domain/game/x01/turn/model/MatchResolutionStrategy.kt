package com.pointlessapps.dartify.domain.game.x01.turn.model

enum class MatchResolutionStrategy {
    FirstTo, BestOf;

    fun resolutionPredicate(maximumValue: Int): (value: Int) -> Boolean = { value ->
        when (this) {
            FirstTo -> value >= maximumValue
            BestOf -> value > maximumValue / 2
        }
    }
}
