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

import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.iterator
import java.util.*

/**
 * Created by mibac138 on 23-06-2017.
 */
class SyntaxLinkerImpl(syntax: SyntaxElement) : ReusableSyntaxLinker {
    private val argsMap: MutableMap<String, IndexedValue<SyntaxElement>> = HashMap()
    private val noNameArgsMap: MutableMap<Class<*>, MutableMap<Int, SyntaxElement>> = HashMap()

    init {
        recreate(syntax)
    }

    override fun recreate(syntax: SyntaxElement) {
        argsMap.clear()
        noNameArgsMap.clear()

        for ((i, element) in syntax.iterator().withIndex()) {
            val name = element.name

            if (name != null) argsMap[name] = IndexedValue(i, element)
            else noNameArgsMap.getOrPut(element.outputType, { mutableMapOf() })[i] = element
        }
    }

    // region Linking

    override fun link(input: Any): Map<SyntaxElement, Any?> =
            linkMap(input.entryIterator(false))


    private fun linkMap(iterator: Iterator<IndexedValue<Map.Entry<String?, *>>>,
                        map: MutableMap<SyntaxElement, Any?> = IdentityHashMap()
                       ): Map<SyntaxElement, Any?> {

        for ((i, entry) in iterator) {
            val (key, value) = entry

            val element = resolveArg(key, value, i) ?:
                    if (key.isNullOrEmpty() && value != null) {
                        linkMap(value.entryIterator(true), map)
                        continue
                    } else if (key != null || value != null) throw IllegalArgumentException(
                            "Parser returned result [key='$key', value='$value'] which I can't map " +
                                    "to the syntax")
                    else continue

            if (map.containsKey(element))
                throw IllegalStateException("Can't pass two values to one argument")

            map[element] = value
        }

        return map
    }

    //endregion

    // region Resolving arguments

    private fun resolveArg(key: String?, value: Any?, index: Int): SyntaxElement? {
        if (key != null) return getArgByName(key)
        return getArgByTypeAndIndex(value, index)
    }

    private fun getArgByName(name: String): SyntaxElement?
            = argsMap[name]?.value


    private fun getArgByTypeAndIndex(instance: Any?, index: Int): SyntaxElement? {
        for ((currentIndex, value) in argsMap.values)
            if (currentIndex == index && instance?.let { value.outputType.isInstance(it) } != false)
                return value

        if (instance != null)
            for ((key, value) in noNameArgsMap)
                if (key.isInstance(instance))
                    return value[index]

        return null
    }

    // endregion

    // region Iteration

    @Suppress("UNCHECKED_CAST")
    private fun Any.entryIterator(nested: Boolean): Iterator<IndexedValue<Map.Entry<String?, *>>>
            = when (this) {
        is Collection<*> -> this.iterator().asEntryBasedIterator().withIndex()
        is Array<*> -> this.iterator().asEntryBasedIterator().withIndex()
        is Map<*, *> -> (this.entries.iterator() as Iterator<Map.Entry<String?, *>>).withIndex()
        else -> if (nested)
        // if the object is nested and has got here it means the linker couldn't resolve it
            throw UnsupportedOperationException(
                    "The given input type [$this (${this::class})] isn't supported")
        else
            listOf(this).iterator().asEntryBasedIterator().withIndex()
    }

    private fun <T> Iterator<T>.asEntryBasedIterator(): Iterator<Map.Entry<String?, *>>
            = ListAsEntryIterator(this)

    private class ListAsEntryIterator<out T>(private val iterator: Iterator<T>) : Iterator<Map.Entry<String?, T>> {
        override fun hasNext() = iterator.hasNext()

        override fun next() = ValueOnlyEntry(iterator.next())

        private class ValueOnlyEntry<out T>(override val value: T) : Map.Entry<String?, T> {
            override val key
                get() = null
        }
    }

    // endregion
}
