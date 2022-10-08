package com.pointlessapps.dartify.compose.game.active.x01.ui

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
internal fun NumberOfThrowsAndDoublesDialog(
    minNumberOfThrows: Int,
    onDoneClicked: (throws: Int, doubles: Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var throwsInTotal by remember { mutableStateOf(0) }
    var throwsOnDoubles by remember { mutableStateOf(-1) }

    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.how_many_throws),
            icon = R.drawable.ic_darts,
            dismissible = false,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_tiny),
            ),
        ) {
            ComposeText(
                text = stringResource(id = R.string.how_many_throws_in_total),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onBackground,
                    typography = MaterialTheme.typography.subtitle1.copy(
                        fontSize = 10.sp,
                    ),
                ),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                repeat(3) {
                    ComposeSimpleButton(
                        modifier = Modifier.weight(1f),
                        label = "${it + 1}",
                        onClick = { throwsInTotal = it + 1 },
                        simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                            backgroundColor = if (throwsInTotal == it + 1) {
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
                            enabled = it + 1 >= minNumberOfThrows,
                        ),
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_tiny),
            ),
        ) {
            ComposeText(
                text = stringResource(id = R.string.how_many_throws_on_double),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onBackground,
                    typography = MaterialTheme.typography.subtitle1.copy(
                        fontSize = 10.sp,
                    ),
                ),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                repeat(4) {
                    ComposeSimpleButton(
                        modifier = Modifier.weight(1f),
                        label = "$it",
                        onClick = { throwsOnDoubles = it },
                        simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                            backgroundColor = if (throwsOnDoubles == it) {
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
                            enabled = it <= throwsInTotal,
                        ),
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_tiny),
            ),
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.caption_icon_size)),
                painter = painterResource(id = R.drawable.ic_help),
                tint = MaterialTheme.colors.onBackground,
                contentDescription = null,
            )
            ComposeText(
                text = stringResource(id = R.string.how_many_throws_on_double_desc),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onBackground,
                    typography = MaterialTheme.typography.subtitle1.copy(
                        fontSize = 10.sp,
                    ),
                ),
            )
        }

        ComposeSimpleButton(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(id = R.string.done),
            onClick = {
                if (validate(throwsInTotal, throwsOnDoubles)) {
                    onDoneClicked(throwsInTotal, throwsOnDoubles)
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

private fun validate(throwsInTotal: Int, throwsOnDoubles: Int): Boolean =
    throwsInTotal in 1..3 && throwsOnDoubles in 0..throwsInTotal
