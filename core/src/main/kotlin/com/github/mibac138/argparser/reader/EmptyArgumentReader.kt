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
 * Represents a empty [ArgumentReader].
 */
object EmptyArgumentReader : ArgumentReader {
    override fun skip(count: Int) = throwIf<Unit>(count > 0) ?: Unit

    override fun read(count: Int) = throwIf(count > 0) ?: ""

    override fun hasNext(): Boolean
            = false

    override fun next(): Char = throwIf(true)!!

    override fun getLength(): Int
            = 0

    override fun getAvailableCount(): Int
            = 0

    override fun mark() {
    }

    override fun removeMark(): Boolean
            = false

    override fun reset(): Boolean
            = false

    @Suppress("NOTHING_TO_INLINE")
    private inline fun <T> throwIf(cond: Boolean): T? {
        if (cond)
            throw IllegalArgumentException("I'm EmptyArgumentReader. That means that I have no " +
                    "content (length and available count always equals 0, skip and read throw if count > 0)")

        return null
    }
}