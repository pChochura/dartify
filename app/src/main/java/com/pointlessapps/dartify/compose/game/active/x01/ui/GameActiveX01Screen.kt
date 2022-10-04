package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.koin.androidx.compose.getViewModel

@Composable
internal fun GameActiveX01Screen(
    viewModel: GameActiveX01ViewModel = getViewModel(),
    gameSettings: GameSettings,
    onNavigate: (Route) -> Unit,
) {
    LaunchedEffect(gameSettings) {
        viewModel.setGameSettings(gameSettings)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is GameActiveX01Event.Navigate -> onNavigate(it.route)
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
                startingScore = viewModel.state.startingScore,
                playersScores = viewModel.state.playersScores,
                currentPlayer = viewModel.state.currentPlayer,
            )
            InputScore(
                currentInputScore = viewModel.state.currentInputScore,
                onClearClicked = viewModel::onClearClicked,
            )
            QuickScores(
                onQuickScoreClicked = viewModel::onQuickScoreClicked,
            )
            Keyboard(
                onKeyClicked = viewModel::onKeyClicked,
                onUndoClicked = viewModel::onUndoClicked,
                onDoneClicked = viewModel::onDoneClicked,
            )
        }
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
    startingScore: Int,
    playersScores: List<PlayerScore>,
    currentPlayer: Player,
) {
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
                PlayerScoreData(
                    name = it.player.name,
                    icon = when (it.player) {
                        is Bot -> R.drawable.ic_robot
                        else -> R.drawable.ic_person
                    },
                    scoreLeft = startingScore - it.score,
                    max = it.max,
                    avg = it.average,
                    darts = it.numberOfDarts,
                    doublePercentage = it.doublePercentage,
                ),
                isActive = currentPlayer == it.player,
            )
        }
    }
}

@Composable
private fun RowScope.Score(scoreData: PlayerScoreData, isActive: Boolean) {
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
        ScoreStatisticEntry(scoreData.icon, scoreData.name)
        ComposeText(
            text = "${scoreData.scoreLeft}",
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h1.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.scaledSp(),
                ),
            ),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_tiny),
            ),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_tiny),
                ),
            ) {
                ScoreStatisticEntry(
                    R.drawable.ic_score,
                    stringResource(id = R.string.statistic_max, scoreData.max),
                )
                ScoreStatisticEntry(
                    R.drawable.ic_stats,
                    stringResource(id = R.string.statistic_avg, scoreData.avg),
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_tiny),
                ),
            ) {
                ScoreStatisticEntry(
                    R.drawable.ic_dart,
                    stringResource(id = R.string.statistic_darts, scoreData.darts),
                )
                ScoreStatisticEntry(
                    R.drawable.ic_darts,
                    stringResource(
                        id = R.string.statistic_double,
                        (scoreData.doublePercentage * 100).toInt(),
                    ),
                )
            }
        }
    }
}

@Composable
private fun ScoreStatisticEntry(@DrawableRes icon: Int, value: String) {
    Row(
        modifier = Modifier
            .border(
                width = dimensionResource(id = R.dimen.player_score_border_width),
                color = MaterialTheme.colors.primary,
                shape = CircleShape,
            )
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_tiny),
                vertical = dimensionResource(id = R.dimen.margin_nano),
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_nano),
        ),
    ) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.player_score_icon_size)),
            painter = painterResource(id = icon),
            tint = MaterialTheme.colors.onSecondary,
            contentDescription = null,
        )
        ComposeText(
            text = value,
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.subtitle1.copy(
                    fontSize = 10.scaledSp(),
                    fontWeight = FontWeight.Bold,
                ),
            ),
        )
    }
}

@Composable
private fun InputScore(
    currentInputScore: Int,
    onClearClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_semi_big),
                vertical = dimensionResource(id = R.dimen.margin_small),
            ),
    ) {
        ComposeText(
            modifier = Modifier.align(Alignment.Center),
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
            modifier = Modifier.align(Alignment.CenterEnd),
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
    onQuickScoreClicked: (Int) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        userScrollEnabled = false,
    ) {
        items(GameActiveX01ViewModel.QUICK_SCORES) {
            QuickScore(
                score = it,
                onQuickScoreClicked = {
                    onQuickScoreClicked(it)
                },
            )
        }
    }
}

@Composable
private fun QuickScore(score: Int, onQuickScoreClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .rectBorder(
                top = dimensionResource(id = R.dimen.score_button_border_width),
                left = dimensionResource(id = R.dimen.score_button_border_width),
                right = dimensionResource(id = R.dimen.score_button_border_width),
                color = MaterialTheme.colors.primary,
            )
            .background(MaterialTheme.colors.secondary)
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

private data class PlayerScoreData(
    val name: String,
    @DrawableRes val icon: Int,
    val scoreLeft: Int,
    val max: Int,
    val avg: Float,
    val darts: Int,
    @FloatRange(from = 0.0, to = 1.0) val doublePercentage: Float,
)
