package com.pointlessapps.dartify.domain.game.x01.turn

import com.pointlessapps.dartify.datasource.game.x01.move.TurnDataSource
import com.pointlessapps.dartify.domain.game.x01.model.Player
import com.pointlessapps.dartify.domain.game.x01.turn.mappers.toPlayerScore
import com.pointlessapps.dartify.domain.game.x01.turn.model.CurrentState
import com.pointlessapps.dartify.domain.game.x01.turn.model.MatchResolutionStrategy
import com.pointlessapps.dartify.errors.game.x01.move.EmptyPlayersListException

interface TurnRepository {
    fun getStartingState(): CurrentState

    fun setStartingScore(score: Int)
    fun setPlayers(players: List<Player>)
    fun setMatchResolutionStrategy(
        numberOfSets: Int,
        numberOfLegs: Int,
        matchResolutionStrategy: MatchResolutionStrategy,
    )

    fun addInput(score: Int, numberOfThrows: Int, numberOfThrowsOnDouble: Int)
    fun undo(): CurrentState
    fun nextTurn(): CurrentState
    fun finishLeg(numberOfThrows: Int, numberOfThrowsOnDouble: Int): CurrentState
}

internal class TurnRepositoryImpl(
    private val turnDataSource: TurnDataSource,
) : TurnRepository {

    private lateinit var matchResolutionStrategy: MatchResolutionStrategy
    private var numberOfSets = 0
    private var numberOfLegs = 0

    private var startingPlayerIndex = 0

    private val players = mutableListOf<Player>()
    private var currentPlayer: Player? = null

    override fun getStartingState() = CurrentState(
        set = 1,
        leg = 1,
        player = players[startingPlayerIndex],
        playerScores = turnDataSource.getPlayerScores().map {
            it.toPlayerScore(players.associateBy(Player::id))
        },
    )

    override fun setStartingScore(score: Int) {
        turnDataSource.setStartingScore(score)
    }

    override fun setPlayers(players: List<Player>) {
        if (players.isEmpty()) {
            throw EmptyPlayersListException()
        }

        startingPlayerIndex = 0
        this.players.clear()
        this.players.addAll(players)
        this.currentPlayer = players.first()
        turnDataSource.setPlayers(players.map(Player::id))
    }

    override fun setMatchResolutionStrategy(
        numberOfSets: Int,
        numberOfLegs: Int,
        matchResolutionStrategy: MatchResolutionStrategy,
    ) {
        this.numberOfSets = numberOfSets
        this.numberOfLegs = numberOfLegs
        this.matchResolutionStrategy = matchResolutionStrategy
    }

    override fun addInput(
        score: Int,
        numberOfThrows: Int,
        numberOfThrowsOnDouble: Int,
    ) {
        if (currentPlayer == null) {
            throw IllegalStateException("currentPlayer cannot be null")
        }

        turnDataSource.addInput(
            requireNotNull(currentPlayer).id,
            score,
            numberOfThrows,
            numberOfThrowsOnDouble,
        )
    }

    override fun undo(): CurrentState {
        if (currentPlayer == null) {
            throw IllegalStateException("currentPlayer cannot be null")
        }

        val currentPlayerIndex = players.indexOf(currentPlayer)
        var previousPlayer = players[currentPlayerIndex.prevPlayerIndex()]

        if (turnDataSource.hasNoInputs(previousPlayer.id)) {
            return CurrentState(score = 0)
        }

        // Set or leg was reverted
        if (turnDataSource.hasNoInputsInThisLeg(previousPlayer.id)) {
            if (turnDataSource.hasWonPreviousLeg(requireNotNull(currentPlayer).id)) {
                previousPlayer = requireNotNull(currentPlayer)
            }
            startingPlayerIndex = startingPlayerIndex.prevPlayerIndex()

            players.forEach {
                if (it != previousPlayer) {
                    turnDataSource.revertLeg(it.id)
                }
            }
        }

        val previousPlayerInputScore = turnDataSource.popInput(previousPlayer.id)

        val currentSet = turnDataSource.getWonSets() + 1
        val currentLeg = turnDataSource.getWonLegs() + 1

        currentPlayer = previousPlayer

        return CurrentState(
            score = previousPlayerInputScore,
            leg = currentLeg,
            set = currentSet,
            player = previousPlayer,
            playerScores = turnDataSource.getPlayerScores().map {
                it.toPlayerScore(players.associateBy(Player::id))
            },
        )
    }

    override fun nextTurn(): CurrentState {
        if (currentPlayer == null) {
            throw IllegalStateException("currentPlayer cannot be null")
        }

        val currentPlayerIndex = players.indexOf(currentPlayer)
        val nextPlayer = players[currentPlayerIndex.nextPlayerIndex()]

        currentPlayer = nextPlayer

        return CurrentState(
            score = 0,
            player = nextPlayer,
            playerScores = turnDataSource.getPlayerScores().map {
                it.toPlayerScore(players.associateBy(Player::id))
            },
        )
    }

    override fun finishLeg(numberOfThrows: Int, numberOfThrowsOnDouble: Int): CurrentState {
        if (currentPlayer == null) {
            throw IllegalStateException("currentPlayer cannot be null")
        }

        val currentPlayerId = requireNotNull(currentPlayer).id

        val isSetFinished = matchResolutionStrategy
            .resolutionPredicate(numberOfLegs)
            .invoke(turnDataSource.getWonLegs(currentPlayerId) + 1)

        val isMatchFinished = isSetFinished && matchResolutionStrategy
            .resolutionPredicate(numberOfSets)
            .invoke(turnDataSource.getWonSets(currentPlayerId) + 1)

        turnDataSource.addInput(
            currentPlayerId,
            turnDataSource.getScoreLeft(currentPlayerId),
            numberOfThrows,
            numberOfThrowsOnDouble,
        )

        if (isSetFinished) {
            turnDataSource.finishSet(winnerId = currentPlayerId)
        } else {
            turnDataSource.finishLeg(winnerId = currentPlayerId)
        }

        if (isMatchFinished) {
            return CurrentState(player = currentPlayer, won = true)
        }

        startingPlayerIndex = startingPlayerIndex.nextPlayerIndex()

        val nextPlayer = players[startingPlayerIndex]
        currentPlayer = nextPlayer

        return CurrentState(
            score = 0,
            set = turnDataSource.getWonSets() + 1,
            leg = turnDataSource.getWonLegs() + 1,
            player = nextPlayer,
            playerScores = turnDataSource.getPlayerScores().map {
                it.toPlayerScore(players.associateBy(Player::id))
            },
        )
    }

    private fun Int.nextPlayerIndex() = (this + 1) % players.size
    private fun Int.prevPlayerIndex() = (this + players.size - 1) % players.size
}
