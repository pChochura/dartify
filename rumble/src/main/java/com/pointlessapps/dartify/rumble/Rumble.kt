/*
MIT License

Copyright (c) 2018 Joseph Meir Rubin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.pointlessapps.dartify.rumble

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

interface Rumble {
    fun tick()
    fun click()
    fun doubleClick()
    fun makePattern(): RumblePattern
    fun stop()
}

internal class RumbleImpl(context: Context) : Rumble {

    private companion object {
        const val TICK_TIME = 30L
        const val CLICK_TIME = 50L
        const val PAUSE_TIME = 10L
    }

    private val vibrator: Vibrator
    private var rumbleDisabled: Boolean = false

    init {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            context.getSystemService(Vibrator::class.java)
        }

        rumbleDisabled = !vibrator.hasVibrator()
    }

    private fun apiIndependentVibrate(pattern: LongArray) {
        if (rumbleDisabled) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    override fun stop() {
        if (rumbleDisabled) {
            return
        }

        vibrator.cancel()
    }

    override fun tick() {
        if (rumbleDisabled) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(TICK_TIME)
        }
    }

    override fun click() {
        if (rumbleDisabled) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            makePattern().beat(CLICK_TIME).playPattern(1)
        }
    }

    override fun doubleClick() {
        if (rumbleDisabled) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
        } else {
            makePattern()
                .beat(CLICK_TIME)
                .rest(PAUSE_TIME)
                .playPattern(2)
        }
    }

    override fun makePattern() = RumblePattern(
        onPlayPatternCallback = ::apiIndependentVibrate,
    )
}
