package com.pointlessapps.dartify.domain.database.game.usecase

import com.pointlessapps.dartify.domain.database.game.GameRepository
import com.pointlessapps.dartify.domain.database.game.model.ActiveGame
import com.pointlessapps.dartify.domain.database.game.x01.usecase.LoadGameX01UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest

class LoadGameUseCase(
    private val gameRepository: GameRepository,
    private val loadGameX01UseCase: LoadGameX01UseCase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(gameId: Long, gameType: ActiveGame.Type) =
        gameRepository.deleteActiveGame(gameId)
            .flatMapLatest {
                when (gameType) {
                    ActiveGame.Type.X01 -> loadGameX01UseCase(gameId)
                }
            }
}
