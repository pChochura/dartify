package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.model.Bot
import com.pointlessapps.dartify.compose.ui.components.*
import com.pointlessapps.dartify.compose.utils.toPercentage

@Composable
internal fun WinnerDialog(
    playerScore: PlayerScore,
    onShowGameStatsClicked: () -> Unit,
    onSaveAndCloseClicked: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.and_the_winner_is),
            icon = R.drawable.ic_trophy,
            dismissible = false,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
            ),
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
                    .padding(dimensionResource(id = R.dimen.margin_small)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.winner_card_icon_size)),
                    painter = painterResource(
                        id = if (playerScore.player is Bot) {
                            R.drawable.ic_robot
                        } else {
                            R.drawable.ic_person
                        },
                    ),
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = null,
                )
            }
            ComposeText(
                text = playerScore.player.name,
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                    typography = MaterialTheme.typography.h1.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                ),
            )
            ScoreStatisticEntry(
                icon = R.drawable.ic_darts,
                value = stringResource(
                    id = R.string.statistic_double,
                    playerScore.doublePercentage.toPercentage(),
                ),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                ScoreStatisticEntry(
                    icon = R.drawable.ic_score,
                    value = stringResource(id = R.string.statistic_max, playerScore.max),
                )
                ScoreStatisticEntry(
                    icon = R.drawable.ic_stats,
                    value = stringResource(
                        id = R.string.statistic_avg,
                        playerScore.average,
                    ),
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
            ),
        ) {
            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.show_game_stats),
                onClick = onShowGameStatsClicked,
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = MaterialTheme.colors.secondary,
                    icon = R.drawable.ic_stats,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )
            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.save_and_close),
                onClick = onSaveAndCloseClicked,
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = colorResource(id = R.color.red),
                    icon = R.drawable.ic_done,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )
        }
    }
}
