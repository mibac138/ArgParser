package com.github.mibac138.argparser

import com.github.mibac138.argparser.exception.ParserException
import com.github.mibac138.argparser.exception.ParserInternalException
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.skipSpaces
import com.github.mibac138.argparser.syntax.CustomSyntaxElement
import com.github.mibac138.argparser.syntax.SyntaxContainer
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.iterator
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by mibac138 on 05-04-2017.
 */
class GlobalParserRegistry private constructor() : ParserRegistry {
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
        for (clazz in parser.getSupportedTypes()) {
            classToParserMap.put(clazz, parser)
        }
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

    override fun parse(input: ArgumentReader, syntax: SyntaxElement<*>): List<*> {
        if (!input.hasNext())
            return emptyList<Any>()

        val size: Int
        if (syntax is SyntaxContainer) {
            if (syntax.getContent().isEmpty())
                return emptyList<Any>()
            else
                size = syntax.getContent().size
        } else {
            size = 1
        }

        return parseSyntax(input, size, syntax.iterator())
    }

    private fun parseSyntax(input: ArgumentReader, size: Int, iterator: Iterator<SyntaxElement<*>>): List<*> {
        val result = ArrayList<Any>(size)

        for (element in iterator) {
            val parser = getParserForElement(element)

            result += parseElement(input, element, parser)
        }

        return result
    }

    private fun parseElement(reader: ArgumentReader, element: SyntaxElement<*>, parser: Parser): Any {
        reader.skipSpaces()
        reader.mark()
        var parsed: Any
        try {
            parsed = parser.parse(reader, element)
        } catch (e: Exception) {
            parsed = wrapException(e)
        }

        reader.removeMark()

        return parsed
    }

    private fun getParserForElement(element: SyntaxElement<*>): Parser {
        if (element is CustomSyntaxElement<*>) {
            return element.getParser()
        } else {
            return getParserForType(element.getOutputType())
        }
    }

    private fun getParserForType(type: Class<*>): Parser {
        val parser = classToParserMap[type]
        if (parser != null)
            return parser

        throw IllegalArgumentException("Tried to parse $type but no eligible parsers were registered")
    }

    private fun wrapException(e: Exception?): Exception {
        if (e is ParserException) {
            return e
        } else {
            return ParserInternalException(e)
        }
    }

    companion object {
        val INSTANCE = GlobalParserRegistry()
    }
}