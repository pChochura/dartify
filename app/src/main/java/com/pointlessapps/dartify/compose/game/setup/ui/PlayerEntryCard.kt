package com.pointlessapps.dartify.compose.game.setup.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle

@Composable
internal fun PlayerEntryCard(
    label: String,
    onClick: () -> Unit,
    infoCardText: String? = null,
    modifier: Modifier = Modifier,
    playerEntryCardModel: PlayerEntryCardModel = defaultPlayerEntryCardModel(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(playerEntryCardModel.backgroundColor)
            .clickable { onClick() }
            .padding(dimensionResource(id = R.dimen.margin_small)),
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
            Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(playerEntryCardModel.mainIconBackgroundColor)
                .padding(dimensionResource(id = R.dimen.margin_tiny)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.player_entry_card_icon_size)),
                painter = painterResource(id = playerEntryCardModel.mainIcon),
                tint = playerEntryCardModel.textColor,
                contentDescription = null,
            )
        }
        ComposeText(
            modifier = Modifier.weight(1f),
            text = label,
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.h3,
                textColor = playerEntryCardModel.textColor,
            ),
        )
        if (infoCardText != null) {
            ComposeText(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(playerEntryCardModel.infoCardBackgroundColor)
                    .padding(
                        vertical = dimensionResource(id = R.dimen.margin_tiny),
                        horizontal = dimensionResource(id = R.dimen.margin_medium),
                    ),
                text = infoCardText,
                textStyle = defaultComposeTextStyle().copy(
                    typography = MaterialTheme.typography.h2,
                    textColor = playerEntryCardModel.textColor,
                    textAlign = TextAlign.Center,
                ),
            )
        }
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.player_entry_card_icon_size)),
            painter = painterResource(id = playerEntryCardModel.additionalIcon),
            tint = playerEntryCardModel.textColor,
            contentDescription = null,
        )
    }
}

@Composable
internal fun defaultPlayerEntryCardModel() = PlayerEntryCardModel(
    backgroundColor = MaterialTheme.colors.secondary,
    mainIconBackgroundColor = MaterialTheme.colors.primary,
    infoCardBackgroundColor = MaterialTheme.colors.primary,
    textColor = MaterialTheme.colors.onSecondary,
    mainIcon = R.drawable.ic_person,
    additionalIcon = R.drawable.ic_move_handle,
)

internal data class PlayerEntryCardModel(
    val backgroundColor: Color,
    val mainIconBackgroundColor: Color,
    val infoCardBackgroundColor: Color,
    val textColor: Color,
    @DrawableRes val mainIcon: Int,
    @DrawableRes val additionalIcon: Int,
)
