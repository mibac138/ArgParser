/*
 * Copyright (c) 2017 Michał Bączkowski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.mibac138.argparser.reader

import com.github.mibac138.argparser.reader.ArgumentString.Companion.NOT_REQUIRED

class EcoFriendlyString(private val recyclingThreshold: Int = 80) : RegularString() {
    private var length = 0
    private var offset = 0

    override fun recycle() {
        val eligible = eligibleForRecycling()
        if (eligible > recyclingThreshold) {
            content = content.substring(eligible)
            offset += eligible
            pos -= eligible

            if (minPos != NOT_REQUIRED)
                minPos -= eligible
        }
    }

    override fun append(string: String) {
        super.append(string)
        length += string.length
    }

    override fun getLength(): Int
            = length

    private fun eligibleForRecycling(): Int
            = when (minPos) {
        NOT_REQUIRED -> pos
        else -> Math.min(minPos, pos)
    }

    override fun markRequiredPosition() {
        super.markRequiredPosition()
        recycle()
    }

    override fun removeRequiredPosition() {
        super.removeRequiredPosition()
        recycle()
    }

    override fun read(amount: Int): String {
        val result = super.read(amount)
        recycle()
        return result
    }

    override fun getPosition(): Int
            = super.getPosition() + offset

    override fun getRequiredPosition(): Int {
        if (minPos == NOT_REQUIRED)
            return NOT_REQUIRED
        return minPos + offset
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as EcoFriendlyString

        if (recyclingThreshold != other.recyclingThreshold) return false
        if (length != other.length) return false
        if (offset != other.offset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recyclingThreshold
        result = 31 * result + length
        result = 31 * result + offset
        return result
    }
}