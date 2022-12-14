package com.pointlessapps.dartify.domain.game.x01.turn

import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01Input
import com.pointlessapps.dartify.datasource.game.x01.turn.TurnDataSource
import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.model.InputScore
import com.pointlessapps.dartify.domain.game.x01.turn.mappers.fromInputScore
import com.pointlessapps.dartify.domain.game.x01.turn.mappers.toInputScore
import com.pointlessapps.dartify.domain.game.x01.turn.mappers.toPlayerScore
import com.pointlessapps.dartify.domain.game.x01.turn.model.*
import com.pointlessapps.dartify.domain.model.GameMode
import com.pointlessapps.dartify.domain.model.Player
import com.pointlessapps.dartify.errors.game.x01.move.EmptyPlayersListException

interface TurnRepository {
    /**
     * Returns the state of the game, containing information like starting score, check-in mode
     * or scores of the players.
     */
    fun getGameState(): GameState

    /**
     * Sets up all of the functionality for the game and returns back the state
     */
    fun setup(
        players: List<Player>,
        startingScore: Int,
        inMode: GameMode,
        numberOfSets: Int,
        numberOfLegs: Int,
        matchResolutionStrategy: MatchResolutionStrategy,
    ): CurrentState

    /**
     * Performs an insertion of the score into the current player's account
     */
    fun addInput(score: InputScore, numberOfThrows: Int, numberOfThrowsOnDouble: Int)

    /**
     * Reverts a turn by removing the previous input and sets the current player to the previous one
     * returning the state after the changes
     */
    fun undoTurn(): CurrentState

    /**
     * Marks the current turn as complete and sets the current player to the next one
     * returning the state after the changes
     */
    fun nextTurn(): CurrentState

    /**
     * Adds the input to the current player's account and marks the current leg as complete.
     * Also performs a check to see if the match is finished, if so returns an applicable [State]
     */
    fun finishLeg(inputScore: InputScore, numberOfThrows: Int, numberOfThrowsOnDouble: Int): State

    /**
     * Performs a set of checks to see if the current action should be to ask the player for an
     * additional input (as number of throws or number of doubles, or both) and returns an
     * appropriate [DoneTurnEvent]
     */
    fun doneTurn(
        inputScore: InputScore,
        shouldAskForNumberOfDoubles: Boolean,
        minNumberOfThrows: Int,
        maxNumberOfDoubles: Map<Int, Int>,
    ): DoneTurnEvent

    /**
     * Resets the state of the game to the state after the [setup] function call
     */
    fun reset(): CurrentState
}

