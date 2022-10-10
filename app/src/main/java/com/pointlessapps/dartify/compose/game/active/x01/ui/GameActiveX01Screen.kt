package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.model.Bot
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.components.ComposeExactGrid
import com.pointlessapps.dartify.compose.ui.components.ComposeScaffoldLayout
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.ui.modifiers.rectBorder
import com.pointlessapps.dartify.compose.ui.theme.Route
import com.pointlessapps.dartify.compose.utils.conditional
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
            InputScore(
                finishSuggestion = viewModel.getCurrentFinishSuggestion(),
                currentInputScore = viewModel.state.currentInputScore,
                onClearClicked = viewModel::onClearClicked,
            )
            QuickScores(
                onPossibleCheckoutRequested = viewModel::onPossibleCheckoutRequested,
                onQuickScoreClicked = viewModel::onQuickScoreClicked,
            )
            Keyboard(
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
            playersScores.forEach {
                Score(
                    name = it.player.name,
                    icon = when (it.player) {
                        is Bot -> R.drawable.ic_robot
                        else -> R.drawable.ic_person
                    },
                    scoreLeft = onScoreLeftRequested(it.player),
                    lastScore = it.lastScore,
                    max = it.max,
                    average = it.average,
                    numberOfDarts = it.numberOfDarts,
                    doublePercentage = it.doublePercentage,
                    isActive = currentPlayer == it.player,
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
    doublePercentage: Float,
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
                ScoreStatisticEntry(
                    R.drawable.ic_darts,
                    stringResource(
                        id = R.string.statistic_double,
                        doublePercentage.toPercentage(),
                    ),
                )
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

@Composable
private fun InputScore(
    finishSuggestion: String?,
    currentInputScore: Int,
    onClearClicked: () -> Unit,
) {
    val suggestionTextFontSize by animateFloatAsState(if (currentInputScore == 0) 40f else 16f)
    val suggestionTextAlignment by animateFloatAsState(if (currentInputScore == 0) 0f else -1f)
    val currentInputScoreOpacity by animateFloatAsState(
        if (finishSuggestion == null || currentInputScore != 0) {
            1f
        } else {
            0f
        },
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_semi_big),
                vertical = dimensionResource(id = R.dimen.margin_small),
            ),
    ) {
        AnimatedVisibility(
            modifier = Modifier.align(BiasAlignment(suggestionTextAlignment, 0f)),
            visible = finishSuggestion != null,
        ) {
            ComposeText(
                text = finishSuggestion.toString(),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onSecondary,
                    typography = MaterialTheme.typography.h1.copy(
                        fontSize = suggestionTextFontSize.scaledSp(),
                        fontWeight = FontWeight.Bold,
                    ),
                ),
            )
        }
        ComposeText(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(currentInputScoreOpacity),
            text = currentInputScore.toString(),
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h1.copy(
                    fontSize = 40.scaledSp(),
                    fontWeight = FontWeight.Bold,
                ),
            ),
        )
        Button(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .alpha(currentInputScoreOpacity),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(id = R.dimen.margin_tiny),
                vertical = dimensionResource(id = R.dimen.margin_nano),
            ),
            elevation = null,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.red)),
            onClick = onClearClicked,
            enabled = currentInputScore != 0,
        ) {
            ComposeText(
                text = stringResource(id = R.string.clear),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onSecondary,
                    typography = MaterialTheme.typography.subtitle1.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun QuickScores(
    onPossibleCheckoutRequested: () -> Int? = { null },
    onQuickScoreClicked: (Int) -> Unit,
) {
    val possibleCheckout = onPossibleCheckoutRequested()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        userScrollEnabled = false,
    ) {
        itemsIndexed(GameActiveX01ViewModel.QUICK_SCORES) { index, item ->
            if (
                possibleCheckout != null &&
                index == GameActiveX01ViewModel.QUICK_SCORES.lastIndex
            ) {
                QuickScore(
                    score = possibleCheckout,
                    onQuickScoreClicked = {
                        onQuickScoreClicked(possibleCheckout)
                    },
                    hasAccent = true,
                )
            } else {
                QuickScore(
                    score = item,
                    onQuickScoreClicked = {
                        onQuickScoreClicked(item)
                    },
                )
            }
        }
    }
}

@Composable
private fun QuickScore(score: Int, onQuickScoreClicked: () -> Unit, hasAccent: Boolean = false) {
    Box(
        modifier = Modifier
            .rectBorder(
                top = dimensionResource(id = R.dimen.score_button_border_width),
                left = dimensionResource(id = R.dimen.score_button_border_width),
                right = dimensionResource(id = R.dimen.score_button_border_width),
                color = MaterialTheme.colors.primary,
            )
            .background(
                if (hasAccent) {
                    colorResource(id = R.color.red)
                } else {
                    MaterialTheme.colors.secondary
                },
            )
            .clickable(
                role = Role.Button,
                onClickLabel = score.toString(),
                onClick = onQuickScoreClicked,
            )
            .padding(vertical = dimensionResource(id = R.dimen.margin_small)),
        contentAlignment = Alignment.Center,
    ) {
        ComposeText(
            text = score.toString(),
            textStyle = defaultComposeTextStyle().copy(
                textAlign = TextAlign.Center,
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h1.let {
                    it.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = it.fontSize.value.scaledSp(),
                    )
                },
            ),
        )
    }
}

