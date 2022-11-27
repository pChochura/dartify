package com.pointlessapps.dartify.local.datasource.game.x01.turn.model

internal sealed interface InputHistoryEvent {
    data class LegFinished(val inputs: List<Input>, val won: Boolean) : InputHistoryEvent
    data class SetFinished(val won: Boolean) : InputHistoryEvent
    data class CurrentLeg(val inputs: List<Input>) : InputHistoryEvent
}
