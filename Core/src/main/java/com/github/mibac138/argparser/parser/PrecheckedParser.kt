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

package com.github.mibac138.argparser.parser

import com.github.mibac138.argparser.reader.skipChar

/**
 * Created by mibac138 on 05-04-2017.
 */
abstract class PrecheckedParser<out T : Any> : Parser {

    @JvmField
    protected val NO_LENGTH: Int = -1

    /**
     * Returned value means how much more to read of input (*not* in total)
     */
    protected open fun getMatchingLength(alreadyRead: Int): Int = if (alreadyRead == NO_LENGTH) 25 else 25

    protected abstract fun getPattern(): java.util.regex.Pattern

    protected abstract fun parse(matcher: java.util.regex.Matcher, element: com.github.mibac138.argparser.syntax.SyntaxElement<*>): T

    override fun parse(input: com.github.mibac138.argparser.reader.ArgumentReader, syntax: com.github.mibac138.argparser.syntax.SyntaxElement<*>): T {
        input.mark()

        val output: Pair<java.util.regex.Matcher, String>
        try {
            input.skipChar(' ')
            output = matchInput(input)
        } catch(e: Exception) {
            input.reset()
            throw e
        }

        if (!output.first.find()) {
            input.reset()
            if (syntax.required)
                throw com.github.mibac138.argparser.exception.ParserInvalidInputException("Pattern [${output.first.pattern()}] didn't match the input [${output.second}]")

            @Suppress("UNCHECKED_CAST")
            return syntax.defaultValue!! as T
        }

        // Revert if matcher took too much text
        if (output.first.end() != output.second.length) {
            input.reset()
            input.skip(output.first.end())
        }

        try {
            val parsed = parse(output.first, syntax)
            input.removeMark()
            return parsed
        } catch (e: Exception) {
            input.reset()
            throw e
        }
    }

    private fun matchInput(input: com.github.mibac138.argparser.reader.ArgumentReader): Pair<java.util.regex.Matcher, String> {
        if (input.getAvailableCount() == 0)
            throw com.github.mibac138.argparser.exception.ParserInternalException()

        var total = NO_LENGTH
        var read: String = ""
        val matcher: java.util.regex.Matcher = getPattern().matcher("")
        var available = input.getAvailableCount()

        do {
            val length = getMatchingLength(total)
            total += length

            read += input.read(Math.min(length, available))
            matcher.reset(read)
            available = input.getAvailableCount()
        } while (!matcher.hitEnd() && available != 0)

        return Pair(matcher, read)
    }

    override fun toString(): String {
        return "PrecheckedParser()"
    }
}
