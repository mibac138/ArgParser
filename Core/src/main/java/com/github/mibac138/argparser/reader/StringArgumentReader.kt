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
}