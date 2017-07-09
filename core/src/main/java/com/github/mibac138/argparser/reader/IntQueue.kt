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

import java.util.*

internal class IntQueue {
    private val list = ArrayList<Int>(4)

    fun size(): Int
            = list.size

    fun add(int: Int): Boolean {
        return list.add(int)
    }

    fun remove(): Int {
        return list.removeAt(head)
    }

    fun poll(): Int?
            = if (list.isEmpty()) null else list.removeAt(head)

    fun shift(amount: Int) {
        for (i in list.indices) {
            list[i] = list[i] - amount
        }
    }

    private val head: Int
        get() = list.size - 1

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val intQueue = other as IntQueue?

        return list == intQueue!!.list
    }

    override fun hashCode(): Int {
        return list.hashCode()
    }

    override fun toString(): String {
        return list.toString()
    }
}
