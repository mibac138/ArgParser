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

import com.github.mibac138.argparser.syntax.SyntaxElement

interface SyntaxLinkedMap<K, out V> : Map<K, V> {
    val syntaxToValueMap: Map<SyntaxElement, V>
}

// Used to remove ambiguity when using generics
// (this is considered better than explicit casting)
val <K, V> SyntaxLinkedMap<K, V>.keyToValueMap: Map<K, V>
    get() = this

fun <K, V> SyntaxLinkedMap(keyToValueMap: Map<K, V>, syntaxToValueMap: Map<SyntaxElement, V>): SyntaxLinkedMap<K, V>
        = SyntaxLinkedMapImpl(keyToValueMap, syntaxToValueMap)

private data class SyntaxLinkedMapImpl<K, out V>(
        private val keyToValueMap: Map<K, V>,
        override val syntaxToValueMap: Map<SyntaxElement, V>
                                                ) : SyntaxLinkedMap<K, V>, Map<K, V> by keyToValueMap

