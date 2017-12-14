package com.github.mibac138.argparser

import com.github.mibac138.argparser.exception.ParserInvalidInputException
import com.github.mibac138.argparser.parser.*
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.*
import com.github.mibac138.argparser.syntax.dsl.element
import com.github.mibac138.argparser.syntax.dsl.syntaxContainer
import com.github.mibac138.argparser.syntax.dsl.syntaxElement
import com.nhaarman.mockito_kotlin.any
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.math.BigInteger
import java.util.*
import kotlin.test.assertTrue

/**
 * Created by mibac138 on 06-04-2017.
 */
class SimpleParserRegistryTest {
    private val registry = SimpleParserRegistry()

    @Test
    fun testRegisterParser() {
        val parser = parserOf(Any::class.java, BigInteger::class.java, Mockito::class.java)

        registry.registerParser(parser)
        assertTrue(registry.supportedTypes.containsAll(parser.supportedTypes))

        registry.removeParser(parser)
        assertTrue(Collections.disjoint(registry.supportedTypes, parser.supportedTypes))
    }

    @Test(expected = NumberFormatException::class)
    fun testInvalidInput() {
        registry.parse("15 hi!".asReader(), syntaxContainer(Any::class.java) {
            element(String::class.java)
            element(Int::class.java)
        })
    }

    @Test
    fun testRemove() {
        val parser = parserOf(Date::class.java)

        assertFalse(registry.supportedTypes.contains(Date::class.java))

        registry.removeParser(parser)

        assertFalse(registry.supportedTypes.contains(Date::class.java))
    }

    @Test
    fun testRemoveNotOwnType() {
        val parser1 = parserOf(Mockito::class.java)
        val parser2 = parserOf(Mockito::class.java)

        assertFalse(registry.supportedTypes.contains(Mockito::class.java))

        registry.registerParser(parser1)
        assertTrue(registry.supportedTypes.contains(Mockito::class.java))

        registry.removeParser(parser2)
        assertTrue(registry.supportedTypes.contains(Mockito::class.java))

        registry.removeParser(parser1)
        assertFalse(registry.supportedTypes.contains(Mockito::class.java))
    }

    @Test
    fun testEmptyParse() {
        assertTrue(registry.parse("".asReader(), EmptySyntaxContainer).isEmpty())
    }

    @Test
    fun testEmptySyntaxParse() {
        assertTrue(registry.parse("true hi 2 aaa".asReader(), EmptySyntaxContainer).isEmpty())
    }

    @Test
    fun testSimpleParse() {
        val syntax = syntaxContainer(Any::class.java) {
            element(Boolean::class.java)
        }

        assertEquals(true, registry.parse("yes".asReader(), syntax)[0])
    }

    @Test
    fun testSimpleParse2() {
        val syntax = syntaxElement(Boolean::class.java)

        assertEquals(true, registry.parse("true".asReader(), syntax)[0])
    }

    @Test
    fun testCustomParser2() {
        val syntax = syntaxElement(Boolean::class.java) {
            parser = object : Parser {
                override val supportedTypes = setOf(Boolean::class.java)

                override fun parse(input: ArgumentReader, syntax: SyntaxElement): Boolean?
                        = input.readUntilCharOrDefault(syntax, { !java.lang.Boolean.parseBoolean(it) })
            }
        }

        assertEquals(mapOf(0 to false), registry.parse("true".asReader(), syntax))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testUnknownParser() {
        val syntax = syntaxContainer(Any::class.java) {
            element(Mockito::class.java)
        }

        registry.parse("hi".asReader(), syntax)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testProblematicParser() {
        val syntax = syntaxContainer(Any::class.java) {
            element(Boolean::class.java)
        }

        val exception = ArrayIndexOutOfBoundsException()
        registry.registerParser(problematicParser(exception, Boolean::class.java))

        registry.parse("hi".asReader(), syntax)
    }

    @Test(expected = ParserInvalidInputException::class)
    fun testProblematicParser2() {
        val exception = ParserInvalidInputException()
        val parser = problematicParser(exception, Boolean::class.java)
        registry.registerParser(parser)
        val syntax = syntaxContainer(Any::class.java) {
            element(Boolean::class.java)
        }

        registry.parse("hi".asReader(), syntax)
    }

    @Test
    fun testCustomParser() {
        val parser = parserOf(Boolean::class.java)
        val syntax = syntaxContainer(Any::class.java) {
            element(Boolean::class.java)
        }

        registry.registerParser(parser)

        registry.parse("hi".asReader(), syntax)

        verify(parser).parse(any(), any())
    }

    @Test
    fun issue10() {
        registry.registerParser(SequenceParser())
        val syntax = syntaxContainer {
            element(String::class.java)
            element(String::class.java) { required = false; defaultValue = "default" }
        }

        val output = registry.parse("yes".asReader(), syntax)

        assertEquals(mapOf(0 to "yes", 1 to "default"), output)
    }

    @Test
    fun testOrderedParser() {
        registry.registerParser(SequenceParser())
        registry.registerParser(0, customSequenceParser())

        val output = registry.parse("!Hello, World!".asReader(), syntaxElement(String::class.java))

        assertEquals(mapOf(0 to "!Hello, World!"), output.keyToValueMap)
    }

    private fun customSequenceParser(): SequenceParser {
        val parser = SequenceParser()
        parser.addQuotationMark('!')
        return parser
    }

    private fun parserOf(vararg clazz: Class<*>): Parser {
        val parser = mock(Parser::class.java)
        `when`(parser.supportedTypes).thenReturn(setOf(*clazz))
        `when`(parser.toString()).thenReturn("Fake parser of $clazz")
        return parser
    }

    private fun problematicParser(exception: Throwable, vararg clazz: Class<*>): Parser {
        val parser = mock(Parser::class.java)
        `when`(parser.parse(any(), any())).thenThrow(exception)
        `when`(parser.supportedTypes).thenReturn(setOf(*clazz))
        return parser
    }
}