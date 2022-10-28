package com.pointlessapps.dartify.domain.vibration

import com.pointlessapps.dartify.datasource.vibration.VibrationDataSource

interface VibrationRepository {
    /**
     * Performs a single vibration for [VIBRATION_TIME] ms.
     */
    fun vibrateOnce()

    /**
     * Performs a single vibration for [VIBRATION_TIME] ms, pauses for [PAUSE_TIME] ms and then
     * performs a single vibration for [VIBRATION_TIME] ms again.
     */
    fun vibrateTwice()

    /**
     * Performs a *tick* vibration effect. When it's not available, a single 30ms vibration will be
     * performed.
     */
    fun tick()

    /**
     * Performs a *click* vibration effect. When it's not available, a single 50ms vibration will be
     * performed.
     */
    fun click()

    /**
     * Performs a *double click* vibration effect. When it's not available, a double 50ms vibration
     * will be performed.
     */
    fun doubleClick()
}

internal class VibrationRepositoryImpl(
    private val vibrationDataSource: VibrationDataSource,
) : VibrationRepository {
    override fun vibrateOnce() = vibrationDataSource.vibrate(1, VIBRATION_TIME, PAUSE_TIME)
    override fun vibrateTwice() = vibrationDataSource.vibrate(2, VIBRATION_TIME, PAUSE_TIME)
    override fun tick() = vibrationDataSource.tick()
    override fun click() = vibrationDataSource.click()
    override fun doubleClick() = vibrationDataSource.doubleClick()
}
