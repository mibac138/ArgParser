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

import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.reader.readUntilChar
import com.github.mibac138.argparser.syntax.SyntaxElement
import java.lang.reflect.Array

private fun parseArrayLiteral(input: ArgumentReader,
                              syntax: SyntaxElement,
                              elementParser: Parser,
                              separator: Char,
                              openingToClosingCharsMap: Map<Char, Char>): List<Any?> {
    if (!input.hasNext()) return emptyList()

    val start = input.next()
    val end = openingToClosingCharsMap[start]
            ?: throw IllegalArgumentException("Unrecognized array start character: \'$start\'")
    val content = input.readUntilChar(end)
    val elements = content.split(separator)


    return elements.map { elementParser.parse(it.asReader(), syntax) }
}

class ArrayParser(private val elementParser: Parser,
                  private val separator: Char = ',',
                  private val openingToClosingCharsMap: Map<Char, Char> = mapOf('[' to ']')) : Parser {
    override val supportedTypes: Set<Class<*>> = elementParser.supportedTypes.map {
        Array.newInstance(it, 0)::class.java // Class<T> -> Class<T[]>
    }.toSet()

    override fun parse(input: ArgumentReader, syntax: SyntaxElement)
            = parseArrayLiteral(input, syntax, elementParser, separator, openingToClosingCharsMap).toTypedArray()
}

class ListParser(private val elementParser: Parser,
                 private val separator: Char = ',',
                 private val openingToClosingCharsMap: Map<Char, Char> = mapOf('[' to ']')) : Parser {
    override val supportedTypes: Set<Class<*>> = setOf(List::class.java)

    override fun parse(input: ArgumentReader, syntax: SyntaxElement): List<Any?>
            = parseArrayLiteral(input, syntax, elementParser, separator, openingToClosingCharsMap)
}