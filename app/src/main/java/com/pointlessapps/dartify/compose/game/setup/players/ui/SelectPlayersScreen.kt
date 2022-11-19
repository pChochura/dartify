package com.pointlessapps.dartify.compose.game.setup.players.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import com.pointlessapps.dartify.LocalSnackbarHostState
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.Bot
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.game.setup.players.ui.dialog.SelectCpuAverageDialog
import com.pointlessapps.dartify.compose.game.setup.players.ui.dialog.SelectPlayerNameDialog
import com.pointlessapps.dartify.compose.game.setup.ui.PlayerEntryCard
import com.pointlessapps.dartify.compose.game.setup.ui.defaultPlayerEntryCardModel
import com.pointlessapps.dartify.compose.ui.components.*
import com.pointlessapps.dartify.reorderable.list.rememberReorderableListState
import com.pointlessapps.dartify.reorderable.list.reorderable
import com.pointlessapps.dartify.reorderable.list.reorderableAnchorItem
import com.pointlessapps.dartify.reorderable.list.reorderableItem
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SelectPlayersScreen(
    viewModel: SelectPlayersViewModel = getViewModel(),
    selectedPlayers: ImmutableList<Player>,
    onPlayersSelected: (ImmutableList<Player>) -> Unit,
) {
    val localSnackbarHostState = LocalSnackbarHostState.current

    var cpuAverageDialogModel by remember { mutableStateOf<CpuAverageDialogModel?>(null) }
    var playerNameDialogModel by remember { mutableStateOf<PlayerNameDialogModel?>(null) }

    LaunchedEffect(selectedPlayers) {
        viewModel.setSelectedPlayers(selectedPlayers)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is SelectPlayersEvent.OnPlayersSelected -> onPlayersSelected(it.players)
                is SelectPlayersEvent.AskForCpuAverage ->
                    cpuAverageDialogModel = CpuAverageDialogModel(it.bot)
                is SelectPlayersEvent.AskForPlayerName ->
                    playerNameDialogModel = PlayerNameDialogModel(it.player)
                is SelectPlayersEvent.ShowActionSnackbar -> localSnackbarHostState.showSnackbar(
                    it.message,
                    it.actionLabel,
                    it.actionCallback,
                )
            }
        }
    }

    ComposeScaffoldLayout(
        topBar = { Title() },
        fab = { SaveButton(onSaveClicked = viewModel::onSaveClicked) },
    ) { innerPadding ->
        val reorderableState = rememberReorderableListState(
            onMove = viewModel::onPlayersSwapped,
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
                items = viewModel.state.players.subList(0, viewModel.state.selectedPlayersIndex),
                key = { it.id },
            ) { player ->
                PlayerEntryCard(
                    modifier = Modifier.reorderableItem(player.id, reorderableState),
                    label = player.name,
                    onClick = { viewModel.onPlayerClicked(player) },
                    playerEntryCardModel = defaultPlayerEntryCardModel().copy(
                        mainIcon = if (player is Bot) R.drawable.ic_robot else R.drawable.ic_person,
                        additionalIcon = R.drawable.ic_filter,
                    ),
                )
            }

            item(key = R.string.label_all) {
                ComposeText(
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.margin_small))
                        .animateItemPlacement()
                        .reorderableAnchorItem(R.string.label_all, reorderableState),
                    text = stringResource(id = R.string.label_all),
                    textStyle = defaultComposeTextStyle().copy(
                        typography = MaterialTheme.typography.h2,
                        textColor = MaterialTheme.colors.onPrimary,
                    ),
                )
            }

            items(
                items = viewModel.state.players.subList(
                    viewModel.state.selectedPlayersIndex,
                    viewModel.state.players.size,
                ),
                key = { it.id },
            ) { player ->
                PlayerEntryCard(
                    modifier = Modifier.reorderableItem(player.id, reorderableState),
                    label = player.name,
                    onClick = { viewModel.onPlayerClicked(player) },
                    playerEntryCardModel = defaultPlayerEntryCardModel().copy(
                        mainIcon = if (player is Bot) R.drawable.ic_robot else R.drawable.ic_person,
                        additionalIcon = R.drawable.ic_filter,
                    ),
                )
            }

            item(key = R.string.add_a_cpu) {
                PlayerEntryCard(
                    modifier = Modifier.animateItemPlacement(),
                    label = stringResource(id = R.string.cpu),
                    onClick = viewModel::onAddCpuClicked,
                    playerEntryCardModel = defaultPlayerEntryCardModel().copy(
                        mainIcon = R.drawable.ic_person,
                        additionalIcon = R.drawable.ic_plus,
                    ),
                )
            }

            item(key = R.string.add_a_player) {
                AddPlayerItem(onAddPlayerClicked = viewModel::onAddPlayerClicked)
            }

            item(key = R.string.long_press_to_rearrange_desc) {
                Row(
                    modifier = Modifier.padding(
                        top = dimensionResource(id = R.dimen.margin_small),
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.margin_tiny),
                    ),
                ) {
                    Icon(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.caption_icon_size)),
                        painter = painterResource(id = R.drawable.ic_help),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = null,
                    )
                    ComposeText(
                        text = stringResource(id = R.string.long_press_to_rearrange_desc),
                        textStyle = defaultComposeTextStyle().copy(
                            textColor = MaterialTheme.colors.onBackground,
                            typography = MaterialTheme.typography.subtitle1.copy(
                                fontSize = 10.sp,
                            ),
                        ),
                    )
                }
            }

            item(key = "spacer_bottom") {
                Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
            }
        }
    }

    cpuAverageDialogModel?.let { model ->
        SelectCpuAverageDialog(
            bot = model.bot,
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

private data class PlayerNameDialogModel(val player: Player?)
private data class CpuAverageDialogModel(val bot: Bot?)
