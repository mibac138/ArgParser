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

package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.parser.Parser
import com.github.mibac138.argparser.parser.parseReturnSyntaxLinkedMap
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.syntax.SyntaxElement
import kotlin.reflect.KCallable

/**
 * Represents a bound method. Can be easily invoked using [invoke]
 */
interface BoundMethod {
    /**
     * @param parameters A properly ordered array of parameters used by this method.
     * @return the same thing the underlying method returned or `null`
     */
    fun invoke(parameters: Map<SyntaxElement, Any?>): Any?

    /**
     * Syntax this method uses.
     */
    val syntax: SyntaxElement

    /**
     * The underlying method
     */
    val method: KCallable<*>
}

fun BoundMethod.invoke(reader: ArgumentReader, parser: Parser): Any?
        = this.invoke(parser.parseReturnSyntaxLinkedMap(reader, this.syntax).syntaxToValueMap)