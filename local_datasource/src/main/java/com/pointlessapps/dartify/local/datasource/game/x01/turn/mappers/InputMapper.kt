package com.pointlessapps.dartify.local.datasource.game.x01.turn.mappers

import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01Input
import com.pointlessapps.dartify.datasource.game.x01.turn.model.InputScore
import com.pointlessapps.dartify.local.datasource.game.x01.turn.model.Input
import com.pointlessapps.dartify.local.datasource.game.x01.turn.model.InputHistoryEvent

internal fun List<InputHistoryEvent>.toInputs(playerId: Long): List<GameX01Input> {
    var legIndex = 0
    var order = 0

    return flatMap { event ->
        when (event) {
            is InputHistoryEvent.LegFinished -> event.inputs.map {
                GameX01Input(
                    playerId = playerId,
                    score = it.score,
                    numberOfThrows = it.numberOfThrows,
                    numberOfThrowsOnDouble = it.numberOfThrowsOnDouble,
                    type = GameX01Input.Type.LEG_FINISHED,
                    won = event.won,
                    legIndex = legIndex,
                    order = order.also {
                        order++
                    },
                )
            }.also {
                legIndex++
            }
            is InputHistoryEvent.SetFinished -> listOf(
                GameX01Input(
                    playerId = playerId,
                    score = InputScore.Turn(0),
                    numberOfThrows = 0,
                    numberOfThrowsOnDouble = 0,
                    type = GameX01Input.Type.SET_FINISHED,
                    won = event.won,
                    legIndex = legIndex,
                    order = order.also {
                        order++
                    },
                ),
            )
            is InputHistoryEvent.CurrentLeg -> event.inputs.map {
                GameX01Input(
                    playerId = playerId,
                    score = it.score,
                    numberOfThrows = it.numberOfThrows,
                    numberOfThrowsOnDouble = it.numberOfThrowsOnDouble,
                    type = GameX01Input.Type.CURRENT_LEG,
                    won = false,
                    legIndex = legIndex,
                    order = order.also {
                        order++
                    },
                )
            }
        }
    }
}

internal fun List<GameX01Input>.toInputHistoryEvent(): List<InputHistoryEvent> {
    val groupedInputs = fold(
        emptyList<Pair<Int, MutableList<GameX01Input>>>(),
    ) { acc, input ->
        return@fold if (acc.lastOrNull()?.first != input.legIndex) {
            acc + (input.legIndex to mutableListOf(input))
        } else {
            acc.also { it.last().second.add(input) }
        }
    }

    return groupedInputs.mapNotNull { (_, inputs) ->
        when (inputs.firstOrNull()?.type) {
            GameX01Input.Type.LEG_FINISHED -> InputHistoryEvent.LegFinished(
                inputs = inputs.map { it.toInput() },
                won = inputs.first().won,
            )
            GameX01Input.Type.SET_FINISHED -> InputHistoryEvent.SetFinished(
                won = inputs.first().won,
            )
            GameX01Input.Type.CURRENT_LEG -> InputHistoryEvent.CurrentLeg(
                inputs = inputs.map { it.toInput() },
            )
            else -> null
        }
    }
}

private fun GameX01Input.toInput() = Input(
    score = score,
    numberOfThrows = numberOfThrows,
    numberOfThrowsOnDouble = numberOfThrowsOnDouble,
)
