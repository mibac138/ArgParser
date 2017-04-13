package com.github.mibac138.argparser.reader

/**
 * Created by mibac138 on 08-04-2017.
 */
interface ArgumentString {
    companion object {
        const val NOT_REQUIRED = -1
    }

    fun recycle()

    fun append(array: CharArray, from: Int, to: Int)
    fun append(char: Char)

    fun getLength(): Int
    fun getPosition(): Int
    fun getAvailableAmount(): Int

    fun markRequiredPosition()
    fun removeRequiredPosition()
    fun resetToRequiredPosition()
    fun getRequiredPosition(): Int

    fun setPosition(pos: Int)
    fun modPosition(mod: Int)

    fun read(amount: Int): String
}