internal class TurnRepositoryImpl(
    private val turnDataSource: TurnDataSource,
) : TurnRepository {

    private lateinit var inMode: GameMode
    private var startingScore = 0

    private var numberOfSets = 0
    private var numberOfLegs = 0
    private lateinit var matchResolutionStrategy: MatchResolutionStrategy

    private lateinit var currentPlayer: Player
    private lateinit var players: List<Player>
    private val playersById: Map<Long, Player>
        get() = players.associateBy(Player::id)

    override fun getGameState() = GameState(
        inMode = inMode,
        startingScore = startingScore,
        numberOfSets = numberOfSets,
        numberOfLegs = numberOfLegs,
        player = currentPlayer,
        playerScores = turnDataSource.getPlayerScores().map {
            it.toPlayerScore(playersById)
        },
        matchResolutionStrategy = matchResolutionStrategy,
    )

    fun load(
        players: List<Player>,
        currentPlayer: Player,
        startingScore: Int,
        inMode: GameMode,
        numberOfSets: Int,
        numberOfLegs: Int,
        matchResolutionStrategy: MatchResolutionStrategy,
        inputsHistory: List<GameX01Input>,
    ): CurrentState {
        setup(
            players,
            startingScore,
            inMode,
            numberOfSets,
            numberOfLegs,
            matchResolutionStrategy,
        )
        this.currentPlayer = currentPlayer

        turnDataSource.load(startingScore, players.map(Player::id), inputsHistory)

        return CurrentState(
            score = null,
            set = turnDataSource.getWonSets() + 1,
            leg = turnDataSource.getWonLegs() + 1,
            player = currentPlayer,
            playerScores = turnDataSource.getPlayerScores().map {
                it.toPlayerScore(playersById)
            },
        )
    }

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

        this.players = players
        this.currentPlayer = players.first()
        this.startingScore = startingScore
        this.inMode = inMode
        this.numberOfSets = numberOfSets
        this.numberOfLegs = numberOfLegs
        this.matchResolutionStrategy = matchResolutionStrategy

        turnDataSource.setup(startingScore, players.map(Player::id))

        return CurrentState(
            score = null,
            set = 1,
            leg = 1,
            player = players[0],
            playerScores = turnDataSource.getPlayerScores().map {
                it.toPlayerScore(playersById)
            },
        )
    }

    override fun reset(): CurrentState {
        this.currentPlayer = players.first()
        turnDataSource.setup(startingScore, players.map(Player::id))

        return CurrentState(
            score = null,
            set = 1,
            leg = 1,
            player = players[0],
            playerScores = turnDataSource.getPlayerScores().map {
                it.toPlayerScore(playersById)
            },
        )
    }

    override fun addInput(
        score: InputScore,
        numberOfThrows: Int,
        numberOfThrowsOnDouble: Int,
    ) {
        turnDataSource.addInput(
            currentPlayer.id,
            score.fromInputScore(),
            numberOfThrows,
            numberOfThrowsOnDouble,
        )
    }

    override fun undoTurn(): CurrentState {
        val currentPlayerIndex = players.indexOf(currentPlayer)
        var previousPlayer = players[currentPlayerIndex.prevPlayerIndex()]

        if (turnDataSource.hasNoInputs(previousPlayer.id)) {
            turnDataSource.revertLeg(currentPlayer.id)
            val currentPlayerInputScore = turnDataSource.popInput(currentPlayer.id)

            return CurrentState(
                score = currentPlayerInputScore?.toInputScore(),
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

            players.forEach {
                if (it.id != previousPlayer.id) {
                    turnDataSource.revertLeg(it.id)
                }
            }
        }

        val previousPlayerInputScore = turnDataSource.popInput(previousPlayer.id)

        currentPlayer = previousPlayer

        return CurrentState(
            score = previousPlayerInputScore?.toInputScore(),
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
            score = null,
            set = turnDataSource.getWonSets() + 1,
            leg = turnDataSource.getWonLegs() + 1,
            player = nextPlayer,
            playerScores = turnDataSource.getPlayerScores().map {
                it.toPlayerScore(playersById)
            },
        )
    }

    override fun doneTurn(
        inputScore: InputScore,
        shouldAskForNumberOfDoubles: Boolean,
        minNumberOfThrows: Int,
        maxNumberOfDoubles: Map<Int, Int>,
    ): DoneTurnEvent {
        val currentPlayerScore = requireNotNull(
            turnDataSource.getPlayerScores().find {
                it.playerId == currentPlayer.id
            },
        )

        if (currentPlayerScore.scoreLeft == inputScore.score()) {
            return when {
                shouldAskForNumberOfDoubles && inputScore is InputScore.Dart -> {
                    DoneTurnEvent.AskForNumberOfDoubles(
                        minNumberOfDoubles = 1,
                        maxNumberOfDoubles = maxNumberOfDoubles[inputScore.scores.size] ?: 1,
                    )
                }
                shouldAskForNumberOfDoubles -> {
                    DoneTurnEvent.AskForNumberOfThrowsAndDoubles(
                        minNumberOfThrows = minNumberOfThrows,
                        maxNumberOfDoubles = maxNumberOfDoubles,
                    )
                }
                else -> {
                    DoneTurnEvent.AskForNumberOfThrows(
                        minNumberOfThrows = minNumberOfThrows,
                    )
                }
            }
        }

        if (shouldAskForNumberOfDoubles) {
            return DoneTurnEvent.AskForNumberOfDoubles(
                minNumberOfDoubles = 0,
                maxNumberOfDoubles = maxNumberOfDoubles[DEFAULT_NUMBER_OF_THROWS] ?: 1,
            )
        }

        return DoneTurnEvent.AddInput(
            when (inputScore) {
                is InputScore.Dart -> inputScore.withFixedSize(DEFAULT_NUMBER_OF_THROWS)
                else -> inputScore
            },
        )
    }

    override fun finishLeg(
        inputScore: InputScore,
        numberOfThrows: Int,
        numberOfThrowsOnDouble: Int,
    ): State {
        val currentPlayerId = currentPlayer.id

        val isSetFinished = matchResolutionStrategy
            .resolutionPredicate(numberOfLegs)
            .invoke(turnDataSource.getWonLegs(currentPlayerId) + 1)

        val isMatchFinished = isSetFinished && matchResolutionStrategy
            .resolutionPredicate(numberOfSets)
            .invoke(turnDataSource.getWonSets(currentPlayerId) + 1)

        turnDataSource.addInput(
            currentPlayerId,
            inputScore.fromInputScore(),
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

        val nextPlayer = players[turnDataSource.getNumberOfLegsPlayed() % players.size]
        currentPlayer = nextPlayer

        return CurrentState(
            score = null,
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

    fun getAllInputsHistory() = turnDataSource.getAllInputsHistory()
}
