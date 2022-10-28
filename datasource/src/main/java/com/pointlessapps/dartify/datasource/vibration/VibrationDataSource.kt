package com.pointlessapps.dartify.datasource.vibration

interface VibrationDataSource {
    fun tick()
    fun click()
    fun doubleClick()
    fun vibrate(times: Int, vibrationTime: Long, pauseTime: Long)
}
