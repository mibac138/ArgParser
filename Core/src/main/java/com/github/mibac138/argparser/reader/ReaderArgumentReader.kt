package com.github.mibac138.argparser.reader

import com.github.mibac138.argparser.reader.ArgumentString.Companion.NOT_REQUIRED
import java.io.Reader

class ReaderArgumentReader(private val stream: Reader, private var buffer: ArgumentString) : ArgumentReader {
    private val MIN_READ_AMOUNT = 20

    private val marks: IntQueue = IntQueue()

    constructor(stream: Reader, recycleString: Boolean = true) : this(stream, if (recycleString) EcoFriendlyString() else RegularString())

    init {
        feedBuffer(1)
    }


    override fun skip(count: Int) {
        replenishBuffer(count)
        buffer.modPosition(count)
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
            return if (isStreamEmpty()) 0 else 1
        }

        return bufferAvailability
    }

    override fun mark() {
        if (marks.size() == 0)
            buffer.markRequiredPosition()

        marks.add(buffer.getPosition())
    }

    override fun removeMark(): Boolean {
        if (marks.size() == 0)
            return false

        if (marks.size() == 1)
            buffer.removeRequiredPosition()

        marks.remove()
        return true
    }

    override fun reset(): Boolean {
        if (marks.size() == 0)
            return false

        if (marks.size() == 1)
            buffer.resetToRequiredPosition()

        buffer.setPosition(marks.remove())
        return true
    }

    private fun feedBuffer(requiredAmount: Int) {
        if (!quietFeedBuffer(requiredAmount))
            overRead()
    }

    private fun quietFeedBuffer(requiredAmount: Int): Boolean {
        if (requiredAmount == 0)
            return true

        val amount = Math.max(requiredAmount, MIN_READ_AMOUNT)

        val array = CharArray(amount)
        val read = stream.read(array)

        if (read < requiredAmount) {
            // Happens when stream has ended whilst reading to array
            return false
        } else {
            buffer.append(array, 0, read)
        }

        return true
    }

    private fun getAccessibleBufferLength(): Int
            = buffer.getAvailableAmount()

    private fun readBuffer(amount: Int): String {
        return buffer.read(amount)
    }

    private fun overRead() {
        throw IllegalArgumentException("Tried to read more than possible")
    }

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
}