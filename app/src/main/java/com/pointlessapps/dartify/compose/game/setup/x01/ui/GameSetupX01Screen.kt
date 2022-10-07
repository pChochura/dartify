package com.pointlessapps.dartify.compose.game.setup.x01.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.game.setup.ui.PlayerEntryCard
import com.pointlessapps.dartify.compose.game.setup.ui.defaultPlayerEntryCardModel
import com.pointlessapps.dartify.compose.game.setup.x01.model.GameMode
import com.pointlessapps.dartify.compose.game.setup.x01.model.MatchResolutionStrategy
import com.pointlessapps.dartify.compose.ui.components.*
import com.pointlessapps.dartify.compose.ui.theme.Route
import org.koin.androidx.compose.getViewModel

@Composable
internal fun GameSetupX01Screen(
    viewModel: GameSetupX01ViewModel = getViewModel(),
    onNavigate: (Route?) -> Unit,
) {
    var startingScoreDialogModel by remember { mutableStateOf<StartingScoreDialogModel?>(null) }
    var gameModeDialogModel by remember { mutableStateOf<GameModeDialogModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is GameSetupX01Event.Navigate -> onNavigate(it.route)
            }
        }
    }

    ComposeScaffoldLayout(
        topBar = { Title() },
        fab = { StartGameButton(onStartGameClicked = viewModel::onStartGameClicked) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(id = R.dimen.margin_semi_big)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_semi_big),
            ),
        ) {
            MatchSettings(
                startingScore = viewModel.state.startingScore,
                matchResolutionStrategy = viewModel.state.matchResolutionStrategy,
                onShowStartingScoreDialog = { startingScoreDialogModel = it },
                onMatchResolutionStrategyChanged = viewModel::onMatchResolutionStrategyChanged,
                onNumberOfSetsChanged = viewModel::onNumberOfSetsChanged,
                onNumberOfLegsChanged = viewModel::onNumberOfLegsChanged,
            )
            GameModes(
                inMode = viewModel.state.inMode,
                outMode = viewModel.state.outMode,
                onInGameModeSelected = viewModel::onInGameModeSelected,
                onOutGameModeSelected = viewModel::onOutGameModeSelected,
                onShowGameModeDialog = { gameModeDialogModel = it },
            )
            Players(
                players = viewModel.state.players,
                onAddPlayerClicked = viewModel::onAddPlayerClicked,
            )
        }
    }

    startingScoreDialogModel?.let {
        StartingScoreDialog(
            onButtonClicked = {
                viewModel.setStartingScore(it)
                startingScoreDialogModel = null
            },
            onDismissRequest = { startingScoreDialogModel = null },
        )
    }

    gameModeDialogModel?.let { model ->
        GameModeDialog(
            label = model.label,
            onButtonClicked = {
                gameModeDialogModel?.callback?.invoke(it)
                gameModeDialogModel = null
            },
            onDismissRequest = { gameModeDialogModel = null },
        )
    }
}

@Composable
private fun Title() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background.copy(alpha = 0.8f))
            .statusBarsPadding()
            .padding(dimensionResource(id = R.dimen.margin_semi_big)),
    ) {
        ComposeText(
            text = stringResource(id = R.string.x01),
            textStyle = defaultComposeTextStyle().copy(
                textColor = colorResource(id = R.color.red),
                typography = MaterialTheme.typography.h1,
            ),
        )
    }
}

@Composable
private fun StartGameButton(
    onStartGameClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        MaterialTheme.colors.background.copy(alpha = 0.8f),
                    ),
                ),
            )
            .padding(dimensionResource(id = R.dimen.margin_huge)),
        contentAlignment = Alignment.Center,
    ) {
        ComposeButton(
            label = stringResource(id = R.string.start_game),
            buttonModel = defaultComposeButtonModel().copy(
                icon = R.drawable.ic_play,
                size = ComposeButtonSize.Medium,
                shape = ComposeButtonShape.Pill,
                backgroundColor = colorResource(id = R.color.red),
            ),
            onClick = onStartGameClicked,
        )
    }
}

