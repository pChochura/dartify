package com.pointlessapps.dartify.compose.game.model

internal enum class MatchResolutionStrategy {
    FirstTo, BestOf;

    fun convertValueFrom(strategy: MatchResolutionStrategy, value: Int) = when (strategy) {
        FirstTo -> when (this) {
            FirstTo -> value
            BestOf -> value * 2 - 1
        }
        BestOf -> when (this) {
            FirstTo -> (value + 1) / 2
            BestOf -> value
        }
    }
}
