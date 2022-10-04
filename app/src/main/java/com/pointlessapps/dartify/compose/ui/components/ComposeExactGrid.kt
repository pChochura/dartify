package com.pointlessapps.dartify.compose.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun ComposeExactGrid(
    rows: Int,
    columns: Int,
    modifier: Modifier = Modifier,
    itemContent: @Composable BoxScope.(x: Int, y: Int) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(rows) { y ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(columns) { x ->
                    Box(modifier = Modifier.weight(1f)) {
                        itemContent(x = x, y = y)
                    }
                }
            }
        }
    }
}
