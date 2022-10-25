package com.pointlessapps.dartify.domain.game.x01.turn.mappers

import com.pointlessapps.dartify.datasource.game.x01.move.model.InputScore
import com.pointlessapps.dartify.domain.game.x01.model.InputScore as RemoteInputScore

internal fun InputScore.toInputScore() = when (this) {
    is InputScore.Dart -> RemoteInputScore.Dart(this.scores)
    is InputScore.Turn -> RemoteInputScore.Turn(this.score)
}

internal fun RemoteInputScore.fromInputScore() = when (this) {
    is RemoteInputScore.Dart -> InputScore.Dart(this.scores)
    is RemoteInputScore.Turn -> InputScore.Turn(this.score)
}
