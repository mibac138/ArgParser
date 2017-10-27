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

import com.github.mibac138.argparser.exception.ParserInvalidInputException
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 06-04-2017.
 */
class BooleanParser(private val caseSensitive: Boolean = false) : Parser {
    override val supportedTypes = setOf(Boolean::class.java, Boolean::class.javaObjectType)

    val values = mutableMapOf(
            "yes" to true,
            "y" to true,
            "true" to true,
            "t" to true,
            "1" to true,

            "no" to false,
            "n" to false,
            "false" to false,
            "f" to false,
            "0" to false
                                                          )

    override fun parse(input: ArgumentReader, syntax: SyntaxElement): Boolean? {
        return input.readUntilCharOrDefault(syntax, {
            values[it.lowerCaseIfApplicable()] ?: throw ParserInvalidInputException(
                    "Couldn't match text \"$it\". Valid values are: $values")
        })
    }

    private fun String.lowerCaseIfApplicable(): String =
            if (caseSensitive) this else this.toLowerCase()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as BooleanParser

        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int
            = values.hashCode()

    override fun toString(): String
            = "BooleanParser(values=$values)"
}