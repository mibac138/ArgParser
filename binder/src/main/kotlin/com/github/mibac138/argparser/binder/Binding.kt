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

/**
 * Binding is a connector between [Parser]'s output, [SyntaxLinker]'s linking and [BoundMethod]'s invoke
 */
@Deprecated("Limit usage of this class. See: https://github.com/mibac138/ArgParser/issues/27")
interface Binding {

    /**
     * Calls the underlying method using output from [parser].
     */
    fun invoke(reader: ArgumentReader, parser: Parser): Any?
}

/**
 * Default [Binding] implementation.
 */
@Deprecated("Limit usage of this class. See: https://github.com/mibac138/ArgParser/issues/27")
open class BindingImpl constructor(
        protected val boundMethod: BoundMethod
                                  ) : Binding {
    private var syntax: SyntaxElement = boundMethod.syntax


    override fun invoke(reader: ArgumentReader, parser: Parser): Any? {
        val parsed = parser.parseReturnSyntaxLinkedMap(reader, syntax)

        return boundMethod.invoke(parsed.syntaxToValueMap)
    }
}