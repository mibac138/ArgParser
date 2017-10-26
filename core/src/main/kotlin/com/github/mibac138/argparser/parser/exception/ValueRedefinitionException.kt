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

sealed class ValueReassignmentException(s: String,
                                        val element: SyntaxElement,
                                        val originalValue: Any?,
                                        val reassignedValue: Any?) : IllegalArgumentException(s) {

    companion object {
        @JvmStatic
        fun of(element: SyntaxElement, originalValue: Any?, reassignedValue: Any?) =
                when {
                    element.name != null -> Named(element, originalValue, reassignedValue)
                    element.index != null -> Indexed(element, originalValue, reassignedValue)
                    else -> throw IllegalArgumentException()
                }
    }

    class Named(element: SyntaxElement, originalValue: Any?, reassignedValue: Any?) : ValueReassignmentException(
            "Tried to reassign element's (${element::class.java.name}) value (name: \"${element.name}\") from $originalValue to $reassignedValue",
            element, originalValue, reassignedValue)

    class Indexed(element: SyntaxElement, originalValue: Any?, reassignedValue: Any?) : ValueReassignmentException(
            "Tried to reassign element's (${element::class.java.name}) value (position: ${element.index}) from $originalValue to $reassignedValue",
            element, originalValue, reassignedValue)
}