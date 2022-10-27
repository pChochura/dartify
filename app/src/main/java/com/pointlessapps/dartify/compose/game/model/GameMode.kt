package com.pointlessapps.dartify.compose.game.model

import androidx.annotation.StringRes
import com.pointlessapps.dartify.R

internal enum class GameMode(
    @StringRes val label: Int,
    @StringRes val abbrev: Int,
    @StringRes val description: Int,
) {
    Straight(
        R.string.game_mode_straight,
        R.string.s,
        R.string.game_mode_straight_desc,
    ),
    Double(
        R.string.game_mode_double,
        R.string.d,
        R.string.game_mode_double_desc,
    ),
    Master(
        R.string.game_mode_master,
        R.string.m,
        R.string.game_mode_master_desc,
    ),
}
