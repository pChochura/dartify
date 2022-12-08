package com.pointlessapps.dartify.compose.game.setup.players.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.BotOptions
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.components.*

private const val MIN_AVERAGE = 30
private const val MAX_AVERAGE = 180
private const val DEFAULT_AVERAGE = 50
private const val AVERAGE_INCREMENT = 5

@Composable
internal fun SelectCpuAverageDialog(
    bot: Player? = null,
    onRemoveClicked: (Player) -> Unit,
    onSaveClicked: (Player) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val botNameTemplate = stringResource(id = R.string.cpu_avg)
    var average by remember { mutableStateOf(bot?.botOptions?.average?.toInt() ?: DEFAULT_AVERAGE) }

    fun getBot() = (bot ?: Player(name = "")).copy(
        name = String.format(botNameTemplate, average.toFloat()),
        botOptions = BotOptions(
            average = average.toFloat(),
        ),
    )

    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(
                id = if (bot != null) {
                    R.string.edit_the_cpu
                } else {
                    R.string.add_a_cpu
                },
            ),
            icon = R.drawable.ic_robot,
        ),
    ) {
        ComposeCounter(
            value = average,
            maxValue = MAX_AVERAGE,
            minValue = MIN_AVERAGE,
            label = stringResource(id = R.string.average),
            onChange = { average += it * AVERAGE_INCREMENT },
            counterModel = defaultComposeCounterModel().copy(
                counterColor = colorResource(id = R.color.red),
            ),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_tiny),
            ),
        ) {
            if (bot != null) {
                ComposeSimpleButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.remove),
                    onClick = { onRemoveClicked(bot) },
                    simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                        backgroundColor = MaterialTheme.colors.secondary,
                        icon = R.drawable.ic_delete,
                        orientation = ComposeSimpleButtonOrientation.Horizontal,
                    ),
                )
            }

            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.save),
                onClick = { onSaveClicked(getBot()) },
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = colorResource(id = R.color.red),
                    icon = R.drawable.ic_done,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )
        }
    }
}
