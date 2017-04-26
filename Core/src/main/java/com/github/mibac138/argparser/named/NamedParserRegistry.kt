package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.Parser
import com.github.mibac138.argparser.ParserRegistry
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.skipSpaces
import com.github.mibac138.argparser.syntax.CustomSyntaxElement
import com.github.mibac138.argparser.syntax.SyntaxContainer
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.iterator
import javax.rmi.CORBA.Util.wrapException

/**
 * Created by mibac138 on 09-04-2017.
 */
class NamedParserRegistry : ParserRegistry {
    private val typeToParserMap: MutableMap<Class<*>, Parser> = HashMap()
    private val nameToParserMap: MutableMap<String, Parser> = HashMap()

    override fun parse(input: ArgumentReader, syntax: SyntaxElement<*>): Map<String, *> {
        if (syntax !is NamedSyntaxElement<*> && syntax !is SyntaxContainer)
            throw IllegalArgumentException("I only accept NamedSyntaxElements or SyntaxContainers containing NSEs")

        return parseSyntax(input, syntax.iterator())
    }

    private fun parseSyntax(input: ArgumentReader, iterator: Iterator<SyntaxElement<*>>): Map<String, *> {
        val map = HashMap<String, Any>()

        for (element in iterator) {
            if (element !is NamedSyntaxElement<*>)
                throw IllegalArgumentException("Only NamedSyntaxElements can be used with this parser registry")

            val parser = getParserForElement(element)
            val parsed = parseElement(input, element, parser)

            map.put(element.getName(), parsed)
        }

        return map
    }

    private fun parseElement(input: ArgumentReader, element: NamedSyntaxElement<*>, parser: Parser): Any {
        input.skipSpaces()
        input.mark()
        var parsed: Any
        try {
            parsed = parser.parse(input, element)
        } catch (e: Exception) {
            parsed = wrapException(e)
        }

        input.removeMark()
        return parsed
    }

    override fun getSupportedTypes(): Set<Class<*>>
            = typeToParserMap.keys

    override fun registerParser(parser: Parser) {
        if (parser !is NamedParser)
            throw IllegalArgumentException("I only accept named parsers. If you really want to register a not named one, consider using registerParser(Parser, String)")

        for (name in parser.getSupportedNames())
            nameToParserMap[name] = parser

        for (type in parser.getSupportedTypes())
            typeToParserMap[type] = parser
    }

    fun registerParser(parser: Parser, name: String) {
        if (parser is NamedParser)
            throw IllegalArgumentException("I won't name a parser twice. If you are sure you want to do this then invoke toNamedParser yourself")

        nameToParserMap[name] = parser

        for (type in parser.getSupportedTypes())
            typeToParserMap[type] = parser
    }

    override fun removeParser(parser: Parser) {
        if (parser !is NamedParser)
            throw IllegalArgumentException("I only accept named parsers")

        for (type in parser.getSupportedTypes()) {
            typeToParserMap.removeIfMatches(type, parser)
        }

        for (name in parser.getSupportedNames()) {
            nameToParserMap.removeIfMatches(name, parser)
        }
    }

    private fun <K, V> MutableMap<K, V>.removeIfMatches(key: K, value: V) {
        if (this[key] == value)
            remove(key)
    }

    fun removeParser(name: String) {
        nameToParserMap.remove(name)
    }

    fun associateParserWithName(type: Class<*>, name: String) {
        val parser = getParserForType(type)
        nameToParserMap[name] = parser
    }

    private fun getParserForElement(element: NamedSyntaxElement<*>): Parser {
        if (element is CustomSyntaxElement<*>) {
            return element.getParser()
        } else {
            return getParserForName(element.getName())
        }
    }

    private fun getParserForType(type: Class<*>): Parser {
        val parser = typeToParserMap[type]
        if (parser != null)
            return parser

        throw IllegalArgumentException("Couldn't find parser supporting type '$type'")
    }

    private fun getParserForName(name: String): Parser {
        val parser = nameToParserMap[name]
        if (parser != null)
            return parser

        throw IllegalArgumentException("Couldn't find parser for name '$name'")
    }
}