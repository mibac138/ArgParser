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

import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.skipChar
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.iterator
import com.github.mibac138.argparser.syntax.parser

/**
 * Created by mibac138 on 05-04-2017.
 */
class SimpleParserRegistry : OrderedParserRegistry {
    private val classToParserMap = mutableMapOf<Class<*>, Parser>()
    private val positionToParserMap = mutableMapOf<Int, Parser>()
    override val supportedTypes: Set<Class<*>> = classToParserMap.keys

    init {
        registerParser(SequenceParser())
        registerParser(BooleanParser())
        registerParser(IntParser())
    }


    override fun registerParser(parser: Parser, position: Int) {
        positionToParserMap[position] = parser
    }

    override fun removeParser(position: Int) {
        positionToParserMap.remove(position)
    }

    override fun registerParser(parser: Parser) {
        parser.supportedTypes.forEach { clazz -> classToParserMap.put(clazz, parser) }
    }

    override fun removeParser(parser: Parser) {
        for (clazz in parser.supportedTypes)
            classToParserMap.remove(clazz, parser)
    }

    override fun removeAllParsers() {
        classToParserMap.clear()
        positionToParserMap.clear()
    }

    override fun parse(input: ArgumentReader, syntax: SyntaxElement): SyntaxLinkedMap<Int, *>
            = parse(input, syntax.iterator())

    private fun parse(input: ArgumentReader,
                      iterator: Iterator<SyntaxElement>)
            : SyntaxLinkedMap<Int, *> {
        val syntaxMap = mutableMapOf<SyntaxElement, Any?>()
        val valuesMap = mutableMapOf<Int, Any?>()

        for ((i, element) in iterator.withIndex()) {
            val parser = getParserForElement(element, i)
            val parsed = parseElement(input, element, parser)

            valuesMap[i] = parsed
            syntaxMap[element] = parsed
        }

        return SyntaxLinkedMap(valuesMap, syntaxMap)
    }

    private fun parseElement(reader: ArgumentReader, element: SyntaxElement, parser: Parser): Any? {
        reader.skipChar(' ')
        reader.mark()

        val parsed: Any?
        try {
            parsed = parser.parse(reader, element)
        } catch (e: Exception) {
            reader.reset()
            throw e
        }

        reader.removeMark()
        return parsed
    }

    private fun getParserForElement(element: SyntaxElement, position: Int): Parser
            = element.parser ?: positionToParserMap[position] ?: getParserForType(element.outputType)

    private fun getParserForType(type: Class<*>): Parser {
        val parser = classToParserMap[type]
        if (parser != null)
            return parser

        throw IllegalArgumentException("Tried to parse ${type.name} but no eligible parsers were registered")
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleParserRegistry

        if (classToParserMap != other.classToParserMap) return false
        if (positionToParserMap != other.positionToParserMap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = classToParserMap.hashCode()
        result = 31 * result + positionToParserMap.hashCode()
        return result
    }

    override fun toString(): String = "SimpleParserRegistry(classToParserMap=$classToParserMap)"
}