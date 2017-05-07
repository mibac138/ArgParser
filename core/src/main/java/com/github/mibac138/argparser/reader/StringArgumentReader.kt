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

class StringArgumentReader(private var string: String) : ArgumentReader {
    private var position: Int = 0
    private var minPosition: Int = -1
    private var length: Int = 0
    private val marks: IntQueue = IntQueue()

    init {
        length = string.length
    }


    override fun skip(count: Int) {
        ensureLength(count)
        position += count
        cleanBuffer()
    }

    override fun read(count: Int): String {
        ensureLength(count)

        val read = string.substring(position, position + count)
        position += count
        cleanBuffer()
        return read
    }

    override fun hasNext(): Boolean
            = string.length > position

    override fun next(): Char {
        ensureLength(1)
        val read = string.elementAt(position++)
        cleanBuffer()
        return read
    }

    override fun getLength(): Int
            = length

    override fun getAvailableCount(): Int
            = string.length - position

    override fun mark() {
        if (marks.size() == 0)
            minPosition = position
        marks.add(position)
    }

    override fun removeMark(): Boolean {
        if (marks.size() == 1) {
            minPosition = NOT_REQUIRED
            cleanBuffer()
        }

        return marks.poll() != null
    }

    override fun reset(): Boolean {
        position = marks.poll() ?: return false
        return true
    }


    private fun cleanBuffer() {
        if (minPosition == -1) {
            shrinkBuffer(position)
        } else {
            shrinkBuffer(Math.min(position, minPosition))
        }
    }

    private fun shrinkBuffer(amount: Int) {
        string = string.substring(amount)
        position -= amount
        if (minPosition != -1)
            minPosition -= amount

        marks.shift(amount)
    }

    private fun ensureLength(count: Int) {
        if (count < 0)
            throw IllegalArgumentException("Count can't be negative")
        if (position + count > string.length)
            throw IllegalArgumentException("Trying to access more than available")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as StringArgumentReader

        if (string != other.string) return false
        if (position != other.position) return false
        if (minPosition != other.minPosition) return false
        if (length != other.length) return false
        if (marks != other.marks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = string.hashCode()
        result = 31 * result + position
        result = 31 * result + minPosition
        result = 31 * result + length
        result = 31 * result + marks.hashCode()
        return result
    }

    override fun toString(): String {
        return "StringArgumentReader(string='$string', position=$position, minPosition=$minPosition, length=$length, marks=$marks)"
    }
}