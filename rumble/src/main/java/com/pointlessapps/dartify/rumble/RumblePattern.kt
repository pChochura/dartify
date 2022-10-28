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

class RumblePattern internal constructor(
    private val onPlayPatternCallback: (LongArray) -> Unit,
) {
    private val internalPattern: MutableList<Long>

    init {
        internalPattern = ArrayList()
        internalPattern.add(0L)
    }

    fun beat(milliseconds: Long): RumblePattern {
        if (internalPattern.size % 2 == 0) {
            internalPattern[internalPattern.size - 1] =
                internalPattern[internalPattern.size - 1] + milliseconds
        } else {
            internalPattern.add(milliseconds)
        }

        return this
    }

    fun rest(milliseconds: Long): RumblePattern {
        if (internalPattern.size % 2 == 0) {
            internalPattern.add(milliseconds)
        } else {
            internalPattern[internalPattern.size - 1] =
                internalPattern[internalPattern.size - 1] + milliseconds
        }

        return this
    }

    fun playPattern(numberOfTimes: Int = 1) {
        if (numberOfTimes < 0) {
            throw IllegalArgumentException("numberOfTimes must be >= 0")
        }

        val endsWithRest = internalPattern.size % 2 == 0

        // We have a List<Long> but we need a long[]. We can't simply use toArray because that
        // yields a Long[]. Reserve enough space to hold the full pattern as many times as
        // necessary to play the pattern the right number of times.
        val primitiveArray = LongArray(
            internalPattern.size * numberOfTimes - if (endsWithRest) 0 else numberOfTimes - 1,
        )

        for (i in internalPattern.indices) {
            // Auto unboxing converts each Long to a long.
            primitiveArray[i] = internalPattern[i]
        }

        // Copy the array into itself to duplicate the pattern enough times.
        // Not a simple copy - we must overlay the copies if the pattern ends in a rest.
        //   R    B    R
        // [100, 300, 500]
        //             +
        //           [100, 300, 500]
        for (i in 1 until numberOfTimes) {
            for (j in internalPattern.indices) {
                val k = j + internalPattern.size * i - if (endsWithRest) 0 else i
                primitiveArray[k] += primitiveArray[j]
            }
        }

        onPlayPatternCallback(primitiveArray)
    }

    override fun toString() = "RumblePattern{internalPattern=$internalPattern}"
}
