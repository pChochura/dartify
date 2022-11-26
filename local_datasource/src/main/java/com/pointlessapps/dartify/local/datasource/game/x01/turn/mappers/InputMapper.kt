package com.pointlessapps.dartify.local.datasource.game.x01.turn.mappers

import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01Input
import com.pointlessapps.dartify.datasource.game.x01.move.model.InputScore
import com.pointlessapps.dartify.local.datasource.game.x01.turn.model.InputHistoryEvent

internal fun List<InputHistoryEvent>.toInputs(playerId: Long) = flatMap { event ->
    when (event) {
        is InputHistoryEvent.LegFinished -> event.inputs.map {
            GameX01Input(
                playerId = playerId,
                score = it.score,
                numberOfThrows = it.numberOfThrows,
                numberOfThrowsOnDouble = it.numberOfThrowsOnDouble,
                type = GameX01Input.Type.LEG_FINISHED,
                won = event.won,
            )
        }
        is InputHistoryEvent.SetFinished -> listOf(
            GameX01Input(
                playerId = playerId,
                score = InputScore.Turn(0),
                numberOfThrows = 0,
                numberOfThrowsOnDouble = 0,
                type = GameX01Input.Type.SET_FINISHED,
                won = event.won,
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
            )
        }
    }
}
