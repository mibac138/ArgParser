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
 * Default [Binding] implementation.
 */
open class BindingImpl constructor(
        protected val boundMethod: BoundMethod
//        private val linker: SyntaxLinker = SyntaxLinkerImpl(boundMethod.syntax)
                                  ) : Binding {
    private var syntax: SyntaxElement = boundMethod.syntax


    override fun invoke(reader: ArgumentReader, parser: Parser): Any? {
        if (boundMethod.syntax != syntax)
            updateSyntax()

        val parsed = parser.parseReturnSyntaxLinkedMap(reader, syntax)

        return boundMethod.invoke(parsed.syntaxToValueMap)
    }

    /**
     * Recreates internal syntax representation
     */
    fun updateSyntax() {
//        if (linker !is ReusableSyntaxLinker)
//            throw IllegalStateException("Can't update syntax if the SyntaxLinker isn't reusable")
//
//        syntax = boundMethod.syntax
//        linker.recreate(syntax)
    }
}