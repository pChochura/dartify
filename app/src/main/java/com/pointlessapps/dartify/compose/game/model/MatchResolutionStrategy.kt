package com.pointlessapps.dartify.compose.game.model

internal enum class MatchResolutionStrategy {
    FirstTo, BestOf;

    fun resolutionPredicate(maximumValue: Int): (value: Int) -> Boolean = { value ->
        when (this) {
            FirstTo -> value >= maximumValue
            BestOf -> value > maximumValue / 2
        }
    }
}
