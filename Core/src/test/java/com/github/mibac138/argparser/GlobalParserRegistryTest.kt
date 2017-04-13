package com.github.mibac138.argparser

import com.github.mibac138.argparser.exception.ParserException
import com.github.mibac138.argparser.exception.ParserInvalidInputException
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.EmptySyntax
import com.github.mibac138.argparser.syntax.SyntaxBuilder
import com.nhaarman.mockito_kotlin.any
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.math.BigInteger
import java.util.*

/**
 * Created by mibac138 on 06-04-2017.
 */
class GlobalParserRegistryTest {
    val registry = GlobalParserRegistry.INSTANCE

    @Test fun testRegisterParser() {
        val parser = parserOf(Any::class.java, BigInteger::class.java, Mockito::class.java)

        registry.registerParser(parser)
        assertTrue(registry.getSupportedTypes().containsAll(parser.getSupportedTypes()))

        registry.removeParser(parser)
        assertTrue(Collections.disjoint(registry.getSupportedTypes(), parser.getSupportedTypes()))
    }

    @Test fun testRemove() {
        val parser = parserOf(Date::class.java)

        assertFalse(registry.getSupportedTypes().contains(Date::class.java))

        registry.removeParser(parser)

        assertFalse(registry.getSupportedTypes().contains(Date::class.java))
    }

    @Test fun testRemoveNotOwnType() {
        val parser1 = parserOf(Mockito::class.java)
        val parser2 = parserOf(Mockito::class.java)

        assertFalse(registry.getSupportedTypes().contains(Mockito::class.java))

        registry.registerParser(parser1)
        assertTrue(registry.getSupportedTypes().contains(Mockito::class.java))

        registry.removeParser(parser2)
        assertTrue(registry.getSupportedTypes().contains(Mockito::class.java))

        registry.removeParser(parser1)
        assertFalse(registry.getSupportedTypes().contains(Mockito::class.java))
    }


    @Test fun testEmptyParse() {
        assertTrue(registry.parse("".asReader(), EmptySyntax()).isEmpty())
    }

    @Test fun testEmptySyntaxParse() {
        assertTrue(registry.parse("true hi 2 aaaa".asReader(), EmptySyntax()).isEmpty())
    }

    @Test fun testSimpleParse() {
        val syntax = SyntaxBuilder.start().appendType(Boolean::class.java).build()

        assertTrue(registry.parse("yes".asReader(), syntax)[0] == true)
    }

    @Test(expected = IllegalArgumentException::class) fun testUnknownParser() {
        val syntax = SyntaxBuilder.start().appendType(Mockito::class.java).build()

        registry.parse("hi".asReader(), syntax)
    }

    @Test fun testProblematicParser() {
        val exception = ArrayIndexOutOfBoundsException()
        registry.registerParser(problematicParser(exception, Boolean::class.java))
        val syntax = SyntaxBuilder.start().appendType(Boolean::class.java).build()

        val parsed = registry.parse("hi".asReader(), syntax)
        assertTrue(parsed[0] is ParserException)
        assertEquals((parsed[0] as Exception).cause, exception)
    }

    @Test fun testProblematicParser2() {
        val exception = ParserInvalidInputException()
        registry.registerParser(problematicParser(exception, Boolean::class.java))
        val syntax = SyntaxBuilder.start().appendType(Boolean::class.java).build()

        val parsed = registry.parse("hi".asReader(), syntax)
        assertTrue(parsed[0] is ParserException)
        assertTrue(parsed[0] is ParserInvalidInputException)
        assertEquals(exception, parsed[0])
    }

    @Test fun testCustomParser() {
        val parser = parserOf(Boolean::class.java)
        val syntax = SyntaxBuilder.start().appendComplex<Boolean>().ofType(Boolean::class.java).withCustomParser(parser).build().build()

        registry.parse("hi".asReader(), syntax)

        verify(parser).parse(any(), any())
    }

    private fun parserOf(vararg clazz: Class<*>): Parser {
        val parser = mock(Parser::class.java)
        `when`(parser.getSupportedTypes()).thenReturn(setOf(*clazz))
        return parser
    }

    private fun problematicParser(exception: Throwable, vararg clazz: Class<*>): Parser {
        val parser = mock(Parser::class.java)
        `when`(parser.parse(any(), any())).thenThrow(exception)
        `when`(parser.getSupportedTypes()).thenReturn(setOf(*clazz))
        return parser
    }
}