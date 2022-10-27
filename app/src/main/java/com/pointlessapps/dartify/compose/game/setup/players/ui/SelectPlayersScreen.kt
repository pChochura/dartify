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
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.game.setup.players.ui.dialog.SelectCpuAverageDialog
import com.pointlessapps.dartify.compose.game.setup.players.ui.dialog.SelectPlayerNameDialog
import com.pointlessapps.dartify.compose.game.setup.ui.PlayerEntryCard
import com.pointlessapps.dartify.compose.game.setup.ui.defaultPlayerEntryCardModel
import com.pointlessapps.dartify.compose.ui.components.*
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SelectPlayersScreen(
    viewModel: SelectPlayersViewModel = getViewModel(),
    selectedPlayers: List<Player>,
    onPlayersSelected: (List<Player>) -> Unit,
) {
    var showCpuAverageDialog by remember { mutableStateOf(false) }
    var showPlayerNameDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedPlayers) {
        viewModel.setSelectedPlayers(selectedPlayers)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is SelectPlayersEvent.OnPlayersSelected -> onPlayersSelected(it.players)
                SelectPlayersEvent.AskForCpuAverage -> showCpuAverageDialog = true
                SelectPlayersEvent.AskForPlayerName -> showPlayerNameDialog = true
            }
        }
    }

    ComposeScaffoldLayout(
        topBar = { Title() },
        fab = { SaveButton(onSaveClicked = viewModel::onSaveClicked) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(id = R.dimen.margin_semi_big)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
            ),
        ) {
            item(key = R.string.label_selected) {
                ComposeText(
                    text = stringResource(id = R.string.label_selected),
                    textStyle = defaultComposeTextStyle().copy(
                        typography = MaterialTheme.typography.h2,
                        textColor = MaterialTheme.colors.onPrimary,
                    ),
                )
            }
            items(viewModel.state.selectedPlayers, key = { it.id }) { player ->
                PlayerEntryCard(
                    modifier = Modifier.animateItemPlacement(),
                    label = player.name,
                    onClick = { viewModel.onPlayerClicked(player) },
                    playerEntryCardModel = defaultPlayerEntryCardModel().copy(
                        mainIcon = R.drawable.ic_person,
                        additionalIcon = R.drawable.ic_close,
                    ),
                )
            }
            item(key = R.string.label_all) {
                ComposeText(
                    modifier = Modifier.padding(
                        top = dimensionResource(id = R.dimen.margin_small),
                    ),
                    text = stringResource(id = R.string.label_all),
                    textStyle = defaultComposeTextStyle().copy(
                        typography = MaterialTheme.typography.h2,
                        textColor = MaterialTheme.colors.onPrimary,
                    ),
                )
            }
            items(viewModel.state.allPlayers, key = { it.id }) { player ->
                PlayerEntryCard(
                    modifier = Modifier.animateItemPlacement(),
                    label = player.name,
                    onClick = { viewModel.onPlayerClicked(player) },
                    playerEntryCardModel = defaultPlayerEntryCardModel().copy(
                        mainIcon = R.drawable.ic_person,
                        additionalIcon = R.drawable.ic_plus,
                    ),
                )
            }
            item(key = R.string.save_and_close) {
                AddPlayerItem(onAddPlayerClicked = viewModel::onAddPlayerClicked)
            }
        }
    }

    if (showCpuAverageDialog) {
        SelectCpuAverageDialog(
            onSaveClicked = {
                viewModel.onPlayerAdded(it)
                showCpuAverageDialog = false
            },
            onDismissRequest = { showCpuAverageDialog = false },
        )
    }

    if (showPlayerNameDialog) {
        SelectPlayerNameDialog(
            onSaveClicked = {
                viewModel.onPlayerAdded(it)
                showPlayerNameDialog = false
            },
            onDismissRequest = { showPlayerNameDialog = false },
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
