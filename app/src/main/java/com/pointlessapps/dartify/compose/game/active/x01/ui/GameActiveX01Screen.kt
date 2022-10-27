package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.active.x01.model.InputMode
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.active.x01.ui.dialogs.*
import com.pointlessapps.dartify.compose.game.active.x01.ui.input.dart.DartInputKeyboard
import com.pointlessapps.dartify.compose.game.active.x01.ui.input.dart.DartInputScore
import com.pointlessapps.dartify.compose.game.active.x01.ui.input.turn.TurnInputKeyboard
import com.pointlessapps.dartify.compose.game.active.x01.ui.input.turn.TurnInputScore
import com.pointlessapps.dartify.compose.game.model.Bot
import com.pointlessapps.dartify.compose.game.model.GameMode
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.components.ComposeScaffoldLayout
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.ui.modifiers.rectBorder
import com.pointlessapps.dartify.compose.ui.theme.Route
import com.pointlessapps.dartify.compose.utils.scaledSp
import com.pointlessapps.dartify.compose.utils.toPercentage
import org.koin.androidx.compose.getViewModel

@Composable
internal fun GameActiveX01Screen(
    viewModel: GameActiveX01ViewModel = getViewModel(),
    gameSettings: GameSettings,
    onNavigate: (Route?) -> Unit,
) {
    var numberOfThrowsAndDoublesDialogModel by remember {
        mutableStateOf<NumberOfThrowsDialogModel?>(null)
    }
    var numberOfDoublesDialogModel by remember { mutableStateOf<NumberOfDoublesDialogModel?>(null) }
    var winnerDialogModel by remember { mutableStateOf<WinnerDialogModel?>(null) }
    var showWarningDialog by remember { mutableStateOf(false) }

    LaunchedEffect(gameSettings) {
        viewModel.setGameSettings(gameSettings)
    }

    BackHandler { showWarningDialog = true }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                GameActiveX01Event.NavigateBack -> onNavigate(null)
                is GameActiveX01Event.Navigate -> onNavigate(it.route)
                is GameActiveX01Event.AskForNumberOfThrows ->
                    numberOfThrowsAndDoublesDialogModel = NumberOfThrowsDialogModel(
                        minNumberOfThrows = it.minNumberOfThrows,
                    )
                is GameActiveX01Event.AskForNumberOfDoubles ->
                    numberOfDoublesDialogModel = NumberOfDoublesDialogModel(
                        minNumberOfDoubles = it.minNumberOfDoubles,
                        maxNumberOfDoubles = it.maxNumberOfDoubles,
                    )
                is GameActiveX01Event.AskForNumberOfThrowsAndDoubles ->
                    numberOfThrowsAndDoublesDialogModel = NumberOfThrowsDialogModel(
                        minNumberOfThrows = it.minNumberOfThrows,
                        maxNumberOfDoubles = it.maxNumberOfDoubles,
                    )
                is GameActiveX01Event.ShowWinnerDialog ->
                    winnerDialogModel = WinnerDialogModel(it.playerScore)
            }
        }
    }

    ComposeScaffoldLayout(
        topBar = {
            Title(
                inputMode = viewModel.getCurrentInputMode(),
                currentSet = viewModel.state.currentSet,
                currentLeg = viewModel.state.currentLeg,
                onChangeInputModeClicked = viewModel::onChangeInputModeClicked,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Scores(
                playersScores = viewModel.state.playersScores,
                currentPlayer = viewModel.state.currentPlayer,
                onScoreLeftRequested = viewModel::onScoreLeftRequested,
            )
            if (viewModel.getCurrentInputMode() == InputMode.PerDart) {
                DartInputScore(
                    finishSuggestion = viewModel.getCurrentFinishSuggestion(),
                    currentInputScore = viewModel.state.currentInputScore,
                )
                DartInputKeyboard(
                    onKeyClicked = viewModel::onKeyClicked,
                    onUndoClicked = viewModel::onUndoClicked,
                    onDoneClicked = viewModel::onDoneClicked,
                )
            } else {
                TurnInputScore(
                    finishSuggestion = viewModel.getCurrentFinishSuggestion(),
                    currentInputScore = viewModel.state.currentInputScore,
                    onClearClicked = viewModel::onClearClicked,
                )
                TurnInputKeyboard(
                    onPossibleCheckoutRequested = viewModel::onPossibleCheckoutRequested,
                    onQuickScoreClicked = viewModel::onQuickScoreClicked,
                    onKeyClicked = { viewModel.onKeyClicked(it) },
                    onUndoClicked = viewModel::onUndoClicked,
                    onDoneClicked = viewModel::onDoneClicked,
                )
            }
        }
    }

    numberOfThrowsAndDoublesDialogModel?.let { model ->
        if (model.maxNumberOfDoubles != null) {
            NumberOfThrowsAndDoublesDialog(
                minNumberOfThrows = model.minNumberOfThrows,
                maxNumberOfDoubles = model.maxNumberOfDoubles,
                onUndoLastMoveClicked = { numberOfThrowsAndDoublesDialogModel = null },
                onDoneClicked = { throwsInTotal, throwsOnDouble ->
                    viewModel.onNumberOfThrowsClicked(throwsInTotal, throwsOnDouble)
                    numberOfThrowsAndDoublesDialogModel = null
                },
            )
        } else {
            NumberOfThrowsDialog(
                minNumberOfThrows = model.minNumberOfThrows,
                onUndoLastMoveClicked = { numberOfThrowsAndDoublesDialogModel = null },
                onButtonClicked = { throwsInTotal ->
                    viewModel.onNumberOfThrowsClicked(throwsInTotal)
                    numberOfThrowsAndDoublesDialogModel = null
                },
            )
        }
    }

    numberOfDoublesDialogModel?.let { model ->
        NumberOfDoublesDialog(
            minNumberOfDoubles = model.minNumberOfDoubles,
            maxNumberOfDoubles = model.maxNumberOfDoubles,
            onUndoLastMoveClicked = { numberOfDoublesDialogModel = null },
            onButtonClicked = {
                viewModel.onNumberOfDoublesClicked(it)
                numberOfDoublesDialogModel = null
            },
        )
    }

    winnerDialogModel?.let { model ->
        WinnerDialog(
            playerScore = model.playerScore,
            onUndoLastMoveClicked = {
                winnerDialogModel = null
                viewModel.onUndoClicked()
            },
            onShowGameStatsClicked = {
                winnerDialogModel = null
                viewModel.onShowGameStatsClicked()
            },
            onSaveAndCloseClicked = {
                winnerDialogModel = null
                viewModel.onSaveAndCloseClicked()
            },
        )
    }

    if (showWarningDialog) {
        WarningDialog(
            onDiscardAndCloseClicked = { showWarningDialog = false },
            onRestartClicked = {
                showWarningDialog = false
                viewModel.onRestartClicked()
            },
            onSaveAndCloseClicked = {
                showWarningDialog = false
                viewModel.onSaveAndCloseClicked()
            },
            onDismissRequest = { showWarningDialog = false },
        )
    }
}

