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

import com.github.mibac138.argparser.exception.ParserException
import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.parser.Parser
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.getSize
import com.github.mibac138.argparser.syntax.iterator
import java.util.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

class BindingImpl internal constructor(private val boundMethod: BoundMethod) : Binding {
    private val exceptions: MutableList<Exception> = ArrayList()
    var syntax: SyntaxElement<*> = boundMethod.syntax

    private val argsMap: MutableMap<String, IndexedValue<SyntaxElement<*>>> = HashMap()
    private val noNameArgsMap: MutableMap<Class<*>, IndexedValue<SyntaxElement<*>>> = HashMap()

    init {
        syntaxChanged()
    }

    override fun getExceptions(): List<Exception>
            = exceptions

    override fun invoke(reader: ArgumentReader, parser: Parser): Any? {
        exceptions.clear()
        val args = parser.parse(reader, syntax)

        val mapped: Array<*>

        if (args is List<*>)
            mapped = transform(args.iterator().asEntry())
        else if (args is Array<*>)
            mapped = transform(args.iterator().asEntry())
        else if (args is Map<*, *>)
            mapped = transform(args.entries.iterator() as Iterator<Map.Entry<String?, *>>)
        else if (args is Exception)
            if (args is ParserException)
                throw args
            else
                throw ParserException(args)
        else
            mapped = arrayOf(args)

        return boundMethod.invoke(mapped)
    }

    fun syntaxChanged() {
        syntax = boundMethod.syntax
        syntax.iterator().withIndex().forEach { (i, element) ->
            val name = element.name

            if (name != null) argsMap[name] = IndexedValue(i, element)
            else noNameArgsMap[element.outputType] = IndexedValue(i, element)
        }
    }

    private fun transform(iterator: Iterator<Map.Entry<String?, *>>): Array<*> {
        val array = Array<Any?>(syntax.getSize(), { null })
        for (entry in iterator) {
            if (entry.value is Exception) {
                exceptions.add(entry.value as Exception)
            } else {
                val element = resolveArg(entry) ?: throw IllegalArgumentException("Parser returned " +
                        "result which I can't map to the syntax: $entry")

                if (array[element.index] != null)
                    throw IllegalStateException("Can't pass two values to one argument")

                array[element.index] = entry.value
            }
        }

        return array
    }

    private fun resolveArg(entry: Map.Entry<String?, *>): IndexedValue<SyntaxElement<*>>? {
        val key = entry.key
        if (key == null)
            return getArgByType(entry.value!!.javaClass)
        else
            return getArgByName(key)
    }

    private fun getArgByType(type: Class<*>): IndexedValue<SyntaxElement<*>>? {
        return argsMap.values.firstOrNull {
            type.isAssignableFrom(it.value.outputType)
        } ?: noNameArgsMap.entries.firstOrNull {
            type.isAssignableFrom(it.key)
        }?.value
    }

    private fun getArgByName(name: String): IndexedValue<SyntaxElement<*>>? {
        return argsMap[name]
    }

    private fun <T> Iterator<T>.asEntry(): Iterator<Map.Entry<String?, *>> {
        return ListAsEntryIterator(this)
    }

    private class ListAsEntryIterator<out T>(private val iterator: Iterator<T>) : Iterator<Map.Entry<String?, T>> {
        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): Map.Entry<String?, T> {
            val value = iterator.next()
            return object : Map.Entry<String?, T> {
                override val key: String?
                    get() = null

                override val value: T
                    get() = value

                override fun toString(): String {
                    return value.toString()
                }
            }
        }
    }
}