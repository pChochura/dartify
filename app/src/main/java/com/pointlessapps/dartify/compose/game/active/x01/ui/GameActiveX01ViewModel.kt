package com.pointlessapps.dartify.compose.game.active.x01.ui

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
import com.pointlessapps.dartify.domain.game.x01.score.usecase.IsCheckoutPossibleUseCase
import com.pointlessapps.dartify.domain.game.x01.score.usecase.ValidateScoreUseCase
import com.pointlessapps.dartify.domain.game.x01.score.usecase.ValidateSingleThrowUseCase
import com.pointlessapps.dartify.domain.game.x01.turn.model.CurrentState
import com.pointlessapps.dartify.domain.game.x01.turn.model.DoneTurnEvent
import com.pointlessapps.dartify.domain.game.x01.turn.model.WinState
import com.pointlessapps.dartify.domain.game.x01.turn.usecase.*
import com.pointlessapps.dartify.domain.vibration.usecase.VibrateUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal sealed interface GameActiveX01Event {
    @JvmInline
    value class Navigate(val route: Route) : GameActiveX01Event
    object NavigateBack : GameActiveX01Event

    data class AskForNumberOfThrowsAndDoubles(
        val minNumberOfThrows: Int,
        val maxNumberOfDoubles: Map<Int, Int>,
    ) : GameActiveX01Event

    @JvmInline
    value class AskForNumberOfThrows(val minNumberOfThrows: Int) : GameActiveX01Event

    data class AskForNumberOfDoubles(
        val minNumberOfDoubles: Int,
        val maxNumberOfDoubles: Int,
    ) : GameActiveX01Event

    data class ShowWinnerDialog(val playerScore: PlayerScore) : GameActiveX01Event

    @JvmInline
    value class ShowErrorSnackbar(@StringRes val message: Int) : GameActiveX01Event
}

@Immutable
internal data class GameActiveX01State(
    val currentSet: Int = 1,
    val currentLeg: Int = 1,
    val playersScores: List<PlayerScore> = emptyList(),
    val currentPlayer: Player? = null,
    val currentInputScore: InputScore? = null,
)

internal class GameActiveX01ViewModel(
    private val validateSingleThrowUseCase: ValidateSingleThrowUseCase,
    private val validateScoreUseCase: ValidateScoreUseCase,
    private val isCheckoutPossibleUseCase: IsCheckoutPossibleUseCase,
    private val nextTurnUseCase: NextTurnUseCase,
    private val doneTurnUseCase: DoneTurnUseCase,
    private val addInputUseCase: AddInputUseCase,
    private val undoTurnUseCase: UndoTurnUseCase,
    private val finishLegUseCase: FinishLegUseCase,
    private val setupGameUseCase: SetupGameUseCase,
    private val vibrateUseCase: VibrateUseCase,
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
        setupGame(gameSettings)
    }

    private fun setupGame(gameSettings: GameSettings) {
        val currentState = setupGameUseCase(
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
        if (
            !validateScoreUseCase(quickScore) &&
            inputModes[state.currentPlayer?.id] == InputMode.PerTurn
        ) {
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
            vibrateUseCase.tick()
            return
        }

        val currentScore = if (startOverAfterFurtherInput) {
            InputScore.Turn(0)
        } else {
            state.currentInputScore
        }
        startOverAfterFurtherInput = false

        if (
            inputModes[state.currentPlayer?.id] == InputMode.PerDart &&
            (currentScore as? InputScore.Dart)?.scores?.size == MAX_NUMBER_OF_THROWS
        ) {
            vibrateUseCase.error()
            viewModelScope.launch {
                eventChannel.send(
                    GameActiveX01Event.ShowErrorSnackbar(R.string.you_can_input_three_values),
                )
            }

            return
        }

        vibrateUseCase.tick()
        state = state.copy(
            currentInputScore = when (requireNotNull(inputModes[state.currentPlayer?.id])) {
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
        val currentState = undoTurnUseCase()
        state = state.copy(
            currentInputScore = currentState.score?.fromInputScore(),
            currentSet = currentState.set,
            currentLeg = currentState.leg,
            currentPlayer = currentState.player.fromPlayer(),
            playersScores = currentState.playerScores.map { it.fromPlayerScore() },
        )
    }

    private fun invokeSingleUndoAction(): Boolean {
        when (requireNotNull(inputModes[state.currentPlayer?.id])) {
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
        val inputScore = state.currentInputScore.toInputScore(
            requireNotNull(inputModes[state.currentPlayer?.id]),
        )

        if (!validateScoreUseCase(inputScore)) {
            vibrateUseCase.error()
            viewModelScope.launch {
                eventChannel.send(
                    GameActiveX01Event.ShowErrorSnackbar(R.string.score_inputted_is_incorrect),
                )
            }

            return
        }

        startOverAfterFurtherInput = false
        vibrateUseCase.click()
        viewModelScope.launch {
            when (val event = doneTurnUseCase(inputScore)) {
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
                    val currentState = nextTurnUseCase()
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
            state.currentInputScore.toInputScore(
                requireNotNull(inputModes[state.currentPlayer?.id]),
            ),
            numberOfThrowsOnDouble = numberOfDoubles,
        )
        val currentState = nextTurnUseCase()
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
                state.currentInputScore.toInputScore(
                    requireNotNull(inputModes[state.currentPlayer?.id]),
                ),
                numberOfThrows,
                numberOfThrowsOnDoubles,
            )
        ) {
            is WinState -> {
                this.state = this.state.copy(currentInputScore = null)
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
        startOverAfterFurtherInput = true
        inputModes[requireNotNull(state.currentPlayer?.id)] =
            when (requireNotNull(inputModes[state.currentPlayer?.id])) {
                InputMode.PerDart -> InputMode.PerTurn
                InputMode.PerTurn -> InputMode.PerDart
            }
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
        viewModelScope.launch {
            eventChannel.send(GameActiveX01Event.NavigateBack)
        }
    }

    fun onRestartClicked() {
        vibrateUseCase.click()
        setupGame(gameSettings)
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

        return when (requireNotNull(inputModes[state.currentPlayer?.id])) {
            InputMode.PerDart -> {
                validateSingleThrowUseCase(
                    key,
                    multiplier,
                    currentPlayerScore.scoreLeft - currentScore.score(),
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
