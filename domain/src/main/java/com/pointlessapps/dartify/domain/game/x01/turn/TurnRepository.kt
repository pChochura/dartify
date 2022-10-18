package com.pointlessapps.dartify.domain.game.x01.turn

import com.pointlessapps.dartify.datasource.game.x01.move.TurnDataSource
import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.model.Player
import com.pointlessapps.dartify.domain.game.x01.score.model.GameMode
import com.pointlessapps.dartify.domain.game.x01.turn.mappers.toPlayerScore
import com.pointlessapps.dartify.domain.game.x01.turn.model.*
import com.pointlessapps.dartify.errors.game.x01.move.EmptyPlayersListException

interface TurnRepository {
    fun getGameState(): GameState

    fun setup(
        players: List<Player>,
        startingScore: Int,
        inMode: GameMode,
        numberOfSets: Int,
        numberOfLegs: Int,
        matchResolutionStrategy: MatchResolutionStrategy,
    ): CurrentState

    fun addInput(score: Int, numberOfThrows: Int, numberOfThrowsOnDouble: Int)
    fun undoTurn(): CurrentState
    fun nextTurn(): CurrentState
    fun finishLeg(numberOfThrows: Int, numberOfThrowsOnDouble: Int): State

    fun doneTurn(
        score: Int,
        shouldAskForNumberOfDoubles: Boolean,
        minNumberOfThrows: Int,
        maxNumberOfDoubles: Map<Int, Int>,
    ): DoneTurnEvent
}

internal class TurnRepositoryImpl(
    private val turnDataSource: TurnDataSource,
) : TurnRepository {

    private lateinit var inMode: GameMode
    private var startingScore = 0

    private var numberOfSets = 0
    private var numberOfLegs = 0
    private lateinit var matchResolutionStrategy: MatchResolutionStrategy

    private var startingPlayerIndex = 0

    private lateinit var currentPlayer: Player
    private lateinit var players: List<Player>
    private val playersById: Map<Long, Player>
        get() = players.associateBy(Player::id)

    override fun getGameState() = GameState(
        inMode = inMode,
        startingScore = startingScore,
        player = currentPlayer,
        playerScores = turnDataSource.getPlayerScores().map {
            it.toPlayerScore(playersById)
        },
    )

    override fun setup(
        players: List<Player>,
        startingScore: Int,
        inMode: GameMode,
        numberOfSets: Int,
        numberOfLegs: Int,
        matchResolutionStrategy: MatchResolutionStrategy,
    ): CurrentState {
        if (players.isEmpty()) {
            throw EmptyPlayersListException()
        }

        startingPlayerIndex = 0
        this.players = players
        this.currentPlayer = players.first()
        this.startingScore = startingScore
        this.inMode = inMode
        this.numberOfSets = numberOfSets
        this.numberOfLegs = numberOfLegs
        this.matchResolutionStrategy = matchResolutionStrategy

        turnDataSource.setStartingScore(startingScore)
        turnDataSource.setPlayers(players.map(Player::id))

        return CurrentState(
            score = 0,
            set = 1,
            leg = 1,
            player = players[startingPlayerIndex],
            playerScores = turnDataSource.getPlayerScores().map {
                it.toPlayerScore(playersById)
            },
        )
    }

    override fun addInput(
        score: Int,
        numberOfThrows: Int,
        numberOfThrowsOnDouble: Int,
    ) {
        turnDataSource.addInput(
            currentPlayer.id,
            score,
            numberOfThrows,
            numberOfThrowsOnDouble,
        )
    }

    override fun undoTurn(): CurrentState {
        val currentPlayerIndex = players.indexOf(currentPlayer)
        var previousPlayer = players[currentPlayerIndex.prevPlayerIndex()]

        if (turnDataSource.hasNoInputs(previousPlayer.id)) {
            return CurrentState(
                score = 0,
                set = turnDataSource.getWonSets() + 1,
                leg = turnDataSource.getWonLegs() + 1,
                player = currentPlayer,
                playerScores = turnDataSource.getPlayerScores().map {
                    it.toPlayerScore(playersById)
                },
            )
        }

        // Set or leg was reverted
        if (turnDataSource.hasNoInputsInThisLeg(previousPlayer.id)) {
            if (turnDataSource.hasWonPreviousLeg(currentPlayer.id)) {
                previousPlayer = currentPlayer
            }
            startingPlayerIndex = startingPlayerIndex.prevPlayerIndex()

            players.forEach {
                if (it.id != previousPlayer.id) {
                    turnDataSource.revertLeg(it.id)
                }
            }
        }

        val previousPlayerInputScore = turnDataSource.popInput(previousPlayer.id)

        currentPlayer = previousPlayer

        return CurrentState(
            score = previousPlayerInputScore,
            set = turnDataSource.getWonSets() + 1,
            leg = turnDataSource.getWonLegs() + 1,
            player = previousPlayer,
            playerScores = turnDataSource.getPlayerScores().map {
                it.toPlayerScore(playersById)
            },
        )
    }

    override fun nextTurn(): CurrentState {
        val currentPlayerIndex = players.indexOf(currentPlayer)
        val nextPlayer = players[currentPlayerIndex.nextPlayerIndex()]

        currentPlayer = nextPlayer

        return CurrentState(
            score = 0,
            set = turnDataSource.getWonSets() + 1,
            leg = turnDataSource.getWonLegs() + 1,
            player = nextPlayer,
            playerScores = turnDataSource.getPlayerScores().map {
                it.toPlayerScore(playersById)
            },
        )
    }

    override fun doneTurn(
        score: Int,
        shouldAskForNumberOfDoubles: Boolean,
        minNumberOfThrows: Int,
        maxNumberOfDoubles: Map<Int, Int>,
    ): DoneTurnEvent {
        val currentPlayerScore = requireNotNull(
            turnDataSource.getPlayerScores().find {
                it.playerId == currentPlayer.id
            },
        )

        if (currentPlayerScore.scoreLeft == score) {
            return if (shouldAskForNumberOfDoubles) {
                DoneTurnEvent.AskForNumberOfThrowsAndDoubles(
                    minNumberOfThrows = minNumberOfThrows,
                    maxNumberOfDoubles = maxNumberOfDoubles,
                )
            } else {
                DoneTurnEvent.AskForNumberOfThrows(
                    minNumberOfThrows = minNumberOfThrows,
                )
            }
        }

        if (shouldAskForNumberOfDoubles) {
            return DoneTurnEvent.AskForNumberOfDoubles(
                maxNumberOfDoublesForThreeThrows = maxNumberOfDoubles[DEFAULT_NUMBER_OF_THROWS]
                    ?: 1,
            )
        }

        return DoneTurnEvent.AddInput
    }

    override fun finishLeg(numberOfThrows: Int, numberOfThrowsOnDouble: Int): State {
        val currentPlayerId = currentPlayer.id

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
            return WinState(
                playerScore = requireNotNull(
                    turnDataSource.getPlayerScores().find {
                        it.playerId == currentPlayerId
                    }?.toPlayerScore(playersById),
                ),
            )
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
                it.toPlayerScore(playersById)
            },
        )
    }

    private fun Int.nextPlayerIndex() = (this + 1) % players.size
    private fun Int.prevPlayerIndex() = (this + players.size - 1) % players.size
}