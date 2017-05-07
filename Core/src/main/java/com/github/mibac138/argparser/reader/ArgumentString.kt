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
    fun addPosition(mod: Int)

    fun read(amount: Int): String
}