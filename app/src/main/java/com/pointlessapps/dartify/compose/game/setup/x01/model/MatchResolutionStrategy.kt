package com.pointlessapps.dartify.compose.game.setup.x01.model

internal sealed class MatchResolutionStrategy(
    val numberOfSets: Int,
    val numberOfLegs: Int,
) {
    abstract fun resolutionPredicate(): (Int, Int) -> Boolean
    abstract fun withNumberOfLegsChanged(change: Int): MatchResolutionStrategy
    abstract fun withNumberOfSetsChanged(change: Int): MatchResolutionStrategy

    class FirstTo(numberOfSets: Int, numberOfLegs: Int) :
        MatchResolutionStrategy(numberOfSets, numberOfLegs) {

        override fun resolutionPredicate() = { sets: Int, legs: Int ->
            sets >= numberOfSets && legs >= numberOfLegs
        }

        override fun withNumberOfLegsChanged(change: Int) = FirstTo(
            numberOfSets, numberOfLegs + change,
        )

        override fun withNumberOfSetsChanged(change: Int) = FirstTo(
            numberOfSets + change, numberOfLegs,
        )
    }

    class BestOf(numberOfSets: Int, numberOfLegs: Int) :
        MatchResolutionStrategy(numberOfSets, numberOfLegs) {

        override fun resolutionPredicate() = { sets: Int, legs: Int ->
            sets >= numberOfSets / 2 && legs >= numberOfLegs / 2
        }

        override fun withNumberOfLegsChanged(change: Int) = BestOf(
            numberOfSets, numberOfLegs + change * 2,
        )

        override fun withNumberOfSetsChanged(change: Int) = BestOf(
            numberOfSets + change * 2, numberOfLegs,
        )
    }

    enum class Type {
        FirstTo, BestOf
    }

    companion object {
        const val MIN_SETS = 1
        const val MAX_SETS = 5

        const val MIN_LEGS = 1
        const val MAX_LEGS = 9

        const val DEFAULT_NUMBER_OF_SETS = 1
        const val DEFAULT_NUMBER_OF_LEGS = 3
    }
}
