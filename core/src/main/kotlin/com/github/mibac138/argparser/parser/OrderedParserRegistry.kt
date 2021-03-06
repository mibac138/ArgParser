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

/**
 * Must be `>= 0`
 */
typealias Position = Int

/**
 * Created by mibac138 on 07-05-2017.
 */
interface OrderedParserRegistry : ParserRegistry {
    /**
     *
     * @param position must be `>= 0`, otherwise a exception will be thrown (commonly `ArrayIndexOutOfBoundsException`)
     */
    fun registerParser(position: Position, parser: Parser)

    /**
     *
     * @param position must be `>= 0`, otherwise a exception will be thrown (commonly `ArrayIndexOutOfBoundsException`)
     */
    fun removeParser(position: Position)
}

fun <T : OrderedParserRegistry> T.withOrderedParsers(vararg parsers: Pair<Position, Parser>): T {
    for ((pos, parser) in parsers)
        registerParser(pos, parser)

    return this
}

fun <T : OrderedParserRegistry> T.withOrderedParsers(vararg parsers: Parser, offset: Position = 0): T {
    for (i in 0 until parsers.size)
        registerParser(i + offset, parsers[i])

    return this
}