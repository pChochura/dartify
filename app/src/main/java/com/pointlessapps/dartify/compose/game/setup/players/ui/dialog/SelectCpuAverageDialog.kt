package com.pointlessapps.dartify.compose.game.setup.players.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.Bot
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.components.*

private const val MIN_AVERAGE = 30
private const val MAX_AVERAGE = 180
private const val DEFAULT_AVERAGE = 50
private const val AVERAGE_INCREMENT = 5

@Composable
internal fun SelectCpuAverageDialog(
    onSaveClicked: (Player) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val botNameTemplate = stringResource(id = R.string.cpu_avg)
    var average by remember { mutableStateOf(DEFAULT_AVERAGE) }

    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.add_a_cpu),
            icon = R.drawable.ic_robot,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_semi_big),
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

            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.save),
                onClick = {
                    onSaveClicked(
                        Bot(
                            average = average.toFloat(),
                            name = String.format(botNameTemplate, average.toFloat()),
                        ),
                    )
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