@Composable
private fun Title(
    inputMode: InputMode,
    currentSet: Int,
    currentLeg: Int,
    onChangeInputModeClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background.copy(alpha = 0.8f))
            .statusBarsPadding()
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_medium),
                vertical = dimensionResource(id = R.dimen.margin_tiny),
            ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.margin_small)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ComposeText(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.set_leg, currentSet, currentLeg),
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colors.onBackground,
                typography = MaterialTheme.typography.h3.let {
                    it.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = it.fontSize.value.scaledSp(),
                    )
                },
            ),
        )
        IconButton(onClick = onChangeInputModeClicked) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.button_icon_size)),
                painter = painterResource(
                    id = when (inputMode) {
                        InputMode.PerDart -> R.drawable.ic_per_turn_score
                        InputMode.PerTurn -> R.drawable.ic_per_dart_score
                    },
                ),
                tint = MaterialTheme.colors.onSecondary,
                contentDescription = null,
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.button_icon_size)),
                painter = painterResource(id = R.drawable.ic_settings),
                tint = MaterialTheme.colors.onSecondary,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun Scores(
    playersScores: List<PlayerScore>,
    currentPlayer: Player?,
    onScoreLeftRequested: (Player) -> Int,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = dimensionResource(id = R.dimen.input_button_border_width),
                    color = MaterialTheme.colors.secondary,
                ),
        ) {
            playersScores.forEach { score ->
                Score(
                    name = score.player.name,
                    icon = when (score.player) {
                        is Bot -> R.drawable.ic_robot
                        else -> R.drawable.ic_person
                    },
                    scoreLeft = onScoreLeftRequested(score.player),
                    lastScore = score.lastScore,
                    max = score.maxScore,
                    average = score.averageScore,
                    numberOfDarts = score.numberOfDarts,
                    doublePercentage = score.doublePercentage.takeIf {
                        score.player.outMode == GameMode.Double
                    },
                    isActive = currentPlayer?.id == score.player.id,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .rectBorder(
                    bottom = dimensionResource(id = R.dimen.score_button_border_width),
                    color = MaterialTheme.colors.secondary,
                )
                .padding(dimensionResource(id = R.dimen.margin_tiny)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            playersScores.forEach {
                ComposeText(
                    modifier = Modifier.weight(1f),
                    text = stringResource(
                        id = R.string.sets_legs,
                        it.numberOfWonSets,
                        it.numberOfWonLegs,
                    ),
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colors.onPrimary,
                        typography = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.scaledSp(),
                        ),
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}

@Composable
private fun RowScope.Score(
    name: String,
    @DrawableRes icon: Int,
    scoreLeft: Int,
    lastScore: Int?,
    max: Int,
    average: Float,
    numberOfDarts: Int,
    doublePercentage: Float?,
    isActive: Boolean,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .background(
                if (isActive) {
                    MaterialTheme.colors.secondary
                } else {
                    MaterialTheme.colors.background
                },
            )
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_tiny),
                vertical = dimensionResource(id = R.dimen.margin_medium),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        ScoreStatisticEntry(icon, name)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ComposeText(
                text = "$scoreLeft",
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onSecondary,
                    typography = MaterialTheme.typography.h1.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.scaledSp(),
                    ),
                ),
            )
            AnimatedVisibility(visible = lastScore != null) {
                ComposeText(
                    text = stringResource(id = R.string.last_score, lastScore ?: 0),
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colors.onSecondary,
                        typography = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    ),
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_nano),
            ),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_nano),
                ),
            ) {
                ScoreStatisticEntry(
                    R.drawable.ic_dart,
                    stringResource(id = R.string.statistic_darts, numberOfDarts),
                    backgroundColor = MaterialTheme.colors.primary,
                )
                if (doublePercentage != null) {
                    ScoreStatisticEntry(
                        R.drawable.ic_darts,
                        stringResource(
                            id = R.string.statistic_double,
                            doublePercentage.toPercentage(),
                        ),
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_nano),
                ),
            ) {
                ScoreStatisticEntry(
                    R.drawable.ic_stats,
                    stringResource(id = R.string.statistic_avg, average),
                )
                ScoreStatisticEntry(
                    R.drawable.ic_score,
                    stringResource(id = R.string.statistic_max, max),
                )
            }
        }
    }
}

private data class NumberOfThrowsDialogModel(
    val minNumberOfThrows: Int,
    val maxNumberOfDoubles: Map<Int, Int>? = null,
)

private data class NumberOfDoublesDialogModel(
    val minNumberOfDoubles: Int,
    val maxNumberOfDoubles: Int,
)

private data class WinnerDialogModel(
    val playerScore: PlayerScore,
)
