package com.pointlessapps.dartify.compose.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.utils.conditional
import java.util.*

@Composable
internal fun ComposeSwitcher(
    values: List<ComposeSwitcherValue>,
    selectedValue: ComposeSwitcherValue,
    onSelect: (ComposeSwitcherValue) -> Unit,
    modifier: Modifier = Modifier,
    switcherModel: ComposeSwitcherModel = defaultComposeSwitcherModel(),
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(switcherModel.backgroundColor),
    ) {
        values.forEach { value ->
            Box(
                modifier = Modifier
                    .conditional(selectedValue == value) {
                        background(switcherModel.selectedBackgroundColor)
                    }
                    .clickable { onSelect(value) }
                    .padding(
                        vertical = dimensionResource(id = R.dimen.margin_medium),
                        horizontal = dimensionResource(id = R.dimen.margin_medium),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                ComposeText(
                    text = stringResource(id = value.label),
                    textStyle = defaultComposeTextStyle().copy(
                        typography = MaterialTheme.typography.h2,
                        textColor = switcherModel.textColor,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}

@Composable
internal fun defaultComposeSwitcherModel() = ComposeSwitcherModel(
    backgroundColor = MaterialTheme.colors.primary,
    selectedBackgroundColor = colorResource(id = R.color.red),
    textColor = MaterialTheme.colors.onPrimary,
)

internal data class ComposeSwitcherModel(
    val backgroundColor: Color,
    val selectedBackgroundColor: Color,
    val textColor: Color,
)

internal data class ComposeSwitcherValue(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    @StringRes val label: Int,
)
