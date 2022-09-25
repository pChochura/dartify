package com.pointlessapps.dartify.compose.ui.components

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
import com.pointlessapps.dartify.R

@Composable
internal fun ComposeEntryCard(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    entryCardModel: ComposeEntryCardModel = defaultComposeEntryCardModel(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(entryCardModel.backgroundColor)
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
                .background(entryCardModel.mainIconBackgroundColor)
                .padding(dimensionResource(id = R.dimen.margin_tiny)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.entry_card_icon_size)),
                painter = painterResource(id = entryCardModel.mainIcon),
                tint = entryCardModel.textColor,
                contentDescription = null,
            )
        }
        ComposeText(
            modifier = Modifier.weight(1f),
            text = label,
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.h3,
                textColor = entryCardModel.textColor,
            ),
        )
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.entry_card_icon_size)),
            painter = painterResource(id = entryCardModel.additionalIcon),
            tint = entryCardModel.textColor,
            contentDescription = null,
        )
    }
}

@Composable
internal fun defaultComposeEntryCardModel() = ComposeEntryCardModel(
    backgroundColor = MaterialTheme.colors.secondary,
    mainIconBackgroundColor = MaterialTheme.colors.primary,
    textColor = MaterialTheme.colors.onSecondary,
    mainIcon = R.drawable.ic_person,
    additionalIcon = R.drawable.ic_move_handle,
)

internal data class ComposeEntryCardModel(
    val backgroundColor: Color,
    val mainIconBackgroundColor: Color,
    val textColor: Color,
    @DrawableRes val mainIcon: Int,
    @DrawableRes val additionalIcon: Int,
)
