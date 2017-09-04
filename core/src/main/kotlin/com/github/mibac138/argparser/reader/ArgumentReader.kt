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

@file:JvmName("ArgumentReaderUtil")

package com.github.mibac138.argparser.reader

import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset
import java.util.*
import java.util.regex.MatchResult
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Interface for reading arguments. Can be implemented to read
 * from various sources as just a plain string or files
 */
interface ArgumentReader {
    /**
     * Skips [count] characters.
     *
     * @throws IllegalArgumentException if underlying source isn't long enough
     * @param count amount of characters meant to be skipped
     */
    fun skip(count: Int)

    /**
     * Reads [count] next characters and returns them
     *
     * @return string of length [count]
     * @throws IllegalArgumentException if underlying source isn't long enough
     */
    fun read(count: Int): String

    /**
     * Returns `true` if the underlying source can read more characters, `false` otherwise
     *
     * @return whether you can read safely read more characters (at least 1)
     */
    fun hasNext(): Boolean

    /**
     * Returns next character from the underlying source
     *
     * @throws IllegalArgumentException if is empty
     */
    fun next(): Char

    /**
     * Returns the sum of already read text length and [getAvailableCount].
     * Might not return actual full text length until [hasNext]
     * returns false
     */
    fun getLength(): Int

    /**
     * Returns guaranteed amount of text you can safely read at the moment
     */
    fun getAvailableCount(): Int

    /**
     * Marks current position in case there's a possibility you will need to go back
     */
    fun mark()

    /**
     * Removes last mark (doesn't return to it)
     *
     * @return `true` if removed mark, `false` otherwise (usually means no marks left)
     */
    fun removeMark(): Boolean

    /**
     * Removes last mark _and_ returns to it
     *
     * @return `true` if removed mark and returned, `false` otherwise (usually means no marks left)
     */
    fun reset(): Boolean
}

/**
 * Reads the reader until it hits the given [targetChar] or [ArgumentReader.hasNext] returns false.
 * If possible, use [readUntilChar] with
 * action as argument instead of this function
 * as it reverts read text in case your
 * code threw an exception
 */
fun ArgumentReader.readUntilChar(targetChar: Char = ' '): String {
    val s = StringBuilder()

    while (hasNext()) {
        mark()
        val char = next()

        if (char == targetChar) {
            reset()

            return s.toString()
        } else {
            s.append(char)
            removeMark()
        }
    }

    return s.toString()
}

/**
 * Reads text until hits a space and passes it to [action]
 *
 * In case executing [action] throws an exception read text
 * is reverted (you will be able to read it again) and exception
 * is passed on
 */
fun <T> ArgumentReader.readUntilChar(targetChar: Char = ' ', action: (String) -> T): T {
    mark()
    try {
        val output = action(readUntilChar(targetChar))
        removeMark()

        return output
    } catch (e: Exception) {
        reset()
        throw e
    }
}

fun ArgumentReader.skipChar(targetChar: Char) {
    while (hasNext()) {
        mark()
        val char = next()
        if (char != targetChar) {
            reset()
            break
        }

        removeMark()
    }
}

fun ArgumentReader.skipUntilChar(targetChar: Char, inclusive: Boolean = true) {
    while (hasNext()) {
        mark()
        val char = next()
        if (char == targetChar) {
            if (inclusive) removeMark()
            else reset()

            break
        }

        removeMark()
    }
}

fun ArgumentReader.matchPattern(pattern: Pattern, readStep: Int = 30): MatchResult?
        = this.matchMatcher(pattern.matcher(""), readStep)


fun ArgumentReader.matchMatcher(matcher: Matcher, readStep: Int = 30): MatchResult? {
    mark()

    val read: String
    try {
        var currentlyRead: String = ""
        var available = getAvailableCount()

        do {
            currentlyRead += read(Math.min(readStep, available))
            matcher.reset(currentlyRead)
            available = getAvailableCount()
        } while (!matcher.hitEnd() && available != 0)

        read = currentlyRead
    } catch (e: Exception) {
        reset()
        throw e
    }

    // Revert if didn't find any matches or didn't find
    // at the start (probably took text that it shouldn't take)
    if (!matcher.find() || matcher.start() != 0) {
        reset()
        matcher.reset()
        return null
    }

    // Revert if matcher took too much text
    if (matcher.end() != read.length) {
        reset()
        skip(matcher.end())
    } else removeMark()

    return matcher.toMatchResult()
}

/**
 * Returns your object as a argument reader
 */
fun Any?.asReader(): ArgumentReader {
    if (this == null) return EmptyArgumentReader
    if (this is InputStream) return this.asReader()
    if (this is Reader) return this.asReader()

    return StringArgumentReader(this.toString())
}

fun InputStream?.asReader(charset: Charset = Charsets.UTF_8): ArgumentReader {
    if (this == null) return EmptyArgumentReader
    return InputStreamReader(this, charset).asReader()
}

fun Reader?.asReader(): ArgumentReader {
    if (this == null) return EmptyArgumentReader
    return ReaderArgumentReader(this)
}


internal fun LinkedList<Int>.shift/*Left*/(amount: Int) {
    for (i in this.indices) {
        this[i] = this[i] - amount
    }
}