package com.pointlessapps.dartify.compose.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pointlessapps.dartify.R

@Composable
internal fun ComposeButton(
    label: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonModel: ComposeButtonModel = defaultComposeButtonModel(),
) {
    val shapeWidthMultiplier = when (buttonModel.shape) {
        ComposeButtonShape.Circle -> 1f
        ComposeButtonShape.Pill -> 1.5f
    }

    val sizeModifier = when (buttonModel.size) {
        ComposeButtonSize.Big -> R.dimen.button_size_big
        ComposeButtonSize.Medium -> R.dimen.button_size_medium
        ComposeButtonSize.Small -> R.dimen.button_size_small
    }.let { res ->
        Modifier.size(
            width = dimensionResource(id = res) * shapeWidthMultiplier,
            height = dimensionResource(id = res),
        )
    }

    val iconSizeModifier = when (buttonModel.size) {
        ComposeButtonSize.Big -> R.dimen.button_icon_size_big
        ComposeButtonSize.Medium -> R.dimen.button_icon_size_medium
        ComposeButtonSize.Small -> R.dimen.button_icon_size_small
    }.let { res ->
        Modifier.size(
            width = dimensionResource(id = res) * shapeWidthMultiplier,
            height = dimensionResource(id = res),
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
            Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            modifier = modifier.then(sizeModifier),
            contentPadding = PaddingValues(0.dp),
            elevation = null,
            shape = CircleShape,
            colors = buttonColors(backgroundColor = buttonModel.backgroundColor),
            onClick = onClick,
            enabled = buttonModel.enabled,
        ) {
            when (val it = buttonModel.content) {
                ComposeButtonContent.Icon -> Icon(
                    modifier = iconSizeModifier,
                    painter = painterResource(id = buttonModel.icon),
                    tint = buttonModel.textColor,
                    contentDescription = null,
                )
                is ComposeButtonContent.Custom -> it.content(this)
            }
        }

        label?.also {
            ComposeText(
                text = label,
                textStyle = defaultComposeTextStyle().copy(
                    typography = MaterialTheme.typography.body1,
                    textColor = buttonModel.textColor,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

@Composable
internal fun defaultComposeButtonModel() = ComposeButtonModel(
    backgroundColor = MaterialTheme.colors.primary,
    textColor = MaterialTheme.colors.onPrimary,
    shape = ComposeButtonShape.Circle,
    size = ComposeButtonSize.Big,
    icon = R.drawable.ic_settings,
    content = ComposeButtonContent.Icon,
    enabled = true,
)

internal data class ComposeButtonModel(
    val backgroundColor: Color,
    val textColor: Color,
    val shape: ComposeButtonShape,
    val size: ComposeButtonSize,
    @DrawableRes val icon: Int,
    val content: ComposeButtonContent,
    val enabled: Boolean,
)

internal enum class ComposeButtonSize {
    Big, Medium, Small
}

internal enum class ComposeButtonShape {
    Circle, Pill
}

internal sealed interface ComposeButtonContent {
    object Icon : ComposeButtonContent
    data class Custom(val content: @Composable RowScope.() -> Unit) : ComposeButtonContent
}
