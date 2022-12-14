package com.pointlessapps.dartify.compose.game.setup.players.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.pointlessapps.dartify.LocalSnackbarHostState
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.game.setup.players.ui.dialog.RemovePlayerDialog
import com.pointlessapps.dartify.compose.game.setup.players.ui.dialog.SelectCpuAverageDialog
import com.pointlessapps.dartify.compose.game.setup.players.ui.dialog.SelectPlayerNameDialog
import com.pointlessapps.dartify.compose.game.setup.ui.PlayerEntryCard
import com.pointlessapps.dartify.compose.game.setup.ui.defaultPlayerEntryCardModel
import com.pointlessapps.dartify.compose.ui.components.*
import com.pointlessapps.dartify.reorderable.list.*
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SelectPlayersScreen(
    viewModel: SelectPlayersViewModel = getViewModel(),
    selectedPlayers: List<Player>,
    onPlayersSelected: (List<Player>) -> Unit,
    onDismissed: (List<Player>) -> Unit,
) {
    val localSnackbarHostState = LocalSnackbarHostState.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cpuAverageDialogModel by remember { mutableStateOf<OptionalPlayerDialogModel?>(null) }
    var playerNameDialogModel by remember { mutableStateOf<OptionalPlayerDialogModel?>(null) }
    var removePlayerDialogModel by remember { mutableStateOf<RequiredPlayerDialogModel?>(null) }

    BackHandler { onDismissed(selectedPlayers) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPlayers()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(selectedPlayers) {
        viewModel.setSelectedPlayers(selectedPlayers)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is SelectPlayersEvent.OnPlayersSelected -> onPlayersSelected(it.players)
                is SelectPlayersEvent.AskForCpuAverage ->
                    cpuAverageDialogModel = OptionalPlayerDialogModel(it.bot)
                is SelectPlayersEvent.AskForPlayerName ->
                    playerNameDialogModel = OptionalPlayerDialogModel(it.player)
                is SelectPlayersEvent.ShowRemovePlayerWarning ->
                    removePlayerDialogModel = RequiredPlayerDialogModel(it.player)
                is SelectPlayersEvent.ShowSnackbar -> localSnackbarHostState.showSnackbar(
                    it.message,
                    it.actionLabel,
                    it.actionCallback,
                    it.dismissCallback,
                )
            }
        }
    }

    ComposeLoader(enabled = viewModel.state.isLoading)

    ComposeScaffoldLayout(
        topBar = { Title() },
        fab = { SaveButton(onSaveClicked = viewModel::onSaveClicked) },
    ) { innerPadding ->
        val reorderableState = rememberReorderableListState(
            onMove = viewModel::onPlayersSwapped,
            onDragStarted = viewModel::onDragStarted,
        )

        LazyColumn(
            state = reorderableState.lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(id = R.dimen.margin_semi_big))
                .reorderable(reorderableState),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
            ),
        ) {
            item(key = "spacer_top") {
                Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            }

            item(key = R.string.label_selected) {
                ComposeText(
                    text = stringResource(id = R.string.label_selected),
                    textStyle = defaultComposeTextStyle().copy(
                        typography = MaterialTheme.typography.h2,
                        textColor = MaterialTheme.colors.onPrimary,
                    ),
                )
            }

            items(
                items = viewModel.state.players
                    .take(viewModel.state.selectedPlayersIndex)
                    .filterNot(Player::deleted),
                key = { it.id },
            ) { player ->
                PlayerItem(
                    reorderableState = reorderableState,
                    player = player,
                    onPlayerClicked = viewModel::onPlayerClicked,
                    onPlayerRemoved = viewModel::onPlayerRemoved,
                )
            }

            item(key = R.string.label_all) {
                ComposeText(
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.margin_small))
                        .reorderableAnchorItem(R.string.label_all, reorderableState),
                    text = stringResource(id = R.string.label_all),
                    textStyle = defaultComposeTextStyle().copy(
                        typography = MaterialTheme.typography.h2,
                        textColor = MaterialTheme.colors.onPrimary,
                    ),
                )
            }

            items(
                items = viewModel.state.players
                    .drop(viewModel.state.selectedPlayersIndex)
                    .filterNot(Player::deleted),
                key = { it.id },
            ) { player ->
                PlayerItem(
                    reorderableState = reorderableState,
                    player = player,
                    onPlayerClicked = viewModel::onPlayerClicked,
                    onPlayerRemoved = viewModel::onPlayerRemoved,
                )
            }

            item(key = R.string.add_a_cpu) {
                PlayerEntryCard(
                    modifier = Modifier.animateItemPlacement(),
                    label = stringResource(id = R.string.cpu),
                    onClick = viewModel::onAddCpuClicked,
                    playerEntryCardModel = defaultPlayerEntryCardModel().copy(
                        mainIcon = R.drawable.ic_robot,
                        additionalIcon = R.drawable.ic_plus,
                    ),
                )
            }

            item(key = R.string.add_a_player) {
                AddPlayerItem(onAddPlayerClicked = viewModel::onAddPlayerClicked)
            }

            item(key = R.string.long_press_to_rearrange_desc) {
                ComposeHelpText(
                    text = R.string.long_press_to_rearrange_desc,
                    modifier = Modifier.padding(
                        top = dimensionResource(id = R.dimen.margin_small),
                    ),
                )
            }

            item(key = "spacer_bottom") {
                Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
            }
        }
    }

    cpuAverageDialogModel?.let { model ->
        SelectCpuAverageDialog(
            bot = model.player,
            onRemoveClicked = {
                viewModel.onPlayerRemoved(it)
                cpuAverageDialogModel = null
            },
            onSaveClicked = {
                viewModel.onPlayerAdded(it)
                cpuAverageDialogModel = null
            },
            onDismissRequest = { cpuAverageDialogModel = null },
        )
    }

    playerNameDialogModel?.let { model ->
        SelectPlayerNameDialog(
            player = model.player,
            onRemoveClicked = {
                viewModel.onPlayerRemoved(it)
                playerNameDialogModel = null
            },
            onSaveClicked = {
                viewModel.onPlayerAdded(it)
                playerNameDialogModel = null
            },
            onDismissRequest = { playerNameDialogModel = null },
        )
    }

    removePlayerDialogModel?.let { model ->
        RemovePlayerDialog(
            onRemoveClicked = {
                viewModel.onPlayerRemovedConfirmed(model.player)
                removePlayerDialogModel = null
            },
            onDismissRequest = { removePlayerDialogModel = null },
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
            text = stringResource(id = R.string.players),
            textStyle = defaultComposeTextStyle().copy(
                textColor = colorResource(id = R.color.red),
                typography = MaterialTheme.typography.h1,
            ),
        )
    }
}

