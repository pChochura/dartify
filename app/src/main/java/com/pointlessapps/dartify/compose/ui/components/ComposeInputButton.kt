package com.pointlessapps.dartify.compose.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

@Composable
internal fun ComposeInputButton(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    inputButtonModel: ComposeInputButtonModel = defaultComposeInputButtonModel(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = dimensionResource(id = R.dimen.input_button_border_width),
                color = inputButtonModel.backgroundColor,
                shape = MaterialTheme.shapes.medium,
            )
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(dimensionResource(id = R.dimen.margin_small)),
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
            Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.input_button_icon_size)),
            painter = painterResource(id = inputButtonModel.icon),
            tint = inputButtonModel.textColor,
            contentDescription = null,
        )
        ComposeText(
            modifier = Modifier.weight(1f),
            text = label,
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.h2,
                textColor = inputButtonModel.textColor,
            ),
        )
        ComposeText(
            modifier = Modifier
                .clip(CircleShape)
                .background(inputButtonModel.backgroundColor)
                .padding(
                    vertical = dimensionResource(id = R.dimen.margin_tiny),
                    horizontal = dimensionResource(id = R.dimen.margin_medium),
                ),
            text = value,
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.h2,
                textColor = inputButtonModel.textColor,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
internal fun defaultComposeInputButtonModel() = ComposeInputButtonModel(
    backgroundColor = MaterialTheme.colors.secondary,
    textColor = MaterialTheme.colors.onSecondary,
    icon = R.drawable.ic_settings,
)

internal data class ComposeInputButtonModel(
    val backgroundColor: Color,
    val textColor: Color,
    @DrawableRes val icon: Int,
)
