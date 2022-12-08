package com.pointlessapps.dartify.compose.game.active.x01.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.*

@Composable
internal fun NumberOfDoublesDialog(
    minNumberOfDoubles: Int,
    maxNumberOfDoubles: Int,
    onUndoLastMoveClicked: () -> Unit,
    onDoneClicked: (Int) -> Unit,
) {
    var numberOfDoubles by remember { mutableStateOf(minNumberOfDoubles) }

    ComposeDialog(
        onDismissRequest = onUndoLastMoveClicked,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.how_many_throws_on_double),
            icon = R.drawable.ic_darts,
            dismissible = ComposeDialogDismissible.OnBackPress,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
            ),
        ) {
            repeat(4) {
                ComposeSimpleButton(
                    modifier = Modifier.weight(1f),
                    label = "$it",
                    onClick = { numberOfDoubles = it },
                    simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                        backgroundColor = if (numberOfDoubles == it) {
                            colorResource(id = R.color.red)
                        } else {
                            MaterialTheme.colors.secondary
                        },
                        icon = null,
                        orientation = ComposeSimpleButtonOrientation.Horizontal,
                        textStyle = defaultComposeTextStyle().copy(
                            textAlign = TextAlign.Center,
                            textColor = MaterialTheme.colors.onSecondary,
                            typography = MaterialTheme.typography.h1.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                            ),
                        ),
                        enabled = it in minNumberOfDoubles..maxNumberOfDoubles,
                    ),
                )
            }
        }

        ComposeHelpText(text = R.string.how_many_throws_on_double_desc)

        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_tiny),
            ),
        ) {
            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.undo_last_move),
                onClick = onUndoLastMoveClicked,
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = MaterialTheme.colors.secondary,
                    icon = R.drawable.ic_undo,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )
            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.done),
                onClick = {
                    if (
                        validateNumberOfDoubles(
                            minNumberOfDoubles,
                            maxNumberOfDoubles,
                            numberOfDoubles,
                        )
                    ) {
                        onDoneClicked(numberOfDoubles)
                    }
                },
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = colorResource(id = R.color.red),
                    icon = R.drawable.ic_done,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )
        }
    }
}

private fun validateNumberOfDoubles(
    minNumberOfDoubles: Int,
    maxNumberOfDoubles: Int,
    numberOfDoubles: Int,
) = numberOfDoubles in minNumberOfDoubles..maxNumberOfDoubles
