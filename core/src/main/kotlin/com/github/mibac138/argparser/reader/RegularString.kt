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

open class RegularString : ArgumentString {
    protected var content: StringBuilder = StringBuilder()
    protected var pos: Int = 0
    protected var minPos: Int = NOT_REQUIRED

    override fun getPosition(): Int
            = pos

    override fun addPosition(mod: Int)
            = setPosition(pos + mod)

    override fun getAvailableAmount(): Int
            = content.length - pos

    override fun getLength(): Int
            = content.length

    override fun append(array: CharArray, from: Int, to: Int) {
        if (from == 0 && to == array.size) {
            append(String(array))
        } else {
            append(String(array.copyOfRange(from, to)))
        }
    }

    override fun append(char: Char) {
        append(char.toString())
    }

    protected open fun append(string: String) {
        content.append(string)
    }

    override fun read(amount: Int): String {
        if (amount < 0)
            throw IllegalArgumentException("I can't read backwards, sorry")
        if (pos + amount > content.length)
            throw IllegalArgumentException("Tried to read more than available")

        val read = content.substring(pos, pos + amount)
        pos += amount
        return read
    }

    override fun recycle() {
    }

    override fun markRequiredPosition() {
        minPos = pos
    }

    override fun removeRequiredPosition() {
        if (minPos == NOT_REQUIRED)
            throw IllegalStateException("Tried to reset to required position even though there isn't one")

        minPos = NOT_REQUIRED
        recycle()
    }

    override fun resetToRequiredPosition() {
        if (minPos == NOT_REQUIRED)
            throw IllegalStateException("Tried to reset to required position even though there isn't one")

        pos = minPos
        minPos = NOT_REQUIRED
    }

    override fun getRequiredPosition()
            = minPos

    override fun setPosition(pos: Int) {
        if (pos < minPos || pos < 0)
            throw IllegalArgumentException()
        this.pos = pos
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as RegularString

        // StringBuilder doesn't implement equals
        if (content.toString() != other.content.toString()) return false
        if (pos != other.pos) return false
        if (minPos != other.minPos) return false

        return true
    }

    override fun hashCode(): Int {
        // StringBuilder doesn't implement hashCode
        var result = content.toString().hashCode()
        result = 31 * result + pos
        result = 31 * result + minPos
        return result
    }
}