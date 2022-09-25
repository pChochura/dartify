package com.pointlessapps.dartify.compose.game.setup.x01.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle

@Composable
internal fun TextListItem(
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textListItemModel: TextListItemModel = defaultTextListItemModel(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(textListItemModel.backgroundColor)
            .clickable { onClick() }
            .padding(dimensionResource(id = R.dimen.margin_small)),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
            Alignment.CenterVertically,
        ),
    ) {
        ComposeText(
            text = title,
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.h3.copy(
                    fontWeight = FontWeight.Bold,
                ),
                textColor = textListItemModel.textColor,
            ),
        )
        subtitle?.also {
            ComposeText(
                text = subtitle,
                textStyle = defaultComposeTextStyle().copy(
                    typography = MaterialTheme.typography.subtitle1,
                    textColor = textListItemModel.textColor,
                ),
            )
        }
    }
}

@Composable
internal fun defaultTextListItemModel() = TextListItemModel(
    backgroundColor = MaterialTheme.colors.secondary,
    textColor = MaterialTheme.colors.onSecondary,
)

internal data class TextListItemModel(
    val backgroundColor: Color,
    val textColor: Color,
)
