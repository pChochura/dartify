package com.pointlessapps.dartify.compose.game.setup.x01.ui.model

internal sealed class MatchResolutionStrategy(
    val numberOfSets: Int,
    val numberOfLegs: Int,
) {
    abstract fun resolutionPredicate(): (Int, Int) -> Boolean

    class FirstTo(numberOfSets: Int, numberOfLegs: Int) :
        MatchResolutionStrategy(numberOfSets, numberOfLegs) {

        override fun resolutionPredicate() = { sets: Int, legs: Int ->
            sets == numberOfSets && legs == numberOfLegs
        }
    }

    class BestOf(numberOfSets: Int, numberOfLegs: Int) :
        MatchResolutionStrategy(numberOfSets, numberOfLegs) {

        override fun resolutionPredicate() = { sets: Int, legs: Int ->
            sets == numberOfSets && legs > numberOfLegs / 2
        }
    }

    companion object {
        // TODO probably move to settings
        const val MIN_SETS = 1
        const val MAX_SETS = 5

        const val MIN_LEGS = 1
        const val MAX_LEGS = 9
    }
}
