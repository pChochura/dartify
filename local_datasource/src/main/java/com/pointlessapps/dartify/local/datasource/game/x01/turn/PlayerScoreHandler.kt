package com.pointlessapps.dartify.local.datasource.game.x01.turn

import com.pointlessapps.dartify.datasource.game.x01.move.model.InputScore
import com.pointlessapps.dartify.local.datasource.game.x01.turn.model.Input
import com.pointlessapps.dartify.local.datasource.game.x01.turn.model.InputHistoryEvent

internal class PlayerScoreHandler(private val startingScore: Int) {

    private val previousInputs = mutableListOf<InputHistoryEvent>()
    private val inputs: MutableList<Input> = mutableListOf()

    private val allInputs: List<Input>
        get() = previousInputs.filterIsInstance<InputHistoryEvent.LegFinished>()
            .flatMap { it.inputs } + inputs

    private val numberOfThrowsOnDouble: Int
        get() = allInputs.sumOf(Input::numberOfThrowsOnDouble)
    private val numberOfThrowsOnDoubleSucceeded: Int
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
        get() = numberOfThrowsOnDouble.takeIf { it != 0 }?.let {
            numberOfThrowsOnDoubleSucceeded.toFloat() / it
        } ?: 0f

    val max: Int
        get() = allInputs.maxOfOrNull { it.score.score() } ?: 0

    val average: Float
        get() = allInputs.takeIf { it.isNotEmpty() }?.let {
            it.fold(0 to 0) { (scores, throws), input ->
                scores + input.score.score() to throws + input.numberOfThrows
            }.let { (scores, throws) ->
                scores * 3f / throws
            }
        } ?: 0f

    val numberOfDarts: Int
        get() = inputs.sumOf(Input::numberOfThrows)

    val scoreLeft: Int
        get() = startingScore - inputs.sumOf { it.score.score() }

    val lastScore: Int?
        get() = inputs.lastOrNull()?.score?.score()

    fun hasNoInputs() = allInputs.isEmpty()

    fun hasWonPreviousLeg() = previousInputs.lastOrNull()?.let {
        it is InputHistoryEvent.LegFinished && it.won ||
                it is InputHistoryEvent.SetFinished && it.won
    } ?: false

    fun addInput(score: InputScore, numberOfThrows: Int, numberOfThrowsOnDouble: Int) {
        inputs.add(Input(score, numberOfThrows, numberOfThrowsOnDouble))
    }

    fun popInput(): InputScore? {
        val input = inputs.removeLastOrNull()?.score
        if (input == null) {
            val previousInputs = previousInputs.removeLastOrNull() ?: return null
            inputs.addAll(
                when (previousInputs) {
                    is InputHistoryEvent.SetFinished ->
                        (this.previousInputs.removeLast() as InputHistoryEvent.LegFinished).inputs
                    is InputHistoryEvent.LegFinished -> previousInputs.inputs
                },
            )

            return inputs.removeLastOrNull()?.score
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
}