@Composable
private fun MatchSettings(
    startingScore: Int,
    matchResolutionStrategy: MatchResolutionStrategy,
    onShowStartingScoreDialog: (StartingScoreDialogModel) -> Unit,
    onMatchResolutionStrategyChanged: (MatchResolutionStrategy.Type?) -> Unit,
    onNumberOfSetsChanged: (Int) -> Unit,
    onNumberOfLegsChanged: (Int) -> Unit,
) {
    val switcherValueFirstTo by remember { mutableStateOf(ComposeSwitcherValue(label = R.string.first_to)) }
    val switcherValueBestOf by remember { mutableStateOf(ComposeSwitcherValue(label = R.string.best_of)) }
    var selectedSwitcherValue by remember {
        mutableStateOf(
            when (matchResolutionStrategy) {
                is MatchResolutionStrategy.FirstTo -> switcherValueFirstTo
                is MatchResolutionStrategy.BestOf -> switcherValueBestOf
            },
        )
    }

    ComposeSwitcher(
        values = listOf(switcherValueFirstTo, switcherValueBestOf),
        selectedValue = selectedSwitcherValue,
        onSelect = {
            selectedSwitcherValue = it
            onMatchResolutionStrategyChanged(
                when (it) {
                    switcherValueFirstTo -> MatchResolutionStrategy.Type.FirstTo
                    switcherValueBestOf -> MatchResolutionStrategy.Type.BestOf
                    else -> null
                },
            )
        },
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
        ),
    ) {
        ComposeCounter(
            value = matchResolutionStrategy.numberOfSets,
            maxValue = MatchResolutionStrategy.MAX_SETS,
            minValue = MatchResolutionStrategy.MIN_SETS,
            label = stringResource(id = R.string.sets),
            onChange = { onNumberOfSetsChanged(it) },
            counterModel = defaultComposeCounterModel().copy(
                counterColor = MaterialTheme.colors.primary,
            ),
        )
        ComposeText(
            text = "/",
            textStyle = defaultComposeTextStyle().let { style ->
                style.copy(
                    textAlign = TextAlign.Center,
                    typography = style.typography.copy(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            },
        )
        ComposeCounter(
            value = matchResolutionStrategy.numberOfLegs,
            maxValue = MatchResolutionStrategy.MAX_LEGS,
            minValue = MatchResolutionStrategy.MIN_LEGS,
            label = stringResource(id = R.string.legs),
            onChange = { onNumberOfLegsChanged(it) },
            counterModel = defaultComposeCounterModel().copy(
                counterColor = colorResource(id = R.color.red),
            ),
        )
    }

    ComposeInputButton(
        label = stringResource(id = R.string.starting_score),
        value = "$startingScore",
        onClick = { onShowStartingScoreDialog(StartingScoreDialogModel(enabled = true)) },
        inputButtonModel = defaultComposeInputButtonModel().copy(
            icon = R.drawable.ic_score,
        ),
    )
}

@Composable
private fun GameModes(
    inMode: GameMode,
    outMode: GameMode,
    onInGameModeSelected: (GameMode) -> Unit,
    onOutGameModeSelected: (GameMode) -> Unit,
    onShowGameModeDialog: (GameModeDialogModel) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
        ),
    ) {
        ComposeText(
            text = stringResource(id = R.string.game_modes),
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.h2,
                textColor = MaterialTheme.colors.onPrimary,
            ),
        )

        ComposeInputButton(
            label = stringResource(id = R.string.in_mode),
            value = stringResource(id = inMode.label),
            onClick = {
                onShowGameModeDialog(
                    GameModeDialogModel(
                        enabled = true,
                        label = R.string.in_mode,
                        callback = onInGameModeSelected,
                    ),
                )
            },
            inputButtonModel = defaultComposeInputButtonModel().copy(
                icon = R.drawable.ic_darts,
            ),
        )
        ComposeInputButton(
            label = stringResource(id = R.string.out_mode),
            value = stringResource(id = outMode.label),
            onClick = {
                onShowGameModeDialog(
                    GameModeDialogModel(
                        enabled = true,
                        label = R.string.out_mode,
                        callback = onOutGameModeSelected,
                    ),
                )
            },
            inputButtonModel = defaultComposeInputButtonModel().copy(
                icon = R.drawable.ic_darts,
            ),
        )
    }
}

@Composable
private fun Players(
    players: List<Player>,
    onAddPlayerClicked: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
        ),
    ) {
        ComposeText(
            text = stringResource(id = R.string.players),
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.h2,
                textColor = MaterialTheme.colors.onPrimary,
            ),
        )

        players.forEach { player ->
            PlayerEntryCard(
                label = player.name,
                onClick = { /*TODO*/ },
                infoCardText = player.outMode?.label?.let { stringResource(id = it) },
                playerEntryCardModel = defaultPlayerEntryCardModel().copy(
                    mainIcon = R.drawable.ic_person,
                    additionalIcon = R.drawable.ic_move_handle,
                ),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .border(
                    width = dimensionResource(id = R.dimen.input_button_border_width),
                    color = MaterialTheme.colors.secondary,
                    shape = MaterialTheme.shapes.medium,
                )
                .clickable(onClick = onAddPlayerClicked)
                .padding(dimensionResource(id = R.dimen.margin_medium)),
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
                Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.player_entry_card_icon_size)),
                painter = painterResource(id = R.drawable.ic_plus),
                tint = MaterialTheme.colors.onSecondary,
                contentDescription = null,
            )
            ComposeText(
                text = stringResource(id = R.string.add_a_player),
                textStyle = defaultComposeTextStyle().copy(
                    typography = MaterialTheme.typography.h2.copy(
                        fontWeight = FontWeight.Normal,
                    ),
                    textColor = MaterialTheme.colors.onPrimary,
                ),
            )
        }
    }
}

private data class GameModeDialogModel(
    val enabled: Boolean = false,
    @StringRes val label: Int = R.string.in_mode,
    val callback: (GameMode) -> Unit,
)

private data class StartingScoreDialogModel(
    val enabled: Boolean = false,
)
