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

    private fun eligibleForRecycling(): Int {
        when (minPos) {
            NOT_REQUIRED -> return pos
            else -> return Math.min(minPos, pos)
        }
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

    override fun getPosition(): Int {
        return super.getPosition() + offset
    }

    override fun getRequiredPosition(): Int {
        if (minPos == NOT_REQUIRED)
            return NOT_REQUIRED
        return minPos + offset
    }
}