@Composable
private fun SaveButton(onSaveClicked: () -> Unit) {
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
            label = stringResource(id = R.string.save_and_close),
            buttonModel = defaultComposeButtonModel().copy(
                icon = R.drawable.ic_done,
                size = ComposeButtonSize.Medium,
                shape = ComposeButtonShape.Pill,
                backgroundColor = colorResource(id = R.color.red),
            ),
            onClick = onSaveClicked,
        )
    }
}

@Composable
private fun AddPlayerItem(onAddPlayerClicked: () -> Unit) {
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.PlayerItem(
    reorderableState: ReorderableListState,
    player: Player,
    onPlayerClicked: (Player) -> Unit,
    onPlayerRemoved: (Player) -> Unit,
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it != DismissValue.Default) {
                onPlayerRemoved(player)
            }

            true
        },
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != DismissValue.Default) {
            dismissState.reset()
        }
    }

    SwipeToDismiss(
        modifier = Modifier.reorderableItem(
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
        dismissContent = {
            PlayerEntryCard(
                label = player.name,
                onClick = { onPlayerClicked(player) },
                playerEntryCardModel = defaultPlayerEntryCardModel().copy(
                    mainIcon = if (player.botOptions != null) {
                        R.drawable.ic_robot
                    } else {
                        R.drawable.ic_person
                    },
                    additionalIcon = R.drawable.ic_filter,
                ),
            )
        },
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
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

private data class OptionalPlayerDialogModel(val player: Player?)
private data class RequiredPlayerDialogModel(val player: Player)
