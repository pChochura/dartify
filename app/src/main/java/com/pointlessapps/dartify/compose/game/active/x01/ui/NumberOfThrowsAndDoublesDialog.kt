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
import kotlin.math.min

@Composable
internal fun NumberOfThrowsAndDoublesDialog(
    minNumberOfThrows: Int,
    maxNumberOfDoubles: Map<Int, Int>,
    onDoneClicked: (throws: Int, doubles: Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var numberOfThrows by remember { mutableStateOf(minNumberOfThrows) }
    var numberOfThrowsOnDouble by remember { mutableStateOf(1) }

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
                        onClick = {
                            numberOfThrows = it + 1
                            selectMaxNumberOfThrowsOnDoubleIfNecessary(
                                maxNumberOfDoubles,
                                numberOfThrows,
                                numberOfThrowsOnDouble,
                            )?.let { newNumberOfThrowsOnDouble ->
                                numberOfThrowsOnDouble = newNumberOfThrowsOnDouble
                            }
                        },
                        simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                            backgroundColor = if (numberOfThrows == it + 1) {
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
                            enabled = validateNumberOfThrows(minNumberOfThrows, it + 1),
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
                        onClick = { numberOfThrowsOnDouble = it },
                        simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                            backgroundColor = if (numberOfThrowsOnDouble == it) {
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
                            enabled = validateNumberOfDoubles(
                                maxNumberOfDoubles, numberOfThrows, it,
                            ),
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
                if (
                    validate(
                        minNumberOfThrows,
                        maxNumberOfDoubles,
                        numberOfThrows,
                        numberOfThrowsOnDouble,
                    )
                ) {
                    onDoneClicked(numberOfThrows, numberOfThrowsOnDouble)
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

private fun selectMaxNumberOfThrowsOnDoubleIfNecessary(
    maxNumberOfDoubles: Map<Int, Int>,
    numberOfThrows: Int,
    numberOfThrowsOnDouble: Int,
): Int? {
    if (!validateNumberOfDoubles(maxNumberOfDoubles, numberOfThrows, numberOfThrowsOnDouble)) {
        (numberOfThrowsOnDouble - 1 downTo 1).forEach {
            if (validateNumberOfDoubles(maxNumberOfDoubles, numberOfThrows, it)) {
                return it
            }
        }
    }

    return null
}

private fun validate(
    minNumberOfThrows: Int,
    maxNumberOfDoubles: Map<Int, Int>,
    numberOfThrows: Int,
    numberOfThrowsOnDouble: Int,
): Boolean = validateNumberOfThrows(minNumberOfThrows, numberOfThrows) &&
        validateNumberOfDoubles(maxNumberOfDoubles, numberOfThrows, numberOfThrowsOnDouble)

private fun validateNumberOfThrows(minNumberOfThrows: Int, numberOfThrows: Int) =
    numberOfThrows >= minNumberOfThrows

private fun validateNumberOfDoubles(
    maxNumberOfDoubles: Map<Int, Int>,
    numberOfThrows: Int,
    numberOfThrowsOnDouble: Int,
) = numberOfThrowsOnDouble in 1..min(
    numberOfThrows,
    maxNumberOfDoubles[numberOfThrows] ?: 1,
)
