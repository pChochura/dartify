package com.pointlessapps.dartify.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pointlessapps.dartify.R

@Composable
internal fun ComposeCounter(
    value: Int,
    maxValue: Int,
    minValue: Int,
    label: String,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    counterModel: ComposeCounterModel = defaultComposeCounterModel(),
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
            Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.margin_small)),
        ) {
            ComposeButton(
                label = null,
                onClick = { onChange(-1) },
                buttonModel = defaultComposeButtonModel().copy(
                    enabled = value > minValue,
                    icon = R.drawable.ic_minus,
                    size = ComposeButtonSize.Small,
                ),
            )
            ComposeText(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(counterModel.counterColor)
                    .defaultMinSize(minWidth = 50.dp)
                    .padding(
                        vertical = dimensionResource(id = R.dimen.margin_small),
                        horizontal = dimensionResource(id = R.dimen.margin_medium),
                    ),
                text = "$value",
                textStyle = defaultComposeTextStyle().copy(
                    typography = MaterialTheme.typography.h1.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    textAlign = TextAlign.Center,
                    textColor = counterModel.textColor,
                ),
            )
            ComposeButton(
                label = null,
                onClick = { onChange(1) },
                buttonModel = defaultComposeButtonModel().copy(
                    enabled = value < maxValue,
                    icon = R.drawable.ic_plus,
                    size = ComposeButtonSize.Small,
                ),
            )
        }

        ComposeText(
            text = label,
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.body1,
                textColor = counterModel.textColor,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
internal fun defaultComposeCounterModel() = ComposeCounterModel(
    counterColor = colorResource(id = R.color.red),
    textColor = MaterialTheme.colors.onPrimary,
)

internal data class ComposeCounterModel(
    val counterColor: Color,
    val textColor: Color,
)
