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

@file:JvmName("ParserUtil")

package com.github.mibac138.argparser.parser

import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.readUntilChar
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.defaultValue

/**
 *
 */
interface Parser {
    fun getSupportedTypes(): Set<Class<*>>
    fun parse(input: ArgumentReader, syntax: SyntaxElement): Any?
}

fun <T> parseOrDefault(syntax: SyntaxElement, action: () -> T): T? {
    try {
        return action()
    } catch (e: Exception) {
        @Suppress("UNCHECKED_CAST")
        if (!syntax.required && syntax.defaultValue != null)
            return syntax.defaultValue as T?
        else
            throw e
    }
}


fun <T> ArgumentReader.readUntilCharOrDefault(syntax: SyntaxElement, action: (String) -> T, char: Char = ' '): T? {
    mark()
    try {
        val input = readUntilChar(char)
        val output = action(input)
        removeMark()

        return output
    } catch (e: Exception) {
        if (!syntax.required) {
            removeMark()

            @Suppress("UNCHECKED_CAST")
            return syntax.defaultValue as T?
        }

        reset()
        throw e
    }
}