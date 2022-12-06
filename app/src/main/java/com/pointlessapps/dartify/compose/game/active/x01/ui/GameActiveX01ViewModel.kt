package com.pointlessapps.dartify.compose.game.active.x01.ui

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.active.x01.model.InputMode
import com.pointlessapps.dartify.compose.game.active.x01.model.InputScore
import com.pointlessapps.dartify.compose.game.active.x01.model.InputScore.Dart.Companion.MAX_NUMBER_OF_THROWS
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.active.x01.model.score
import com.pointlessapps.dartify.compose.game.mappers.*
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.theme.Route
import com.pointlessapps.dartify.compose.utils.extensions.addDecimal
import com.pointlessapps.dartify.domain.database.game.x01.usecase.SaveCurrentGameUseCase
import com.pointlessapps.dartify.domain.game.x01.score.usecase.IsCheckoutPossibleUseCase
import com.pointlessapps.dartify.domain.game.x01.score.usecase.ValidateScoreUseCase
import com.pointlessapps.dartify.domain.game.x01.score.usecase.ValidateSingleThrowUseCase
import com.pointlessapps.dartify.domain.game.x01.turn.model.CurrentState
import com.pointlessapps.dartify.domain.game.x01.turn.model.DoneTurnEvent
import com.pointlessapps.dartify.domain.game.x01.turn.model.WinState
import com.pointlessapps.dartify.domain.game.x01.turn.usecase.AddInputUseCase
import com.pointlessapps.dartify.domain.game.x01.turn.usecase.FinishLegUseCase
import com.pointlessapps.dartify.domain.game.x01.turn.usecase.SetupGameUseCase
import com.pointlessapps.dartify.domain.game.x01.turn.usecase.TurnUseCases
import com.pointlessapps.dartify.domain.vibration.usecase.VibrateUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

internal sealed interface GameActiveX01Event {
    data class Navigate(val route: Route) : GameActiveX01Event
    object NavigateBack : GameActiveX01Event

    data class AskForNumberOfThrowsAndDoubles(
        val minNumberOfThrows: Int,
        val maxNumberOfDoubles: Map<Int, Int>,
    ) : GameActiveX01Event

    data class AskForNumberOfThrows(val minNumberOfThrows: Int) : GameActiveX01Event

    data class AskForNumberOfDoubles(
        val minNumberOfDoubles: Int,
        val maxNumberOfDoubles: Int,
    ) : GameActiveX01Event

    data class ShowWinnerDialog(val playerScore: PlayerScore) : GameActiveX01Event

    object ShowCorruptedGameDialog : GameActiveX01Event

    data class ShowSnackbar(@StringRes val message: Int) : GameActiveX01Event
}

@Immutable
@Parcelize
internal data class GameActiveX01State(
    val isLoading: Boolean = false,
    val isGameFinished: Boolean = false,
    val currentSet: Int = 1,
    val currentLeg: Int = 1,
    val playersScores: List<PlayerScore> = emptyList(),
    val currentPlayer: Player? = null,
    val currentInputScore: InputScore? = null,
) : Parcelable

