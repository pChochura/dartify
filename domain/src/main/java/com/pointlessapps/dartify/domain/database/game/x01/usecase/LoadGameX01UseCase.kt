package com.pointlessapps.dartify.domain.database.game.x01.usecase

import com.pointlessapps.dartify.domain.database.game.x01.GameX01Repository

class LoadGameX01UseCase(
    private val gameX01Repository: GameX01Repository,
) {

    operator fun invoke(gameId: Long) =
        gameX01Repository.loadGame(gameId)
}
