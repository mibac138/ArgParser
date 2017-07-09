package com.github.mibac138.argparser

import com.github.mibac138.argparser.exception.ParserInvalidInputException
import com.github.mibac138.argparser.parser.Parser
import com.github.mibac138.argparser.parser.SequenceParser
import com.github.mibac138.argparser.parser.SimpleParserRegistry
import com.github.mibac138.argparser.parser.readUntilCharOrDefault
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.*
import com.github.mibac138.argparser.syntax.dsl.SyntaxContainerDSL
import com.github.mibac138.argparser.syntax.dsl.element
import com.github.mibac138.argparser.syntax.dsl.syntaxContainer
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
    val registry = SimpleParserRegistry()

    @Test fun testRegisterParser() {
        val parser = parserOf(Any::class.java, BigInteger::class.java, Mockito::class.java)

        registry.registerParser(parser)
        assertTrue(registry.getSupportedTypes().containsAll(parser.getSupportedTypes()))

        registry.removeParser(parser)
        assertTrue(Collections.disjoint(registry.getSupportedTypes(), parser.getSupportedTypes()))
    }

    @Test(expected = NumberFormatException::class) fun testInvalidInput() {
        registry.parse("15 hi!".asReader(), SyntaxContainerDSL(Any::class.java)
                .element(String::class.java)
                .element(Int::class.java)
                .build())
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
        assertTrue(registry.parse("".asReader(), EmptySyntaxContainer).isEmpty())
    }

    @Test fun testEmptySyntaxParse() {
        assertTrue(registry.parse("true hi 2 aaa".asReader(), EmptySyntaxContainer).isEmpty())
    }

    @Test fun testSimpleParse() {
        val syntax = SyntaxContainerDSL(Any::class.java).element(Boolean::class.java).build()

        assertEquals(true, registry.parse("yes".asReader(), syntax)[0])
    }

    @Test fun testSimpleParse2() {
        val syntax = SyntaxElementImpl(Boolean::class.java)

        assertEquals(true, registry.parse("true".asReader(), syntax)[0])
    }

    @Test fun testCustomParser2() {
        val syntax = SyntaxElementImpl(Boolean::class.java, components = ParserComponent(object : Parser {
            override fun getSupportedTypes(): Set<Class<*>> = setOf(Boolean::class.java)

            override fun parse(input: ArgumentReader, syntax: SyntaxElement): Boolean?
                    = input.readUntilCharOrDefault(syntax, { !java.lang.Boolean.parseBoolean(it) })
        }))

        assertEquals(listOf(false), registry.parse("true".asReader(), syntax))
    }

    @Test(expected = IllegalArgumentException::class) fun testUnknownParser() {
        val syntax = SyntaxContainerDSL(Any::class.java).element(Mockito::class.java).build()

        registry.parse("hi".asReader(), syntax)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class) fun testProblematicParser() {
        val exception = ArrayIndexOutOfBoundsException()
        registry.registerParser(problematicParser(exception, Boolean::class.java))
        val syntax = SyntaxContainerDSL(Any::class.java).element(Boolean::class.java).build()

        registry.parse("hi".asReader(), syntax)
    }

    @Test(expected = ParserInvalidInputException::class) fun testProblematicParser2() {
        val exception = ParserInvalidInputException()
        val parser = problematicParser(exception, Boolean::class.java)
        registry.registerParser(parser)
        val syntax = SyntaxContainerDSL(Any::class.java).element(Boolean::class.java).build()

        registry.parse("hi".asReader(), syntax)
    }

    @Test fun testCustomParser() {
        val parser = parserOf(Boolean::class.java)
        val syntax = SyntaxContainerDSL(Any::class.java).element(Boolean::class.java).build()
        registry.registerParser(parser)

        registry.parse("hi".asReader(), syntax)

        verify(parser).parse(any(), any())
    }

    @Test fun issue10() {
        registry.registerParser(SequenceParser())
        val syntax = syntaxContainer {
            element(String::class.java)
            element(String::class.java) { required = false; defaultValue = "default" }
        }
        val output = registry.parse("yes".asReader(), syntax)


        assertEquals(listOf("yes", "default"), output)
    }

    private fun parserOf(vararg clazz: Class<*>): Parser {
        val parser = mock(Parser::class.java)
        `when`(parser.getSupportedTypes()).thenReturn(setOf(*clazz))
        `when`(parser.toString()).thenReturn("Fake parser of $clazz")
        return parser
    }

    private fun problematicParser(exception: Throwable, vararg clazz: Class<*>): Parser {
        val parser = mock(Parser::class.java)
        `when`(parser.parse(any(), any())).thenThrow(exception)
        `when`(parser.getSupportedTypes()).thenReturn(setOf(*clazz))
        return parser
    }
}