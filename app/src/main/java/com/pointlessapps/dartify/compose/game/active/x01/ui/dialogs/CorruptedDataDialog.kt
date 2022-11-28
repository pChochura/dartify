package com.pointlessapps.dartify.compose.game.active.x01.ui.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.*

@Composable
internal fun CorruptedDataDialog(
    onDiscardAndCloseClicked: () -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onDiscardAndCloseClicked,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.saved_game_has_been_corrupted),
            icon = R.drawable.ic_empty,
            dismissible = ComposeDialogDismissible.None,
        ),
    ) {
        ComposeText(
            text = stringResource(id = R.string.corrupted_game_desc),
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.body1,
            ),
        )
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
