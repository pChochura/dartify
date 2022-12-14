@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.pointlessapps.dartify.compose.game.setup.x01.ui

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.pointlessapps.dartify.LocalSnackbarHostState
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.GameMode
import com.pointlessapps.dartify.compose.game.model.MatchResolutionStrategy
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.game.setup.ui.PlayerEntryCard
import com.pointlessapps.dartify.compose.game.setup.ui.defaultPlayerEntryCardModel
import com.pointlessapps.dartify.compose.game.setup.x01.ui.dialog.GameModeDialog
import com.pointlessapps.dartify.compose.game.setup.x01.ui.dialog.StartingScoreDialog
import com.pointlessapps.dartify.compose.ui.components.*
import com.pointlessapps.dartify.compose.ui.theme.Route
import com.pointlessapps.dartify.reorderable.list.ReorderableListState
import com.pointlessapps.dartify.reorderable.list.rememberReorderableListState
import com.pointlessapps.dartify.reorderable.list.reorderable
import com.pointlessapps.dartify.reorderable.list.reorderableItem
import org.koin.androidx.compose.getViewModel

@Composable
internal fun GameSetupX01Screen(
    viewModel: GameSetupX01ViewModel = getViewModel(),
    selectedPlayers: List<Player>,
    onNavigate: (Route?) -> Unit,
) {
    val localSnackbarHostState = LocalSnackbarHostState.current

    var showStartingScoreDialog by remember { mutableStateOf(false) }
    var gameModeDialogModel by remember { mutableStateOf<GameModeDialogModel?>(null) }

    LaunchedEffect(selectedPlayers) {
        viewModel.setSelectedPlayers(selectedPlayers)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is GameSetupX01Event.Navigate -> onNavigate(it.route)
                is GameSetupX01Event.ShowErrorSnackbar -> localSnackbarHostState.showSnackbar(it.message)
            }
        }
    }

    ComposeScaffoldLayout(
        topBar = { Title() },
        fab = { StartGameButton(onStartGameClicked = viewModel::onStartGameClicked) },
    ) { innerPadding ->
        val reorderableState = rememberReorderableListState(
            onMove = viewModel::onPlayersSwapped,
            onDragStarted = viewModel::onDragStarted,
        )

        LazyColumn(
            state = reorderableState.lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .reorderable(reorderableState)
                .padding(horizontal = dimensionResource(id = R.dimen.margin_semi_big)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item(key = "spacer_top") {
                Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            }

            item(key = "match_settings") {
                MatchSettings(
                    startingScore = viewModel.state.startingScore,
                    numberOfSets = viewModel.state.numberOfSets,
                    numberOfLegs = viewModel.state.numberOfLegs,
                    matchResolutionStrategy = viewModel.state.matchResolutionStrategy,
                    onShowStartingScoreDialog = { showStartingScoreDialog = true },
                    onMatchResolutionStrategyChanged = viewModel::onMatchResolutionStrategyChanged,
                    onNumberOfSetsChanged = viewModel::onNumberOfSetsChanged,
                    onNumberOfLegsChanged = viewModel::onNumberOfLegsChanged,
                )
            }

            item(key = "spacer_middle_1") {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_semi_big)))
            }

            item(key = "game_modes") {
                GameModes(
                    inMode = viewModel.state.inMode,
                    outMode = viewModel.state.outMode,
                    onInGameModeSelected = viewModel::onInGameModeSelected,
                    onOutGameModeSelected = viewModel::onOutGameModeSelected,
                    onShowGameModeDialog = { gameModeDialogModel = it },
                )
            }

            item(key = "spacer_middle_2") {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_semi_big)))
            }

            players(
                players = viewModel.state.players,
                reorderableState = reorderableState,
                onPlayerClicked = { player ->
                    gameModeDialogModel = GameModeDialogModel(
                        R.string.out_mode,
                        cancelable = true,
                        callback = { viewModel.onOutGameModeSelectedForPlayer(it, player) },
                    )
                },
                onPlayerRemoved = viewModel::onPlayerRemoved,
                onSelectPlayersClicked = viewModel::onSelectPlayersClicked,
            )

            item(key = "spacer_bottom") {
                Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
            }
        }
    }

    if (showStartingScoreDialog) {
        StartingScoreDialog(
            onButtonClicked = {
                viewModel.setStartingScore(it)
                showStartingScoreDialog = false
            },
            onDismissRequest = { showStartingScoreDialog = false },
        )
    }

    gameModeDialogModel?.let { model ->
        GameModeDialog(
            label = model.label,
            cancelable = model.cancelable,
            onButtonClicked = {
                model.callback(it)
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
private fun LazyItemScope.MatchSettings(
    startingScore: Int,
    numberOfSets: Int,
    numberOfLegs: Int,
    matchResolutionStrategy: MatchResolutionStrategy,
    onShowStartingScoreDialog: () -> Unit,
    onMatchResolutionStrategyChanged: (MatchResolutionStrategy?) -> Unit,
    onNumberOfSetsChanged: (Int) -> Unit,
    onNumberOfLegsChanged: (Int) -> Unit,
) {
    val switcherValueFirstTo by remember { mutableStateOf(ComposeSwitcherValue(label = R.string.first_to)) }
    val switcherValueBestOf by remember { mutableStateOf(ComposeSwitcherValue(label = R.string.best_of)) }
    var selectedSwitcherValue by remember {
        mutableStateOf(
            when (matchResolutionStrategy) {
                MatchResolutionStrategy.FirstTo -> switcherValueFirstTo
                MatchResolutionStrategy.BestOf -> switcherValueBestOf
            },
        )
    }

    ComposeSwitcher(
        modifier = Modifier.animateItemPlacement(),
        values = listOf(switcherValueFirstTo, switcherValueBestOf),
        selectedValue = selectedSwitcherValue,
        onSelect = {
            selectedSwitcherValue = it
            onMatchResolutionStrategyChanged(
                when (it) {
                    switcherValueFirstTo -> MatchResolutionStrategy.FirstTo
                    switcherValueBestOf -> MatchResolutionStrategy.BestOf
                    else -> null
                },
            )
        },
    )

    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_semi_big)))

    Row(
        modifier = Modifier.animateItemPlacement(),
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
        ),
    ) {
        ComposeCounter(
            value = numberOfSets,
            maxValue = GameSetupX01ViewModel.MAX_NUMBER_OF_SETS,
            minValue = GameSetupX01ViewModel.MIN_NUMBER_OF_SETS,
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
            value = numberOfLegs,
            maxValue = GameSetupX01ViewModel.MAX_NUMBER_OF_LEGS,
            minValue = GameSetupX01ViewModel.MIN_NUMBER_OF_LEGS,
            label = stringResource(id = R.string.legs),
            onChange = { onNumberOfLegsChanged(it) },
            counterModel = defaultComposeCounterModel().copy(
                counterColor = colorResource(id = R.color.red),
            ),
        )
    }

    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_semi_big)))

    ComposeInputButton(
        modifier = Modifier.animateItemPlacement(),
        label = stringResource(id = R.string.starting_score),
        value = "$startingScore",
        onClick = onShowStartingScoreDialog,
        inputButtonModel = defaultComposeInputButtonModel().copy(
            icon = R.drawable.ic_score,
        ),
    )
}

