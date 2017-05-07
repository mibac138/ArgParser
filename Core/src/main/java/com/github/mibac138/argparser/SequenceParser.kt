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

package com.github.mibac138.argparser

import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.readUntilChar
import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 05-04-2017.
 */
class SequenceParser : Parser {
    override fun getSupportedTypes(): Set<Class<*>> = setOf(String::class.java)

    private val quotationMarks: MutableSet<Char> = mutableSetOf('\'', '"')

    fun addQuotationMark(mark: Char) {
        quotationMarks.add(mark)
    }

    fun removeQuotationMark(mark: Char) {
        quotationMarks.remove(mark)
    }

    /**
     * Returns the text until a space *or* if next character is a quotation marks text until the next occurence same mark
     *
     * For example:
     * - 'abcd efgh' returns 'abcd'
     * - '"abcd efgh"' returns '"abcd efgh"'
     * - '"abcd ef"gh' returns '"abcd ef"'
     */
    override fun parse(input: ArgumentReader, syntax: SyntaxElement<*>): String {
        val first = input.next()
        if (quotationMarks.contains(first))
            return first + input.readUntilChar(first) + input.next() // return quotation marks too

        if (first.isWhitespace())
            return input.readUntilChar(' ') { it }
        else return input.readUntilChar(' ') { first + it }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as SequenceParser

        if (quotationMarks != other.quotationMarks) return false

        return true
    }

    override fun hashCode(): Int {
        return quotationMarks.hashCode()
    }

    override fun toString(): String {
        return "SequenceParser(quotationMarks=$quotationMarks)"
    }
}