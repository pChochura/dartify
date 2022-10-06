package com.pointlessapps.dartify.compose.game.active.x01.model

import androidx.annotation.FloatRange
import com.pointlessapps.dartify.compose.game.model.Player

internal data class PlayerScore(val player: Player, private val startingScore: Int) {

    private val previousInputs = mutableListOf<List<Input>>()
    var wonSets: Int = 0
        private set
    var wonLegs: Int = 0
        private set

    private val pastInputs: MutableList<Input> = mutableListOf()

    private val allInputs: List<Input>
        get() = previousInputs.flatten() + pastInputs

    private var doubleThrowTries: Int = 0
    private var doubleThrowTriesSucceeded: Int = 0
    val doublePercentage: Float
        get() = if (doubleThrowTries != 0) {
            doubleThrowTriesSucceeded.toFloat() / doubleThrowTries
        } else {
            0f
        }

    val max: Int
        get() = allInputs.maxOfOrNull(Input::score) ?: 0

    val average: Float
        get() = allInputs.let {
            if (it.isEmpty()) {
                0f
            } else {
                it.sumOf(Input::score).toFloat() / it.sumOf(Input::weight)
            }
        }

    val numberOfDarts: Int
        get() = pastInputs.sumOf(Input::weight)

    val scoreLeft: Int
        get() = startingScore - pastInputs.sumOf(Input::score)

    fun addInput(score: Int, @FloatRange(from = 1.0, to = 3.0) weight: Int = 3) {
        pastInputs.add(Input(score, weight))
    }

    fun addDoubleThrowTries(amount: Int) {
        doubleThrowTries += amount
    }

    fun popInput() = pastInputs.removeLastOrNull()?.score ?: 0

    fun markLegAsFinished(won: Boolean) {
        if (won) {
            wonLegs++
            doubleThrowTriesSucceeded++
        }
        previousInputs.add(ArrayList(pastInputs))
        pastInputs.clear()
    }

    fun markSetAsFinished(won: Boolean) {
        if (won) {
            wonSets++
            doubleThrowTriesSucceeded++
        }
        wonLegs = 0
        previousInputs.add(ArrayList(pastInputs))
        pastInputs.clear()
    }

    //@Suppress("MagicNumber")
    //private fun Float.isDouble() = this in (1..20).map { it * 2 } || this == 50

    private data class Input(val score: Int, @FloatRange(from = 1.0, to = 3.0) val weight: Int)
}
