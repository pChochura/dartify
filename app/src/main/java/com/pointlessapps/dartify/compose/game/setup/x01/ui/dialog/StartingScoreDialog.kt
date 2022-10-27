package com.pointlessapps.dartify.compose.game.setup.x01.ui.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.setup.x01.ui.GameSetupX01ViewModel
import com.pointlessapps.dartify.compose.game.setup.x01.ui.TextListItem
import com.pointlessapps.dartify.compose.ui.components.*

@Composable
internal fun StartingScoreDialog(
    onButtonClicked: (Int?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.starting_score),
            icon = R.drawable.ic_score,
            dismissible = ComposeDialogDismissible.Both,
        ),
    ) {
        var customStartingScoreValue by remember { mutableStateOf("") }
        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
            ),
        ) {
            GameSetupX01ViewModel.STARTING_SCORES.forEach { item ->
                TextListItem(
                    title = item.toString(),
                    subtitle = null,
                    onClick = { onButtonClicked(item) },
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ComposeTextField(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = dimensionResource(id = R.dimen.input_button_border_width),
                            color = MaterialTheme.colors.secondary,
                            shape = MaterialTheme.shapes.small,
                        )
                        .padding(dimensionResource(id = R.dimen.margin_small)),
                    value = customStartingScoreValue,
                    onValueChange = { customStartingScoreValue = it },
                    onImeAction = {
                        if (customStartingScoreValue.isNotBlank()) {
                            onButtonClicked(customStartingScoreValue.toIntOrNull())
                        }
                    },
                    textFieldModel = defaultComposeTextFieldModel().copy(
                        placeholder = stringResource(id = R.string.custom),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                    ),
                )
                ComposeButton(
                    label = null,
                    onClick = {
                        if (customStartingScoreValue.isNotBlank()) {
                            onButtonClicked(customStartingScoreValue.toIntOrNull())
                        }
                    },
                    buttonModel = defaultComposeButtonModel().copy(
                        icon = R.drawable.ic_done,
                        size = ComposeButtonSize.Small,
                        backgroundColor = colorResource(id = R.color.red),
                    ),
                )
            }
        }
    }
}
