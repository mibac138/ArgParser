package com.github.mibac138.argparser

import com.github.mibac138.argparser.parser.SequenceParser
import com.github.mibac138.argparser.reader.EmptyArgumentReader
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.EmptySyntaxContainer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertNotEquals

/**
 * Created by mibac138 on 06-04-2017.
 */
class SequenceParserTest : ParserTest() {
    private val parser = SequenceParser()

    @Test fun issue8() {
        assertEquals("", parser.parse(EmptyArgumentReader, EmptySyntaxContainer))
    }

    @Test fun supportedTypes() {
        assertTrue(parser.getSupportedTypes() == setOf(String::class.java))
    }

    @Test fun parse() {
        val reader = "Hey! Hi! Hello!".asReader()
        assertEquals("Hey!", parser.parse(reader, reqElement()))
        assertEquals("Hi!", parser.parse(reader, reqElement()))
        assertEquals("Hello!", parser.parse(reader, reqElement()))

        assertEquals("'Hello world!'", parser.parse("'Hello world!' aaaa".asReader(), reqElement()))
        parser.removeQuotationMark('\'')
        assertEquals("'Hello", parser.parse("'Hello world! aaaa".asReader(), reqElement()))
        parser.addQuotationMark('M')
        assertEquals("MHello world!M", parser.parse("MHello world!M aaaa".asReader(), reqElement()))
    }

    @Test fun equality() {
        val a = SequenceParser()
        val b = SequenceParser()

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a.toString(), b.toString())

        a.addQuotationMark('v')

        assertNotEquals(a, b)
        assertNotEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a.toString(), b.toString())
    }
}