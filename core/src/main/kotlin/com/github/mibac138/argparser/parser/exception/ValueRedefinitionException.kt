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

package com.github.mibac138.argparser.parser.exception

import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.index

/**
 * Thrown to indicate that a value linked to a [element][SyntaxElement] has been reassigned
 */
sealed class ValueReassignmentException(s: String,
                                        /**
                                         * The element of which the value has been reassigned
                                         */
                                        val element: SyntaxElement,
                                        /**
                                         * The original value of the [element]
                                         */
                                        val originalValue: Any?,
                                        /**
                                         * The reassigned value of the [element]
                                         */
                                        val reassignedValue: Any?) : IllegalArgumentException(s) {

    companion object {
        /**
         * Factory method to create [ValueReassignmentException].
         * Creates a [named exception][ValueReassignmentException.Named] if the [element] is [named][SyntaxElement.name] or
         * a [indexed exception][ValueReassignmentException.Indexed]  if the [element] is [ordered][SyntaxElement.index]
         *
         * If the [element] is both [named][SyntaxElement.name] and [ordered][SyntaxElement.index] then
         * a [named exception][ValueReassignmentException.Named] is created
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun of(element: SyntaxElement, originalValue: Any?, reassignedValue: Any?) =
                when {
                    element.name != null -> Named(element, originalValue, reassignedValue)
                    element.index != null -> Indexed(element, originalValue, reassignedValue)
                    else -> throw IllegalArgumentException()
                }
    }

    /**
     * Thrown to indicate that a value linked to a *[named][SyntaxElement.name]* [element][SyntaxElement] has
     * been reassigned
     */
    class Named(element: SyntaxElement, originalValue: Any?, reassignedValue: Any?) : ValueReassignmentException(
            "Tried to reassign element's (${element::class.java.name}) value (name: \"${element.name}\") from $originalValue to $reassignedValue",
            element, originalValue, reassignedValue)

    /**
     * Thrown to indicate that a value linked to a *[ordered][SyntaxElement.index]* [element][SyntaxElement] has
     * been reassigned
     */
    class Indexed(element: SyntaxElement, originalValue: Any?, reassignedValue: Any?) : ValueReassignmentException(
            "Tried to reassign element's (${element::class.java.name}) value (position: ${element.index}) from $originalValue to $reassignedValue",
            element, originalValue, reassignedValue)
}