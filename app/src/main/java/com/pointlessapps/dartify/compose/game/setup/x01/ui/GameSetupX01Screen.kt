package com.pointlessapps.dartify.compose.game.setup.x01.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.setup.ui.PlayerEntryCard
import com.pointlessapps.dartify.compose.game.setup.ui.defaultPlayerEntryCardModel
import com.pointlessapps.dartify.compose.ui.components.*

@Composable
internal fun GameSetupX01Screen() {
    var startingScoreDialogModel by remember { mutableStateOf<StartingScoreDialogModel?>(null) }
    var gameModeDialogModel by remember { mutableStateOf<GameModeDialogModel?>(null) }

    ComposeScaffoldLayout(
        topBar = { Title() },
        fab = { StartGameButton() },
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
            MatchSettings(onShowStartingScoreDialog = { startingScoreDialogModel = it })
            GameModes(onShowGameModeDialog = { gameModeDialogModel = it })
            Players()
        }
    }

    startingScoreDialogModel?.let {
        ComposeDialog(
            onDismissRequest = { startingScoreDialogModel = null },
            dialogModel = defaultComposeDialogModel().copy(
                label = stringResource(id = R.string.starting_score),
                icon = R.drawable.ic_score,
            ),
        ) {
            var customStartingScoreValue by remember { mutableStateOf("") }
            val startingScoreValues = listOf("301", "501", "701")
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                startingScoreValues.forEach { item ->
                    TextListItem(
                        title = item,
                        subtitle = null,
                        onClick = { startingScoreDialogModel = null },
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.margin_small),
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ComposeTextField(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = dimensionResource(id = R.dimen.input_button_border_width),
                                color = MaterialTheme.colors.secondary,
                                shape = MaterialTheme.shapes.small,
                            )
                            .padding(dimensionResource(id = R.dimen.margin_small)),
                        value = customStartingScoreValue,
                        onValueChange = { customStartingScoreValue = it },
                        textFieldModel = defaultComposeTextFieldModel().copy(
                            placeholder = stringResource(id = R.string.custom),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                        ),
                    )
                    ComposeButton(
                        label = null,
                        onClick = { startingScoreDialogModel = null },
                        buttonModel = defaultComposeButtonModel().copy(
                            icon = R.drawable.ic_done,
                            size = ComposeButtonSize.Small,
                            backgroundColor = colorResource(id = R.color.red),
                        ),
                    )
                }
            }
        }
    }

    gameModeDialogModel?.let { model ->
        ComposeDialog(
            onDismissRequest = { gameModeDialogModel = null },
            dialogModel = defaultComposeDialogModel().copy(
                label = stringResource(id = model.label),
                icon = R.drawable.ic_darts,
            ),
        ) {
            val gameModesValues = listOf(
                "straight" to "Any score counts",
                "double" to "Only double scores count",
                "master" to "Only double or treble scores count",
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                gameModesValues.forEach { (title, subtitle) ->
                    TextListItem(
                        title = title,
                        subtitle = subtitle,
                        onClick = { gameModeDialogModel = null },
                    )
                }
            }
        }
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
private fun StartGameButton() {
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
            onClick = { /*TODO*/ },
        )
    }
}

@Composable
private fun MatchSettings(
    onShowStartingScoreDialog: (StartingScoreDialogModel) -> Unit,
) {
    val firstTo = stringResource(id = R.string.first_to)
    val bestOf = stringResource(id = R.string.best_of)
    val switcherValues by remember {
        mutableStateOf(
            listOf(
                ComposeSwitcherValue(label = firstTo),
                ComposeSwitcherValue(label = bestOf),
            ),
        )
    }
    var selectedSwitcherValue by remember { mutableStateOf(switcherValues.random()) }
    var setsValue by remember { mutableStateOf(1) }
    var legsValue by remember { mutableStateOf(3) }
    val startingScore by remember { mutableStateOf("501") }

    ComposeSwitcher(
        values = switcherValues,
        selectedValue = selectedSwitcherValue,
        onSelect = { selectedSwitcherValue = it },
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
        ),
    ) {
        ComposeCounter(
            value = setsValue,
            maxValue = 5,
            minValue = 1,
            label = stringResource(id = R.string.sets),
            onChange = { setsValue += it },
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
            value = legsValue,
            maxValue = 5,
            minValue = 1,
            label = stringResource(id = R.string.legs),
            onChange = { legsValue += it },
            counterModel = defaultComposeCounterModel().copy(
                counterColor = colorResource(id = R.color.red),
            ),
        )
    }

    ComposeInputButton(
        label = stringResource(id = R.string.starting_score),
        value = startingScore,
        onClick = { onShowStartingScoreDialog(StartingScoreDialogModel(enabled = true)) },
        inputButtonModel = defaultComposeInputButtonModel().copy(
            icon = R.drawable.ic_score,
        ),
    )
}

@Composable
private fun GameModes(
    onShowGameModeDialog: (GameModeDialogModel) -> Unit,
) {
    val inMode by remember { mutableStateOf("straight") }
    val outMode by remember { mutableStateOf("double") }

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
            value = inMode,
            onClick = {
                onShowGameModeDialog(
                    GameModeDialogModel(
                        enabled = true,
                        label = R.string.in_mode,
                    ),
                )
            },
            inputButtonModel = defaultComposeInputButtonModel().copy(
                icon = R.drawable.ic_darts,
            ),
        )
        ComposeInputButton(
            label = stringResource(id = R.string.out_mode),
            value = outMode,
            onClick = {
                onShowGameModeDialog(
                    GameModeDialogModel(
                        enabled = true,
                        label = R.string.out_mode,
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
private fun Players() {
    val items by remember { mutableStateOf(listOf("You" to null, "Paweł" to "D-OUT", "Dominik" to null)) }
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

        items.forEach { (name, infoCard) ->
            PlayerEntryCard(
                label = name,
                onClick = { /*TODO*/ },
                infoCardText = infoCard,
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
                .clickable { /*TODO*/ }
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
)

private data class StartingScoreDialogModel(
    val enabled: Boolean = false,
)
