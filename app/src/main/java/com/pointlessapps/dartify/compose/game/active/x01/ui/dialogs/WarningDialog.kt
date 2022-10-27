package com.pointlessapps.dartify.compose.game.active.x01.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.*

@Composable
internal fun WarningDialog(
    onRestartClicked: () -> Unit,
    onSaveAndCloseClicked: () -> Unit,
    onDiscardAndCloseClicked: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.warning),
            icon = R.drawable.ic_warning,
            dismissible = true,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
            ),
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                ComposeSimpleButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    label = stringResource(id = R.string.restart),
                    onClick = onRestartClicked,
                    simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                        backgroundColor = MaterialTheme.colors.secondary,
                        icon = R.drawable.ic_restart,
                        orientation = ComposeSimpleButtonOrientation.Vertical,
                        textStyle = defaultComposeSimpleButtonTextStyle().copy(
                            textAlign = TextAlign.Center,
                        ),
                    ),
                )
                ComposeSimpleButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    label = stringResource(id = R.string.save_and_close),
                    onClick = onSaveAndCloseClicked,
                    simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                        backgroundColor = MaterialTheme.colors.secondary,
                        icon = R.drawable.ic_save,
                        orientation = ComposeSimpleButtonOrientation.Vertical,
                        textStyle = defaultComposeSimpleButtonTextStyle().copy(
                            textAlign = TextAlign.Center,
                        ),
                    ),
                )
            }
            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.discard_and_close),
                onClick = onDiscardAndCloseClicked,
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = colorResource(id = R.color.red),
                    icon = R.drawable.ic_warning,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )
        }
    }
}
