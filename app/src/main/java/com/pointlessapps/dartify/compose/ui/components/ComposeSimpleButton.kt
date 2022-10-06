package com.pointlessapps.dartify.compose.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pointlessapps.dartify.R

@Composable
internal fun ComposeSimpleButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    simpleButtonModel: ComposeSimpleButtonModel = defaultComposeSimpleButtonModel(),
) {
    Button(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        elevation = null,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(backgroundColor = simpleButtonModel.backgroundColor),
        onClick = onClick,
        enabled = simpleButtonModel.enabled,
    ) {
        val content = @Composable {
            if (simpleButtonModel.icon != null) {
                Icon(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.button_icon_size))
                        .then(iconModifier),
                    painter = painterResource(id = simpleButtonModel.icon),
                    tint = simpleButtonModel.textStyle.textColor,
                    contentDescription = null,
                )
            }

            ComposeText(
                modifier = Modifier,
                text = label,
                textStyle = simpleButtonModel.textStyle,
            )
        }

        when (simpleButtonModel.orientation) {
            ComposeSimpleButtonOrientation.Vertical -> Column(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.margin_small)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                content()
            }
            ComposeSimpleButtonOrientation.Horizontal -> Row(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.margin_small)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                content()
            }
        }
    }
}

@Composable
internal fun defaultComposeSimpleButtonModel() = ComposeSimpleButtonModel(
    backgroundColor = MaterialTheme.colors.primary,
    icon = R.drawable.ic_settings,
    orientation = ComposeSimpleButtonOrientation.Horizontal,
    textStyle = defaultComposeTextStyle().copy(
        textColor = MaterialTheme.colors.onPrimary,
        typography = MaterialTheme.typography.h3.copy(
            fontWeight = FontWeight.Bold,
        ),
    ),
    enabled = true,
)

internal data class ComposeSimpleButtonModel(
    val backgroundColor: Color,
    @DrawableRes val icon: Int?,
    val orientation: ComposeSimpleButtonOrientation,
    val textStyle: ComposeTextStyle,
    val enabled: Boolean,
)

internal enum class ComposeSimpleButtonOrientation {
    Vertical, Horizontal
}
