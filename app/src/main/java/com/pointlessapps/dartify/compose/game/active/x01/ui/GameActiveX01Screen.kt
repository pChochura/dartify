package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.active.x01.ui.dialogs.NumberOfDoublesDialog
import com.pointlessapps.dartify.compose.game.active.x01.ui.dialogs.NumberOfThrowsAndDoublesDialog
import com.pointlessapps.dartify.compose.game.active.x01.ui.dialogs.NumberOfThrowsDialog
import com.pointlessapps.dartify.compose.game.active.x01.ui.dialogs.WinnerDialog
import com.pointlessapps.dartify.compose.game.active.x01.ui.input.score.ThreeDartsInputKeyboard
import com.pointlessapps.dartify.compose.game.active.x01.ui.input.score.ThreeDartsInputScore
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
    LaunchedEffect(gameSettings) {
        viewModel.setGameSettings(gameSettings)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                GameActiveX01Event.NavigateBack -> onNavigate(null)
                is GameActiveX01Event.Navigate -> onNavigate(it.route)
                is GameActiveX01Event.AskForNumberOfThrows ->
                    numberOfThrowsAndDoublesDialogModel = NumberOfThrowsDialogModel(
                        it.availableThrowMin,
                    )
                is GameActiveX01Event.AskForNumberOfDoubles ->
                    numberOfDoublesDialogModel = NumberOfDoublesDialogModel(it.availableMax)
                is GameActiveX01Event.AskForNumberOfThrowsAndDoubles ->
                    numberOfThrowsAndDoublesDialogModel = NumberOfThrowsDialogModel(
                        it.availableThrowMin,
                        it.availableDoubleMax,
                    )
                is GameActiveX01Event.ShowWinnerDialog ->
                    winnerDialogModel = WinnerDialogModel(it.playerScore)
            }
        }
    }

    ComposeScaffoldLayout(
        topBar = {
            Title(
                currentSet = viewModel.state.currentSet,
                currentLeg = viewModel.state.currentLeg,
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
            ThreeDartsInputScore(
                finishSuggestion = viewModel.getCurrentFinishSuggestion(),
                currentInputScore = viewModel.state.currentInputScore,
                onClearClicked = viewModel::onClearClicked,
            )
            ThreeDartsInputKeyboard(
                onPossibleCheckoutRequested = viewModel::onPossibleCheckoutRequested,
                onQuickScoreClicked = viewModel::onQuickScoreClicked,
                onKeyClicked = viewModel::onKeyClicked,
                onUndoClicked = viewModel::onUndoClicked,
                onDoneClicked = viewModel::onDoneClicked,
            )
        }
    }

    numberOfThrowsAndDoublesDialogModel?.let { model ->
        if (model.maxNumberOfDouble != null) {
            NumberOfThrowsAndDoublesDialog(
                minNumberOfThrows = model.minNumberOfThrows,
                maxNumberOfDoubles = model.maxNumberOfDouble,
                onDoneClicked = { throwsInTotal, throwsOnDouble ->
                    viewModel.onNumberOfThrowsClicked(throwsInTotal, throwsOnDouble)
                    numberOfThrowsAndDoublesDialogModel = null
                },
                onDismissRequest = { numberOfThrowsAndDoublesDialogModel = null },
            )
        } else {
            NumberOfThrowsDialog(
                minNumberOfThrows = model.minNumberOfThrows,
                onButtonClicked = { throwsInTotal ->
                    viewModel.onNumberOfThrowsClicked(throwsInTotal)
                    numberOfThrowsAndDoublesDialogModel = null
                },
                onDismissRequest = { numberOfThrowsAndDoublesDialogModel = null },
            )
        }
    }

    numberOfDoublesDialogModel?.let { model ->
        NumberOfDoublesDialog(
            maxNumberOfDoubles = model.maxNumberOfDouble,
            onButtonClicked = {
                viewModel.onNumberOfDoublesClicked(it)
                numberOfDoublesDialogModel = null
            },
            onDismissRequest = { numberOfDoublesDialogModel = null },
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
            onDismissRequest = { winnerDialogModel = null },
        )
    }
}

@Composable
private fun Title(
    currentSet: Int,
    currentLeg: Int,
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ComposeText(
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
    currentPlayer: Player,
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
                    max = score.max,
                    average = score.average,
                    numberOfDarts = score.numberOfDarts,
                    doublePercentage = score.doublePercentage.takeIf {
                        score.player.outMode == GameMode.Double
                    },
                    isActive = currentPlayer == score.player,
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
                    text = stringResource(id = R.string.sets_legs, it.wonSets, it.wonLegs),
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
    val maxNumberOfDouble: Map<Int, Int>? = null,
)

private data class NumberOfDoublesDialogModel(
    val maxNumberOfDouble: Int,
)

private data class WinnerDialogModel(
    val playerScore: PlayerScore,
)
