package com.pointlessapps.dartify.compose.game.mappers

import com.pointlessapps.dartify.domain.game.x01.model.InputScore
import com.pointlessapps.dartify.compose.game.active.x01.model.InputScore as ViewInputScore

internal fun ViewInputScore.toInputScore() = when (this) {
    is ViewInputScore.Dart -> InputScore.Dart(this.scores)
    is ViewInputScore.Turn -> InputScore.Turn(this.score)
}

internal fun InputScore.fromInputScore() = when (this) {
    is InputScore.Dart -> ViewInputScore.Dart(this.scores)
    is InputScore.Turn -> ViewInputScore.Turn(this.score)
}
