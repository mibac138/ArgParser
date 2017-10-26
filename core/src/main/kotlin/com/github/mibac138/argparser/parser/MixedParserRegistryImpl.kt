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

import com.github.mibac138.argparser.named.ArgumentMatcher
import com.github.mibac138.argparser.named.PatternArgumentMatcher
import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.parser.exception.ValueReassignmentException
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.skipChar
import com.github.mibac138.argparser.syntax.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * Can parse mixed syntax (i.e. with both named and unnamed elements)
 *
 * Supports [syntax elements][SyntaxElement] which are both [named][SyntaxElement.name] and [ordered][SyntaxElement.index].
 * Can throw [IllegalArgumentException] if a given syntax element is defined twice
 */
class MixedParserRegistryImpl : MixedParserRegistry {
    private val nameToParserMap: MutableMap<String, Parser> = HashMap()
    private val positionToParserMap: MutableMap<Int, Parser> = HashMap()
    override var matcher: ArgumentMatcher = PatternArgumentMatcher(Pattern.compile("--([a-zA-Z_0-9]+)(?:=|: ?)"))

    override val supportedTypes: Set<Class<*>>
        get() {
            val output = mutableSetOf<Class<*>>()
            nameToParserMap.values.map { it.supportedTypes }.forEach { output += it }
            positionToParserMap.values.map { it.supportedTypes }.forEach { output += it }

            return output
        }

    @Throws(IllegalArgumentException::class)
    override fun parse(input: ArgumentReader, syntax: SyntaxElement): SyntaxLinkedMap<String?, *> {
        val valuesMap = mutableMapOf<String?, Any?>()
        val syntaxMap = mutableMapOf<SyntaxElement, Any?>()

        val unnamed = mutableListOf<Any?>()
        val orderedSyntax = syntax.content.filterTo(ArrayList()) { it.index != null }
        orderedSyntax.sortBy { it.index }
        // LinkedList has O(1) add, remove and Iterator.next (the only used methods here)
        // TODO is it really better?
        val unprocessedSyntax = LinkedList(syntax.content)

        var index = 0
        for (i in 0 until syntax.size) {

            input.skipChar(' ')
            if (!input.hasNext()) break

            val matched = matcher.match(input)

            if (matched == null) {
                val element = orderedSyntax[index]
                val parsed = element.parseAtPosition(index, input)

                syntaxMap.putOrThrow(element, parsed, index)
                val name = element.name
                if (name != null)
                    valuesMap[name] = parsed
                else
                    unnamed += parsed

                unprocessedSyntax -= element
                index += 1
            } else {
                val element = syntax.findElementByName(matched.name)
                val parsed = element.parse(matched.value)

                syntaxMap.putOrThrow(element, parsed, matched.name)
                valuesMap[matched.name] = parsed
                unprocessedSyntax -= element

                if (element.index != null)
                    orderedSyntax.remove(element)
            }
        }

        for (element in unprocessedSyntax) {
            val name = element.name

            if (name == null) {
                val parsed = element.parseAtPosition(index, input)

                syntaxMap.putOrThrow(element, parsed, index)
                unnamed += parsed
                index += 1
            } else {
                val parsed = element.parse(input)

                syntaxMap.putOrThrow(element, parsed, name)
                valuesMap[name] = parsed
            }
        }

        if (!unnamed.isEmpty())
            valuesMap[null] = unnamed
        return SyntaxLinkedMap(valuesMap, syntaxMap)
    }

    private fun <K, V> MutableMap<K, V>.putOrThrow(key: K, value: V, index: Int) {
        if (put(key, value) != null)
            throw ValueReassignmentException.of(index)
    }

    private fun <K, V> MutableMap<K, V>.putOrThrow(key: K, value: V, name: String) {
        if (put(key, value) != null)
            throw ValueReassignmentException.of(name)
    }

    private fun SyntaxElement.parseAtPosition(index: Int, input: ArgumentReader): Any?
            = this.parseOrDefault(input, positionToParserMap[index] ?: this.parser)

    private fun SyntaxElement.parseOrDefault(input: ArgumentReader, parser: Parser?): Any?
            = when (parser) {
        null -> this.defaultValue
        else -> this.parse(input, parser)
    }

    private fun SyntaxElement.parse(input: ArgumentReader, parser: Parser = getParserForElement(this)): Any? {
        input.skipChar(' ')
        input.mark()
        val parsed: Any?
        try {
            parsed = parser.parse(input, this)
        } catch (e: Exception) {
            input.reset()
            throw e
        }

        input.removeMark()
        return parsed
    }

    override fun registerParser(parser: Parser, name: String) {
        nameToParserMap[name] = parser
    }

    override fun removeParser(name: String) {
        nameToParserMap.remove(name)
    }

    override fun registerParser(parser: Parser) {
        positionToParserMap[positionToParserMap.size] = parser
    }

    override fun registerParser(parser: Parser, position: Int) {
        positionToParserMap[position] = parser
    }

    override fun removeParser(position: Int) {
        positionToParserMap.remove(position)
    }

    // TODO why is this Unsupported? Implement?
    override fun removeParser(parser: Parser)
            = throw UnsupportedOperationException()

    override fun removeAllParsers() {
        nameToParserMap.clear()
        positionToParserMap.clear()
    }

    private fun getParserForElement(element: SyntaxElement)
            = element.parser ?: getParserForName(element.name ?: throw IllegalArgumentException())

    private fun getParserForName(name: String): Parser {
        val parser = nameToParserMap[name]
        if (parser != null)
            return parser

        throw IllegalArgumentException("Couldn't find parser for name '$name'")
    }

    private fun SyntaxElement.findElementByName(name: String): SyntaxElement {
        for (element in iterator()) {
            if (element.name == name)
                return element
        }

        throw /*IllegalArgument?*/Exception("Couldn't find syntax element with name '$name' inside $this")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MixedParserRegistryImpl

        if (nameToParserMap != other.nameToParserMap) return false
        if (positionToParserMap != other.positionToParserMap) return false
        if (matcher != other.matcher) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nameToParserMap.hashCode()
        result = 31 * result + positionToParserMap.hashCode()
        result = 31 * result + matcher.hashCode()
        return result
    }

    override fun toString(): String =
            "MixedParserRegistryImpl(nameToParserMap=$nameToParserMap, positionToParserMap=$positionToParserMap, matcher=$matcher)"
}