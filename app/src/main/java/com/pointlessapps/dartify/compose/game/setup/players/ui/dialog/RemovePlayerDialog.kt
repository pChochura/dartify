package com.pointlessapps.dartify.compose.game.setup.players.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.*

@Composable
internal fun RemovePlayerDialog(
    onRemoveClicked: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(R.string.warning),
            icon = R.drawable.ic_warning,
        ),
    ) {
        ComposeText(
            text = stringResource(id = R.string.removing_player_desc),
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.body1,
            ),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_tiny),
            ),
        ) {
            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.remove),
                onClick = { onRemoveClicked() },
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = MaterialTheme.colors.secondary,
                    icon = R.drawable.ic_delete,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )

            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.undo),
                onClick = { onDismissRequest() },
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = colorResource(id = R.color.red),
                    icon = R.drawable.ic_undo,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )
        }

        ComposeHelpText(text = R.string.removing_player_help_message)
    }
}
