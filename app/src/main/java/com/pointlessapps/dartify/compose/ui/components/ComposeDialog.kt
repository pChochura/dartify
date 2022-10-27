package com.pointlessapps.dartify.compose.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pointlessapps.dartify.R

@Composable
internal fun ComposeDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dialogModel: ComposeDialogModel = defaultComposeDialogModel(),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dialogModel.dismissible.isDismissibleOnBackPress(),
            dismissOnClickOutside = dialogModel.dismissible.isDismissibleOnClickOutside(),
        ),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        dimensionResource(id = R.dimen.medium_rounded_corners),
                    ),
                )
                .background(dialogModel.backgroundColor)
                .padding(dimensionResource(id = R.dimen.margin_big)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_semi_big),
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.dialog_icon_size)),
                    painter = painterResource(id = dialogModel.icon),
                    tint = dialogModel.textColor,
                    contentDescription = null,
                )
                ComposeText(
                    text = dialogModel.label,
                    textStyle = defaultComposeTextStyle().copy(
                        typography = MaterialTheme.typography.h2,
                        textColor = dialogModel.textColor,
                    ),
                )
            }

            content()
        }
    }
}

@Composable
internal fun defaultComposeDialogModel() = ComposeDialogModel(
    backgroundColor = MaterialTheme.colors.background,
    textColor = MaterialTheme.colors.onBackground,
    label = "",
    icon = R.drawable.ic_settings,
    dismissible = ComposeDialogDismissible.Both,
)

internal data class ComposeDialogModel(
    val backgroundColor: Color,
    val textColor: Color,
    val label: String,
    @DrawableRes val icon: Int,
    val dismissible: ComposeDialogDismissible,
)

internal enum class ComposeDialogDismissible {
    None, OnBackPress, OnClickOutside, Both;

    fun isDismissibleOnBackPress() = this in setOf(
        OnBackPress, Both,
    )

    fun isDismissibleOnClickOutside() = this in setOf(
        OnClickOutside, Both,
    )
}
