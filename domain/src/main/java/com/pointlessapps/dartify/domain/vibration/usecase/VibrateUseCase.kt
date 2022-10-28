package com.pointlessapps.dartify.domain.vibration.usecase

import com.pointlessapps.dartify.domain.vibration.VibrationRepository

class VibrateUseCase(
    private val vibrationRepository: VibrationRepository,
) {

    fun once() = vibrationRepository.vibrateOnce()
    fun twice() = vibrationRepository.vibrateTwice()

    fun tick() = vibrationRepository.tick()
    fun click() = vibrationRepository.click()

    fun error() = vibrationRepository.doubleClick()
}