internal class GameActiveX01ViewModel(
    private val validateSingleThrowUseCase: ValidateSingleThrowUseCase,
    private val validateScoreUseCase: ValidateScoreUseCase,
    private val isCheckoutPossibleUseCase: IsCheckoutPossibleUseCase,
    private val turnUseCases: TurnUseCases,
    private val addInputUseCase: AddInputUseCase,
    private val finishLegUseCase: FinishLegUseCase,
    private val setupGameUseCase: SetupGameUseCase,
    private val vibrateUseCase: VibrateUseCase,
    private val saveCurrentGameUseCase: SaveCurrentGameUseCase,
) : ViewModel() {

    private lateinit var gameSettings: GameSettings

    var state by mutableStateOf(GameActiveX01State())
        private set

    var inputModes = mutableStateMapOf<Long, InputMode>()
        private set

    private var startOverAfterFurtherInput = false

    private val eventChannel = Channel<GameActiveX01Event>()
    val events = eventChannel.receiveAsFlow()

    fun setGameSettings(gameSettings: GameSettings) {
        this.gameSettings = gameSettings
        when (gameSettings) {
            is GameSettings.NewGame -> setupGame(gameSettings)
            is GameSettings.LoadGame -> loadGame(gameSettings)
        }
    }

    private fun setupGame(gameSettings: GameSettings.NewGame) {
        val currentState = setupGameUseCase.setupNewGame(
            gameSettings.players.map(Player::toPlayer),
            gameSettings.startingScore,
            gameSettings.inMode.toInMode(),
            gameSettings.numberOfSets,
            gameSettings.numberOfLegs,
            gameSettings.matchResolutionStrategy.toMatchResolutionStrategy(),
        )

        inputModes.putAll(
            gameSettings.players.associate { it.id to InputMode.PerTurn },
        )

        state = state.copy(
            currentInputScore = currentState.score?.fromInputScore(),
            currentSet = currentState.set,
            currentLeg = currentState.leg,
            currentPlayer = currentState.player.fromPlayer(),
            playersScores = currentState.playerScores.map { it.fromPlayerScore() },
        )
    }

    private fun loadGame(gameSettings: GameSettings.LoadGame) {
        setupGameUseCase.loadGame(gameSettings.id, gameSettings.type.toActiveGameType())
            .take(1)
            .onStart {
                state = state.copy(isLoading = true)
            }
            .onEach { currentState ->
                inputModes.putAll(
                    currentState.playerScores
                        .map { it.player }
                        .associate { it.id to InputMode.PerTurn },
                )
                state = state.copy(
                    isLoading = false,
                    currentInputScore = currentState.score?.fromInputScore(),
                    currentSet = currentState.set,
                    currentLeg = currentState.leg,
                    currentPlayer = currentState.player.fromPlayer(),
                    playersScores = currentState.playerScores.map { it.fromPlayerScore() },
                )
            }
            .catch {
                it.printStackTrace()
                eventChannel.send(GameActiveX01Event.ShowCorruptedGameDialog)
                state = state.copy(isLoading = false)
            }
            .launchIn(viewModelScope)
    }

    fun onPossibleCheckoutRequested(): Int? {
        val currentPlayerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        } ?: return null

        if (
            isCheckoutPossibleUseCase(
                currentPlayerScore.scoreLeft,
                outMode = currentPlayerScore.player.outMode.toOutMode(),
            )
        ) {
            return currentPlayerScore.scoreLeft
        }

        return null
    }

    fun onScoreLeftRequested(player: Player): Int {
        val playerScore = state.playersScores.find {
            it.player == player
        } ?: return 0

        return playerScore.scoreLeft - if (player.id == state.currentPlayer?.id) {
            state.currentInputScore.score()
        } else {
            0
        }
    }

    fun onQuickScoreClicked(quickScore: Int) {
        vibrateUseCase.tick()
        if (!validateScoreUseCase(quickScore)) {
            return
        }

        startOverAfterFurtherInput = false
        state = state.copy(
            currentInputScore = InputScore.Turn(quickScore),
        )

        onDoneClicked()
    }

    fun onKeyClicked(key: Int, multiplier: Int = 1) {
        if (!validateKey(key, multiplier)) {
            vibrateUseCase.error()
            viewModelScope.launch {
                eventChannel.send(
                    GameActiveX01Event.ShowSnackbar(
                        when {
                            !validateScoreUseCase.isCheckInSatisfied(multiplier) ->
                                R.string.you_have_to_satisfy_check_in
                            !validateScoreUseCase.isCheckOutSatisfied(key, multiplier) ->
                                R.string.you_have_to_satisfy_check_out
                            else -> R.string.score_inputted_is_incorrect
                        },
                    ),
                )
            }

            return
        }

        val currentScore = if (startOverAfterFurtherInput) {
            InputScore.Turn(0)
        } else {
            state.currentInputScore
        }
        startOverAfterFurtherInput = false

        if (
            getCurrentInputMode() == InputMode.PerDart &&
            (currentScore as? InputScore.Dart)?.scores?.size == MAX_NUMBER_OF_THROWS
        ) {
            vibrateUseCase.error()
            viewModelScope.launch {
                eventChannel.send(
                    GameActiveX01Event.ShowSnackbar(R.string.you_can_input_three_values),
                )
            }

            return
        }

        vibrateUseCase.tick()
        state = state.copy(
            currentInputScore = when (getCurrentInputMode()) {
                InputMode.PerTurn -> InputScore.Turn(
                    currentScore.score().addDecimal(key),
                )
                InputMode.PerDart -> {
                    InputScore.Dart(
                        when {
                            currentScore is InputScore.Dart -> currentScore.scores
                            currentScore is InputScore.Turn && currentScore.score != 0 ->
                                listOf(currentScore.score)
                            else -> emptyList()
                        } + key,
                    )
                }
            },
        )
    }

    fun onUndoClicked() {
        vibrateUseCase.click()
        if (invokeSingleUndoAction()) {
            return
        }

        startOverAfterFurtherInput = true
        val currentState = turnUseCases.undoTurn()
        state = state.copy(
            isGameFinished = false,
            currentInputScore = currentState.score?.fromInputScore(),
            currentSet = currentState.set,
            currentLeg = currentState.leg,
            currentPlayer = currentState.player.fromPlayer(),
            playersScores = currentState.playerScores.map { it.fromPlayerScore() },
        )
    }

    private fun invokeSingleUndoAction(): Boolean {
        when (getCurrentInputMode()) {
            InputMode.PerDart -> {
                val score = state.currentInputScore
                if (startOverAfterFurtherInput) {
                    startOverAfterFurtherInput = false
                    state = state.copy(
                        currentInputScore = null,
                    )

                    return true
                } else if (score is InputScore.Dart && score.scores.isNotEmpty()) {
                    state = state.copy(
                        currentInputScore = InputScore.Dart(score.scores.dropLast(1)),
                    )

                    return true
                } else if (score is InputScore.Turn && score.score != 0) {
                    state = state.copy(
                        currentInputScore = null,
                    )

                    return true
                }
            }
            InputMode.PerTurn -> {
                if (state.currentInputScore.score() != 0) {
                    state = state.copy(
                        currentInputScore = null,
                    )

                    return true
                }
            }
        }

        return false
    }

    fun onDoneClicked() {
        val inputScore = state.currentInputScore.toInputScore(getCurrentInputMode())

        if (!validateScoreUseCase(inputScore)) {
            vibrateUseCase.error()
            viewModelScope.launch {
                eventChannel.send(
                    GameActiveX01Event.ShowSnackbar(R.string.score_inputted_is_incorrect),
                )
            }

            return
        }

        startOverAfterFurtherInput = false
        vibrateUseCase.click()
        viewModelScope.launch {
            when (val event = turnUseCases.doneTurn(inputScore)) {
                is DoneTurnEvent.AskForNumberOfDoubles -> eventChannel.send(
                    GameActiveX01Event.AskForNumberOfDoubles(
                        minNumberOfDoubles = event.minNumberOfDoubles,
                        maxNumberOfDoubles = event.maxNumberOfDoubles,
                    ),
                )
                is DoneTurnEvent.AskForNumberOfThrows -> eventChannel.send(
                    GameActiveX01Event.AskForNumberOfThrows(event.minNumberOfThrows),
                )
                is DoneTurnEvent.AskForNumberOfThrowsAndDoubles -> eventChannel.send(
                    GameActiveX01Event.AskForNumberOfThrowsAndDoubles(
                        minNumberOfThrows = event.minNumberOfThrows,
                        maxNumberOfDoubles = event.maxNumberOfDoubles,
                    ),
                )
                is DoneTurnEvent.AddInput -> {
                    addInputUseCase(event.inputScore)
                    val currentState = turnUseCases.nextTurn()
                    state = state.copy(
                        currentInputScore = currentState.score?.fromInputScore(),
                        currentSet = currentState.set,
                        currentLeg = currentState.leg,
                        currentPlayer = currentState.player.fromPlayer(),
                        playersScores = currentState.playerScores.map { it.fromPlayerScore() },
                    )
                }
            }
        }
    }

    fun onClearClicked() {
        startOverAfterFurtherInput = false
        vibrateUseCase.click()
        state = state.copy(
            currentInputScore = null,
        )
    }

    fun onNumberOfDoublesClicked(numberOfDoubles: Int) {
        val currentPlayerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        }

        vibrateUseCase.click()
        val score = state.currentInputScore
        if (
            score is InputScore.Dart &&
            score.score() == currentPlayerScore?.scoreLeft
        ) {
            invokeFinishLeg(score.scores.size, numberOfDoubles)

            return
        }

        addInputUseCase(
            state.currentInputScore.toInputScore(getCurrentInputMode()),
            numberOfThrowsOnDouble = numberOfDoubles,
        )
        val currentState = turnUseCases.nextTurn()
        state = state.copy(
            currentInputScore = currentState.score?.fromInputScore(),
            currentSet = currentState.set,
            currentLeg = currentState.leg,
            currentPlayer = currentState.player.fromPlayer(),
            playersScores = currentState.playerScores.map { it.fromPlayerScore() },
        )
    }

    fun onNumberOfThrowsClicked(numberOfThrows: Int, numberOfThrowsOnDoubles: Int = 0) {
        vibrateUseCase.click()
        invokeFinishLeg(numberOfThrows, numberOfThrowsOnDoubles)
    }

    private fun invokeFinishLeg(numberOfThrows: Int, numberOfThrowsOnDoubles: Int) {
        when (
            val state = finishLegUseCase(
                inputScore = state.currentInputScore.toInputScore(getCurrentInputMode()),
                numberOfThrows = numberOfThrows,
                numberOfThrowsOnDouble = numberOfThrowsOnDoubles,
            )
        ) {
            is WinState -> {
                this.state = this.state.copy(
                    currentInputScore = null,
                    isGameFinished = true,
                )
                viewModelScope.launch {
                    eventChannel.send(
                        GameActiveX01Event.ShowWinnerDialog(
                            state.playerScore.fromPlayerScore(),
                        ),
                    )
                }
            }
            is CurrentState -> {
                this.state = this.state.copy(
                    currentInputScore = state.score?.fromInputScore(),
                    currentSet = state.set,
                    currentLeg = state.leg,
                    currentPlayer = state.player.fromPlayer(),
                    playersScores = state.playerScores.map { it.fromPlayerScore() },
                )
            }
        }
    }

    fun onChangeInputModeClicked() {
        vibrateUseCase.click()
        inputModes[requireNotNull(state.currentPlayer?.id)] = when (getCurrentInputMode()) {
            InputMode.PerDart -> InputMode.PerTurn
            InputMode.PerTurn -> InputMode.PerDart
        }

        val currentInputMode = getCurrentInputMode()
        val currentInputScoreMode = when (state.currentInputScore) {
            is InputScore.Dart -> InputMode.PerDart
            is InputScore.Turn -> InputMode.PerTurn
            else -> null
        }
        startOverAfterFurtherInput = currentInputMode != currentInputScoreMode
    }

    fun getCurrentFinishSuggestion(): String? {
        val playerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        } ?: return null

        // TODO add actual implementation of this
        if (playerScore.scoreLeft == 101) {
            return "T20 D20 D1"
        }

        return null
    }

    fun getCurrentInputMode() = inputModes[state.currentPlayer?.id] ?: InputMode.PerTurn

    fun onShowGameStatsClicked() {
        // TODO do something
    }

    fun onDiscardAndCloseClicked() {
        vibrateUseCase.click()
        viewModelScope.launch {
            eventChannel.send(GameActiveX01Event.NavigateBack)
        }
    }

    fun onSaveAndCloseClicked() {
        vibrateUseCase.click()
        saveCurrentGameUseCase(
            isGameFinished = state.isGameFinished,
        ).take(1)
            .onStart {
                state = state.copy(isLoading = true)
            }
            .onEach {
                state = state.copy(isLoading = false)
                eventChannel.send(GameActiveX01Event.Navigate(Route.Home))
            }
            .catch {
                it.printStackTrace()
                eventChannel.send(GameActiveX01Event.ShowSnackbar(R.string.something_went_wrong))
                state = state.copy(isLoading = false)
            }
            .launchIn(viewModelScope)
    }

    fun onRestartClicked() {
        vibrateUseCase.click()
        val currentState = setupGameUseCase.resetGame()

        state = state.copy(
            currentInputScore = currentState.score?.fromInputScore(),
            currentSet = currentState.set,
            currentLeg = currentState.leg,
            currentPlayer = currentState.player.fromPlayer(),
            playersScores = currentState.playerScores.map { it.fromPlayerScore() },
        )
    }

    fun vibrateClick() = vibrateUseCase.click()

    private fun validateKey(key: Int, multiplier: Int): Boolean {
        val currentScore = if (startOverAfterFurtherInput) {
            InputScore.Turn(0)
        } else {
            state.currentInputScore
        }

        val currentPlayerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        } ?: return false

        return when (getCurrentInputMode()) {
            InputMode.PerDart -> {
                validateSingleThrowUseCase(
                    score = key,
                    multiplier = multiplier,
                    scoreLeft = currentPlayerScore.scoreLeft - currentScore.score(),
                ) && currentScore.score() + key <= currentPlayerScore.scoreLeft
            }
            InputMode.PerTurn -> currentScore.score()
                .addDecimal(key) <= currentPlayerScore.scoreLeft
        }
    }

    companion object {
        val QUICK_SCORES = listOf(
            26, 41, 45, 60, 85, 100,
        )
    }
}
