package com.github.mibac138.argparser

import com.github.mibac138.argparser.exception.ParserInternalException
import com.github.mibac138.argparser.exception.ParserInvalidInputException
import com.github.mibac138.argparser.parser.PrecheckedParser
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.reader.skipChar
import com.github.mibac138.argparser.reader.skipUntilChar
import com.github.mibac138.argparser.syntax.SyntaxElement
import org.junit.Assert.*
import org.junit.Test
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by mibac138 on 06-04-2017.
 */

class PrecheckedParserTest : ParserTest() {
    val parser: BasicBooleanParser = BasicBooleanParser()

    @Test fun supportedTypes() {
        assertEquals(parser.getSupportedTypes(), setOf(Boolean::class.java))
    }

    @Test fun simpleParse() {
        assertTrue(parser.parse("yes".asReader(), reqElement()))
        assertFalse(parser.parse("no".asReader(), reqElement()))
    }

    @Test fun matchingLength() {
        var length = parser.noLength()
        for (i in 0..5) {
            val newLength = parser.matchingLength(length) + length
            assertTrue(newLength > 0)
            assertTrue(newLength > length)
            length = newLength
        }
    }

    @Test(expected = ParserInvalidInputException::class) fun invalidInput() {
        parser.parse("maybe".asReader(), reqElement())
    }

    @Test(expected = ParserInternalException::class) fun emptyInput() {
        parser.parse("".asReader(), reqElement())
    }

    @Test fun invalidInputWithDefault() {
        assertEquals(true, parser.parse("maybe".asReader(), defElement(true)))
    }

    @Test(expected = Exception::class) fun problematicParser() {
        val parser = ProblematicParser()

        parser.parse("test".asReader(), reqElement())
    }

    @Test fun exceptionInsideParser() {
        val reader = "maybe yes maybe no".asReader()

        try {
            parser.parse(reader, reqElement())
        } catch (e: ParserInvalidInputException) {
        }

        // If an error occurs PrecheckedParser should revert read text
        assertEquals(reader.getLength(), reader.getAvailableCount())

        reader.skipUntilChar(' ')

        assertTrue(parser.parse(reader, reqElement()))

        // If an error occurs PrecheckedParser should revert read text
        assertEquals(reader.getLength() - "maybe yes".length, reader.getAvailableCount())

        try {
            parser.parse(reader, reqElement())
        } catch (e: ParserInvalidInputException) {
        }

        assertEquals(reader.getLength() - "maybe yes".length, reader.getAvailableCount())

        reader.skipChar(' ')
        reader.skipUntilChar(' ')

        assertFalse(parser.parse(reader, reqElement()))
    }

    @Test fun testToString() {
        assertFalse(parser.toString().isNullOrBlank())
    }


    class BasicBooleanParser : PrecheckedParser<Boolean>() {
        fun noLength(): Int = super.NO_LENGTH

        fun matchingLength(lastLength: Int): Int = super.getMatchingLength(lastLength)

        override fun getSupportedTypes(): Set<Class<*>> = setOf(Boolean::class.java)

        override fun getPattern(): Pattern
                = Pattern.compile("^(yes|no)")

        override fun parse(matcher: Matcher, element: SyntaxElement<*>): Boolean {
            val result = matcher.toMatchResult().group(0)

            if (result.equals("yes", ignoreCase = true)) return true
            if (result.equals("no", ignoreCase = true)) return false

            throw ParserInvalidInputException()
        }
    }

    class ProblematicParser : PrecheckedParser<Any>() {
        override fun getSupportedTypes(): Set<Class<*>> = setOf(Any::class.java)

        override fun getPattern(): Pattern = Pattern.compile("")

        override fun parse(matcher: Matcher, element: SyntaxElement<*>): Any {
            throw Exception("Problematic parser")
        }
    }
}