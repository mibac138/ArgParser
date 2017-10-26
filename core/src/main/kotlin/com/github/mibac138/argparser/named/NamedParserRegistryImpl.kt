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

package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.parser.Parser
import com.github.mibac138.argparser.parser.SyntaxLinkedMap
import com.github.mibac138.argparser.parser.exception.ValueReassignmentException
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.skipChar
import com.github.mibac138.argparser.syntax.*
import java.util.*
import java.util.regex.Pattern

/**
 * Supports named syntax only. The name format can be changed by providing a [ArgumentMatcher]
 */
class NamedParserRegistryImpl : NamedParserRegistry {
    private val typeToParserMap: MutableMap<Class<*>, Parser> = HashMap()
    private val nameToParserMap: MutableMap<String, Parser> = HashMap()

    override var matcher: ArgumentMatcher = PatternArgumentMatcher(Pattern.compile("--([a-zA-Z][a-zA-Z\\d]*)(?:=|: ?)"))

    override fun parse(input: ArgumentReader, syntax: SyntaxElement): SyntaxLinkedMap<String, *> {
        if (syntax.get(NameComponent::class.java) == null && syntax !is SyntaxContainer)
            throw IllegalArgumentException(
                    "I only accept SyntaxElements with NameComponent (and SyntaxContainers with" +
                            "all elements having NameComponent)")

        val syntaxMap = mutableMapOf<SyntaxElement, Any?>()
        val valuesMap = mutableMapOf<String, Any?>()
        // LinkedList has O(1) add, remove and Iterator.next (the only used methods here)
        // TODO is it really better?
        val unprocessedSyntax = LinkedList(syntax.content)

        for (i in 0 until syntax.size) {
            if (!input.hasNext()) break

            input.skipChar(' ')
            val matched = matcher.match(input)
                    ?: throw IllegalArgumentException("Matcher didn't match input")

            val name = matched.name

            val element = syntax.findElementByName(name)
            val parser = getParserForElement(element)
            val parsed = parseElement(matched.value, element, parser)

            valuesMap.putOrThrow(element, name, parsed)
            syntaxMap[element] = parsed
            unprocessedSyntax -= element
        }

        for (element in unprocessedSyntax) {
            // Every element here *must* have a name
            val name = element.name
                    ?: throw IllegalArgumentException(
                    "SyntaxElement $element is unnamed. I accept *only* named syntax elements")

            val parser = getParserForElement(element)
            val parsed = parseElement(input, element, parser)

            valuesMap[name] = parsed
            syntaxMap[element] = parsed
        }

        return SyntaxLinkedMap(valuesMap, syntaxMap)
    }

    private fun <V> MutableMap<String, V>.putOrThrow(element: SyntaxElement, key: String, value: V) {
        val previous = put(key, value)
        if (previous != null)
            throw ValueReassignmentException.of(element, previous, value)
    }

    private fun parseElement(input: ArgumentReader, element: SyntaxElement, parser: Parser): Any? {
        input.skipChar(' ')
        input.mark()
        val parsed: Any?
        try {
            parsed = parser.parse(input, element)
        } catch (e: Exception) {
            input.reset()
            throw e
        }

        input.removeMark()
        return parsed
    }

    override val supportedTypes: Set<Class<*>> = typeToParserMap.keys

    override fun registerParser(parser: Parser) {
        for (type in parser.supportedTypes)
            typeToParserMap[type] = parser
    }

    override fun registerParser(parser: Parser, name: String) {
        nameToParserMap[name] = parser

        for (type in parser.supportedTypes)
            typeToParserMap[type] = parser
    }

    override fun removeParser(parser: Parser) {
        for (type in parser.supportedTypes) {
            typeToParserMap.removeIfMatches(type, parser)
        }

        val it: MutableIterator<Map.Entry<String, Parser>> = nameToParserMap.entries.iterator()
        for (entry in it)
            if (entry.value == parser)
                it.remove()
    }

    private fun <K, V> MutableMap<K, V>.removeIfMatches(key: K, value: V) {
        if (this[key] == value)
            remove(key)
    }

    override fun removeAllParsers() {
        typeToParserMap.clear()
        nameToParserMap.clear()
    }

    override fun removeParser(name: String) {
        nameToParserMap.remove(name)
    }

    fun associateParserWithName(type: Class<*>, name: String) {
        val parser = getParserForType(type)
        nameToParserMap[name] = parser
    }

    private fun getParserForElement(element: SyntaxElement): Parser
            = element.parser ?:
            getParserForName(element.name ?: throw IllegalArgumentException())

    private fun getParserForName(name: String): Parser {
        val parser = nameToParserMap[name]
        if (parser != null)
            return parser

        throw IllegalArgumentException("Couldn't find parser for name '$name'")
    }

    private fun getParserForType(type: Class<*>): Parser {
        val parser = typeToParserMap[type]
        if (parser != null)
            return parser

        throw IllegalArgumentException("Couldn't find parser supporting type '$type'")
    }


    private fun SyntaxElement.findElementByName(name: String): SyntaxElement {
        for (element in iterator()) {
            if (element.name == name)
                return element
        }

        throw /*IllegalArgument?*/Exception("Couldn't find syntax element with name '$name'")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as NamedParserRegistryImpl

        if (typeToParserMap != other.typeToParserMap) return false
        if (nameToParserMap != other.nameToParserMap) return false
        if (matcher != other.matcher) return false

        return true
    }

    override fun hashCode(): Int {
        var result = typeToParserMap.hashCode()
        result = 31 * result + nameToParserMap.hashCode()
        result = 31 * result + matcher.hashCode()
        return result
    }

    override fun toString(): String =
            "NamedParserRegistry(typeToParserMap=$typeToParserMap, nameToParserMap=$nameToParserMap, matcher=$matcher)"
}