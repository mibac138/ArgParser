package com.github.mibac138.argparser.reader

import com.github.mibac138.argparser.reader.ArgumentString.Companion.NOT_REQUIRED

open class RegularString : ArgumentString {
    protected var content: String = ""
    protected var pos: Int = 0
    protected var minPos: Int = NOT_REQUIRED

    override fun getPosition(): Int
            = pos

    override fun modPosition(mod: Int)
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
        content += string
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
}