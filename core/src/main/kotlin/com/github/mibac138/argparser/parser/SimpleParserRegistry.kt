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

import com.github.mibac138.argparser.exception.ParserException
import com.github.mibac138.argparser.exception.ParserInternalException
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.skipChar
import com.github.mibac138.argparser.syntax.SyntaxContainer
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.iterator
import com.github.mibac138.argparser.syntax.parser
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by mibac138 on 05-04-2017.
 */
class SimpleParserRegistry : ParserRegistry {
    private val classToParserMap: MutableMap<Class<*>, Parser> = HashMap()

    init {
        registerParser(SequenceParser())
        registerParser(BooleanParser())
        registerParser(IntParser())
    }


    override fun getSupportedTypes(): Set<Class<*>> {
        return classToParserMap.keys
    }

    override fun registerParser(parser: Parser) {
        parser.getSupportedTypes().forEach { clazz -> classToParserMap.put(clazz, parser) }
    }

    override fun removeParser(parser: Parser) {
        for (clazz in parser.getSupportedTypes()) {
            val owner = classToParserMap[clazz]

            if (owner == null || parser !== owner) {
                continue
            }

            classToParserMap.remove(clazz)
        }
    }

    override fun removeAllParsers() {
        classToParserMap.clear()
    }

    override fun parse(input: ArgumentReader, syntax: SyntaxElement<*>): List<*> {
        if (!input.hasNext())
            return emptyList<Any>()

        val size: Int
        if (syntax is SyntaxContainer) {
            if (syntax.content.isEmpty())
                return emptyList<Any>()
            else
                size = syntax.content.size
        } else size = 1

        return parseSyntax(input, size, syntax.iterator())
    }

    private fun parseSyntax(input: ArgumentReader, size: Int, iterator: Iterator<SyntaxElement<*>>): List<*> {
        val result = ArrayList<Any?>(size)

        for (element in iterator) {
            val parser = getParserForElement(element)

            result += parseElement(input, element, parser)
        }

        return result
    }

    private fun parseElement(reader: ArgumentReader, element: SyntaxElement<*>, parser: Parser): Any? {
        reader.skipChar(' ')
        reader.mark()
        var parsed: Any?
        try {
            parsed = parser.parse(reader, element)
        } catch (e: Exception) {
            parsed = wrapException(e)
        }

        reader.removeMark()

        return parsed
    }

    private fun getParserForElement(element: SyntaxElement<*>): Parser {
        return element.parser ?: getParserForType(element.outputType)
    }

    private fun getParserForType(type: Class<*>): Parser {
        val parser = classToParserMap[type]
        if (parser != null)
            return parser

        throw IllegalArgumentException("Tried to parse ${type.name} but no eligible parsers were registered")
    }

    private fun wrapException(e: Exception?): Exception {
        if (e is ParserException) {
            return e
        } else {
            return ParserInternalException(e)
        }
    }
}