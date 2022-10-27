package com.pointlessapps.dartify.compose.game.setup.players.ui.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.components.*

@Composable
internal fun SelectPlayerNameDialog(
    onSaveClicked: (Player) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var name by remember { mutableStateOf("") }

    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.add_a_player),
            icon = R.drawable.ic_person,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_semi_big),
            ),
        ) {
            ComposeTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = dimensionResource(id = R.dimen.input_button_border_width),
                        color = MaterialTheme.colors.secondary,
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(dimensionResource(id = R.dimen.margin_small)),
                value = name,
                onValueChange = { name = it },
                onImeAction = {
                    if (name.isNotBlank()) {
                        onSaveClicked(Player(name = name))
                    }
                },
                textFieldModel = defaultComposeTextFieldModel().copy(
                    placeholder = stringResource(id = R.string.name),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Words,
                    ),
                ),
            )

            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.save),
                onClick = { onSaveClicked(Player(name = name)) },
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = colorResource(id = R.color.red),
                    icon = R.drawable.ic_done,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )
        }
    }
}
