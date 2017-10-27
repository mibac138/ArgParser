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
import java.io.Reader
import java.util.*

class ReaderArgumentReader(private val stream: Reader, private var buffer: ArgumentString) : ArgumentReader {
    private val MIN_READ_AMOUNT: Int = 20

    private val marks = LinkedList<Int>()

    @JvmOverloads
    constructor(stream: Reader, recycleString: Boolean = true) :
            this(stream, if (recycleString) EcoFriendlyString() else RegularString())

    init {
        feedBuffer(1)
    }


    override fun skip(count: Int) {
        replenishBuffer(count)
        buffer.addPosition(count)
    }

    override fun read(count: Int): String {
        if (count < 0)
            throw IllegalArgumentException("Tried to read $count (negative numbers aren't allowed)")

        replenishBuffer(count)
        return readBuffer(count)
    }

    private fun replenishBuffer(amount: Int) {
        if (!isBufferBigEnough(amount) && buffer.getRequiredPosition() != NOT_REQUIRED)
            feedBuffer(getAccessibleBufferLength() - amount)
    }

    override fun hasNext(): Boolean
            = getAvailableCount() > 0

    override fun next(): Char
            = read(1)[0]

    override fun getLength(): Int
            = buffer.getLength()

    override fun getAvailableCount(): Int {
        val bufferAvailability = getAccessibleBufferLength()
        if (bufferAvailability < 1) {
            if (isStreamEmpty()) return 0
            else return 1
        }

        return bufferAvailability
    }

    override fun mark() {
        if (marks.isEmpty())
            buffer.markRequiredPosition()

        marks.addLast(buffer.getPosition())
    }

    override fun removeMark(): Boolean {
        if (marks.isEmpty())
            return false

        if (marks.size == 1)
            buffer.removeRequiredPosition()

        marks.removeLast()
        return true
    }

    override fun reset(): Boolean {
        if (marks.isEmpty())
            return false

        if (marks.size == 1)
            buffer.resetToRequiredPosition()

        buffer.setPosition(marks.removeLast())
        return true
    }

    private fun feedBuffer(requiredAmount: Int) {
        if (!quietFeedBuffer(requiredAmount))
            throw IllegalArgumentException("Tried to read more than available (reached end of stream)")
    }

    private fun quietFeedBuffer(requiredAmount: Int): Boolean {
        if (requiredAmount == 0)
            return true

        val amount = Math.max(requiredAmount, MIN_READ_AMOUNT)

        val array = CharArray(amount)
        val read = stream.read(array)

        stream.mark(read + 1)
        if (read < requiredAmount) {
            stream.reset()
            // Happens when stream has ended whilst reading to array
            return false
        } else {
            buffer.append(array, 0, read)
        }

        return true
    }

    private fun getAccessibleBufferLength(): Int
            = buffer.getAvailableAmount()

    private fun readBuffer(amount: Int): String
            = buffer.read(amount)

    private fun isBufferBigEnough(requiredLength: Int): Boolean
            = buffer.getAvailableAmount() > requiredLength

    private var streamEmptyForSure = false

    private fun isStreamEmpty(): Boolean {
        if (streamEmptyForSure)
            return true

        if (!quietFeedBuffer(1)) {
            streamEmptyForSure = true
            return true
        }

        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ReaderArgumentReader

        if (stream != other.stream) return false
        if (buffer != other.buffer) return false
        if (marks != other.marks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stream.hashCode()
        result = 31 * result + buffer.hashCode()
        result = 31 * result + marks.hashCode()
        return result
    }

    override fun toString(): String
            = "ReaderArgumentReader(stream=$stream, buffer=$buffer, marks=$marks, streamEmptyForSure=$streamEmptyForSure)"
}