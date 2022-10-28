package com.pointlessapps.dartify.local.datasource.vibration

import com.pointlessapps.dartify.datasource.vibration.VibrationDataSource
import com.pointlessapps.dartify.rumble.Rumble

internal class LocalVibrationDataSource(
    private val rumble: Rumble,
) : VibrationDataSource {

    override fun tick() = rumble.tick()
    override fun click() = rumble.click()
    override fun doubleClick() = rumble.doubleClick()
    override fun vibrate(times: Int, vibrationTime: Long, pauseTime: Long) = rumble.makePattern()
        .beat(vibrationTime)
        .rest(pauseTime)
        .playPattern(times)
}
