package com.pointlessapps.dartify.compose.game.mappers

import com.pointlessapps.dartify.compose.game.active.x01.model.InputMode
import com.pointlessapps.dartify.domain.game.x01.model.InputScore
import com.pointlessapps.dartify.compose.game.active.x01.model.InputScore as ViewInputScore

internal fun ViewInputScore?.toInputScore(inputMode: InputMode) = when (this) {
    is ViewInputScore.Dart -> InputScore.Dart(this.scores)
    is ViewInputScore.Turn -> InputScore.Turn(this.score)
    null -> when (inputMode) {
        InputMode.PerDart -> InputScore.Dart(emptyList())
        InputMode.PerTurn -> InputScore.Turn(0)
    }
}

internal fun InputScore.fromInputScore() = when (this) {
    is InputScore.Dart -> ViewInputScore.Dart(this.scores)
    is InputScore.Turn -> ViewInputScore.Turn(this.score)
}