@Composable
private fun Keyboard(
    onKeyClicked: (Int) -> Unit,
    onUndoClicked: () -> Unit,
    onDoneClicked: () -> Unit,
) {
    val rows = 4
    val columns = 3
    ComposeExactGrid(
        rows = rows,
        columns = columns,
        modifier = Modifier.fillMaxSize(),
    ) { x, y ->
        when {
            y < rows - 1 -> Key(
                label = "${y * columns + x + 1}",
                onKeyClicked = { onKeyClicked(y * columns + x + 1) },
            )
            x == 0 -> IconKey(
                icon = R.drawable.ic_undo,
                label = stringResource(id = R.string.undo),
                onKeyClicked = onUndoClicked,
            )
            x == columns / 2 -> Key(
                label = "0",
                onKeyClicked = { onKeyClicked(0) },
            )
            x == columns - 1 -> IconKey(
                icon = R.drawable.ic_done,
                label = stringResource(id = R.string.done),
                onKeyClicked = onDoneClicked,
                hasAccent = true,
            )
        }
    }
}

@Composable
private fun Key(label: String, onKeyClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .rectBorder(
                top = dimensionResource(id = R.dimen.score_button_border_width),
                left = dimensionResource(id = R.dimen.score_button_border_width),
                right = dimensionResource(id = R.dimen.score_button_border_width),
                color = MaterialTheme.colors.primary,
            )
            .clickable(
                role = Role.Button,
                onClickLabel = label,
                onClick = onKeyClicked,
            )
            .padding(vertical = dimensionResource(id = R.dimen.margin_small)),
        contentAlignment = Alignment.Center,
    ) {
        ComposeText(
            text = label,
            textStyle = defaultComposeTextStyle().copy(
                textAlign = TextAlign.Center,
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h1.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.scaledSp(),
                ),
            ),
        )
    }
}

@Composable
private fun IconKey(
    @DrawableRes icon: Int,
    label: String,
    onKeyClicked: () -> Unit,
    hasAccent: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .rectBorder(
                top = dimensionResource(id = R.dimen.score_button_border_width),
                left = dimensionResource(id = R.dimen.score_button_border_width),
                right = dimensionResource(id = R.dimen.score_button_border_width),
                color = MaterialTheme.colors.primary,
            )
            .conditional(hasAccent) {
                background(colorResource(id = R.color.red))
            }
            .clickable(
                role = Role.Button,
                onClickLabel = label,
                onClick = onKeyClicked,
            )
            .padding(vertical = dimensionResource(id = R.dimen.margin_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_nano),
            Alignment.CenterVertically,
        ),
    ) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.key_button_icon_size)),
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = MaterialTheme.colors.onSecondary,
        )
        ComposeText(
            text = label,
            textStyle = defaultComposeTextStyle().copy(
                textAlign = TextAlign.Center,
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h2.let {
                    it.copy(
                        fontSize = it.fontSize.value.scaledSp(),
                    )
                },
            ),
        )
    }
}

private data class NumberOfThrowsDialogModel(
    val minNumberOfThrows: Int,
    val maxNumberOfDouble: Int? = null,
)

private data class NumberOfDoublesDialogModel(
    val maxNumberOfDouble: Int,
)

private data class WinnerDialogModel(
    val playerScore: PlayerScore,
)