@Composable
private fun LazyItemScope.GameModes(
    inMode: GameMode,
    outMode: GameMode,
    onInGameModeSelected: (GameMode?) -> Unit,
    onOutGameModeSelected: (GameMode?) -> Unit,
    onShowGameModeDialog: (GameModeDialogModel) -> Unit,
) {
    Column(
        modifier = Modifier.animateItemPlacement(),
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

private fun LazyListScope.players(
    players: List<Player>,
    reorderableState: ReorderableListState,
    onPlayerClicked: (Player) -> Unit,
    onPlayerRemoved: (Player) -> Unit,
    onSelectPlayersClicked: () -> Unit,
) {
    item(key = R.string.players) {
        ComposeText(
            modifier = Modifier
                .fillMaxWidth()
                .animateItemPlacement(),
            text = stringResource(id = R.string.players),
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.h2,
                textColor = MaterialTheme.colors.onPrimary,
            ),
        )
    }

    if (players.isNotEmpty()) {
        item(key = R.string.tap_to_specify_out_mode_individually) {
            ComposeHelpText(
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.margin_small))
                    .animateItemPlacement(),
                text = R.string.tap_to_specify_out_mode_individually,
            )
        }
    }

    items(players, { it.id }) { player ->
        val dismissState = rememberDismissState(
            confirmStateChange = {
                if (it != DismissValue.Default) {
                    onPlayerRemoved(player)
                }

                true
            },
        )

        SwipeToDismiss(
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.margin_small))
                .reorderableItem(
                    key = player.id,
                    reorderableListState = reorderableState,
                    nonDraggedModifier = Modifier.animateItemPlacement(),
                ),
            state = dismissState,
            directions = setOf(DismissDirection.EndToStart),
            dismissThresholds = { FractionalThreshold(0.25f) },
            background = background@{
                if (reorderableState.isDragged(player.id)) {
                    return@background
                }

                DismissDeleteActionItem(dismissState)
            },
            dismissContent = { PlayerItem(player, onPlayerClicked) },
        )
    }

    item(key = R.drawable.ic_plus) {
        SelectPlayersItem(onSelectPlayersClicked)
    }
}

