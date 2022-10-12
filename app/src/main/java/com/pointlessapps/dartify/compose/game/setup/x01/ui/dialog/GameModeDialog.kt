package com.pointlessapps.dartify.compose.game.setup.x01.ui.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.GameMode
import com.pointlessapps.dartify.compose.game.setup.x01.ui.TextListItem
import com.pointlessapps.dartify.compose.ui.components.ComposeDialog
import com.pointlessapps.dartify.compose.ui.components.defaultComposeDialogModel

@Composable
internal fun GameModeDialog(
    @StringRes label: Int,
    onButtonClicked: (GameMode) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = label),
            icon = R.drawable.ic_darts,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
            ),
        ) {
            GameMode.values().forEach { gameMode ->
                TextListItem(
                    title = stringResource(id = gameMode.label),
                    subtitle = stringResource(id = gameMode.description),
                    onClick = { onButtonClicked(gameMode) },
                )
            }
        }
    }
}
