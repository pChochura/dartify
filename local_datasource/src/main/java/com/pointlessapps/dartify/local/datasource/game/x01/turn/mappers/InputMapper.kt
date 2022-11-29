package com.pointlessapps.dartify.local.datasource.game.x01.turn.mappers

import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01Input
import com.pointlessapps.dartify.local.datasource.game.x01.turn.model.Input
import com.pointlessapps.dartify.local.datasource.game.x01.turn.model.InputHistoryEvent

internal fun List<InputHistoryEvent>.toInputs(playerId: Long): List<GameX01Input> {
    var legIndex = 0
    var setIndex = 0
    var order = 0

    return flatMap { event ->
        when (event) {
            is InputHistoryEvent.LegFinished -> {
                legIndex++
                event.inputs.map {
                    GameX01Input(
                        playerId = playerId,
                        score = it.score,
                        numberOfThrows = it.numberOfThrows,
                        numberOfThrowsOnDouble = it.numberOfThrowsOnDouble,
                        won = event.won,
                        legIndex = legIndex,
                        setIndex = setIndex,
                        order = order.also { order++ },
                    )
                }
            }
            is InputHistoryEvent.SetFinished -> {
                setIndex++
                legIndex = 0
                emptyList()
            }
            is InputHistoryEvent.CurrentLeg -> event.inputs.map {
                GameX01Input(
                    playerId = playerId,
                    score = it.score,
                    numberOfThrows = it.numberOfThrows,
                    numberOfThrowsOnDouble = it.numberOfThrowsOnDouble,
                    won = false,
                    legIndex = -1,
                    setIndex = -1,
                    order = order.also { order++ },
                )
            }
        }
    }
}

internal data class LegSet(val leg: Int, val set: Int)

internal sealed interface InputsType {
    data class CurrentLeg(val inputs: List<Input>) : InputsType
    data class LegFinished(val inputs: List<Input>, val set: Int, val won: Boolean) : InputsType
}

internal fun List<GameX01Input>.toInputHistoryEvent(): List<InputHistoryEvent> {
    val inputs = groupBy { LegSet(it.legIndex, it.setIndex) }.map { (legSet, inputs) ->
        if (legSet.leg == -1 && legSet.set == -1) {
            InputsType.CurrentLeg(inputs.map { it.toInput() })
        } else {
            InputsType.LegFinished(
                inputs = inputs.map { it.toInput() },
                set = legSet.set,
                won = inputs.firstOrNull()?.won ?: false,
            )
        }
    }

    val inputHistoryEvents = mutableListOf<InputHistoryEvent>()
    inputs.forEachIndexed { index, inputsType ->
        val previousInputsType = inputs.getOrNull(index - 1) as? InputsType.LegFinished
        when (inputsType) {
            is InputsType.LegFinished -> {
                if (previousInputsType != null && previousInputsType.set != inputsType.set) {
                    inputHistoryEvents.add(InputHistoryEvent.SetFinished(previousInputsType.won))
                }
                inputHistoryEvents.add(
                    InputHistoryEvent.LegFinished(inputsType.inputs, inputsType.won),
                )
            }
            is InputsType.CurrentLeg -> inputHistoryEvents.add(
                InputHistoryEvent.CurrentLeg(inputsType.inputs),
            )
        }
    }

    return inputHistoryEvents
}

private fun GameX01Input.toInput() = Input(
    score = score,
    numberOfThrows = numberOfThrows,
    numberOfThrowsOnDouble = numberOfThrowsOnDouble,
)