@Composable
private fun DismissDeleteActionItem(dismissState: DismissState) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialTheme.colors.onBackground
            else -> colorResource(id = R.color.red)
        },
    )
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.dialog_icon_size))
                .scale(scale),
            painter = painterResource(id = R.drawable.ic_delete),
            tint = color,
            contentDescription = null,
        )
    }
}

@Composable
private fun PlayerItem(
    player: Player,
    onPlayerClicked: (Player) -> Unit,
) {
    PlayerEntryCard(
        label = player.name,
        onClick = { onPlayerClicked(player) },
        infoCardText = player.outMode?.abbrev?.let {
            stringResource(
                id = R.string.out_mode_abbrev,
                stringResource(id = it),
            ).uppercase()
        },
        playerEntryCardModel = defaultPlayerEntryCardModel().copy(
            mainIcon = if (player.botOptions != null) {
                R.drawable.ic_robot
            } else {
                R.drawable.ic_person
            },
            additionalIcon = R.drawable.ic_move_handle,
        ),
    )
}

@Composable
private fun LazyItemScope.SelectPlayersItem(onSelectPlayersClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(top = dimensionResource(R.dimen.margin_small))
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = dimensionResource(id = R.dimen.input_button_border_width),
                color = MaterialTheme.colors.secondary,
                shape = MaterialTheme.shapes.medium,
            )
            .clickable(onClick = onSelectPlayersClicked)
            .padding(dimensionResource(id = R.dimen.margin_medium))
            .animateItemPlacement(),
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
            Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.player_entry_card_icon_size)),
            painter = painterResource(id = R.drawable.ic_players),
            tint = MaterialTheme.colors.onSecondary,
            contentDescription = null,
        )
        ComposeText(
            text = stringResource(id = R.string.select_players),
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.h2.copy(
                    fontWeight = FontWeight.Normal,
                ),
                textColor = MaterialTheme.colors.onPrimary,
            ),
        )
    }
}

private data class GameModeDialogModel(
    @StringRes val label: Int = R.string.in_mode,
    val cancelable: Boolean = false,
    val callback: (GameMode?) -> Unit,
)
