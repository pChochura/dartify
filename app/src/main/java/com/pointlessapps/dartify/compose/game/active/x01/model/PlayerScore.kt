package com.pointlessapps.dartify.compose.game.active.x01.model

import androidx.annotation.FloatRange
import com.pointlessapps.dartify.compose.game.model.Player

internal data class PlayerScore(val player: Player, private val startingScore: Int) {

    private val previousInputs = mutableListOf<InputHistoryEvent>()
    private val inputs: MutableList<Input> = mutableListOf()

    private val allInputs: List<Input>
        get() = previousInputs.filterIsInstance<InputHistoryEvent.LegFinished>()
            .flatMap { it.inputs } + inputs

    private val doubleThrowTries: Int
        get() = allInputs.sumOf(Input::doubleThrowTries)
    private val doubleThrowTriesSucceeded: Int
        get() = previousInputs
            .filterIsInstance<InputHistoryEvent.LegFinished>()
            .count { it.won }

    val wonSets: Int
        get() = previousInputs.filterIsInstance<InputHistoryEvent.SetFinished>().count { it.won }
    val wonLegs: Int
        get() = previousInputs
            .takeLastWhile { it !is InputHistoryEvent.SetFinished }
            .filterIsInstance<InputHistoryEvent.LegFinished>()
            .count { it.won }

    val doublePercentage: Float
        get() = doubleThrowTries.takeIf { it != 0 }?.let {
            doubleThrowTriesSucceeded.toFloat() / it
        } ?: 0f

    val max: Int
        get() = allInputs.maxOfOrNull(Input::score) ?: 0

    val average: Float
        get() = allInputs.takeIf { it.isNotEmpty() }?.let {
            it.sumOf(Input::score).toFloat() / it.size
        } ?: 0f

    val numberOfDarts: Int
        get() = inputs.sumOf(Input::weight)

    val scoreLeft: Int
        get() = startingScore - inputs.sumOf(Input::score)

    val lastScore: Int?
        get() = inputs.lastOrNull()?.score

    fun hasNoInputs() = allInputs.isEmpty()

    fun hasWonPreviousLeg() = previousInputs.lastOrNull()?.let {
        it is InputHistoryEvent.LegFinished && it.won ||
                it is InputHistoryEvent.SetFinished && it.won
    } ?: false

    fun addInput(
        score: Int,
        @FloatRange(from = 1.0, to = 3.0) weight: Int = 3,
        doubleThrowTries: Int = 0,
    ) {
        inputs.add(Input(score, weight, doubleThrowTries))
    }

    fun popInput(): Int {
        val input = inputs.removeLastOrNull()?.score
        if (input == null) {
            val previousInputs = previousInputs.removeLastOrNull() ?: return 0
            inputs.addAll(
                when (previousInputs) {
                    is InputHistoryEvent.SetFinished ->
                        (this.previousInputs.removeLast() as InputHistoryEvent.LegFinished).inputs
                    is InputHistoryEvent.LegFinished -> previousInputs.inputs
                },
            )

            return inputs.removeLastOrNull()?.score ?: 0
        }

        return input
    }

    fun markLegAsReverted() {
        if (previousInputs.lastOrNull() is InputHistoryEvent.SetFinished) {
            previousInputs.removeLastOrNull()
        }

        val previousInputs = previousInputs.removeLastOrNull()
                as? InputHistoryEvent.LegFinished ?: return
        inputs.addAll(previousInputs.inputs)
    }

    fun markLegAsFinished(won: Boolean) {
        previousInputs.add(InputHistoryEvent.LegFinished(ArrayList(inputs), won))
        inputs.clear()
    }

    fun markSetAsFinished(won: Boolean) {
        previousInputs.add(InputHistoryEvent.LegFinished(ArrayList(inputs), won))
        previousInputs.add(InputHistoryEvent.SetFinished(wonLegs, won))
        inputs.clear()
    }

    private data class Input(
        val score: Int,
        @FloatRange(from = 1.0, to = 3.0) val weight: Int,
        val doubleThrowTries: Int,
    )

    private sealed interface InputHistoryEvent {
        data class LegFinished(val inputs: List<Input>, val won: Boolean) : InputHistoryEvent
        data class SetFinished(val legs: Int, val won: Boolean) : InputHistoryEvent
    }
}
