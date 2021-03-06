package com.github.mibac138.argparser

import com.github.mibac138.argparser.parser.IntParser
import com.github.mibac138.argparser.reader.asReader
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFalse

/**
 * Created by mibac138 on 06-04-2017.
 */
class IntParserTest : ParserTest() {
    private val parser = IntParser()

    @Test fun supportedTypes() {
        assertEquals(setOf(Int::class.java, Integer::class.java), parser.supportedTypes)
    }

    @Test fun parse() {
        assertEquals(1, parser.parse("1".asReader(), reqElement()))
        assertEquals(-25, parser.parse("-25".asReader(), reqElement()))
        assertEquals(1337, parser.parse("1337".asReader(), reqElement()))
        assertEquals(Int.MAX_VALUE, parser.parse(Int.MAX_VALUE.asReader(), reqElement()))
    }

    @Test(expected = NumberFormatException::class) fun parseInvalid() {
        parser.parse("Hi!".asReader(), reqElement())
    }

    @Test fun parseInvalidWithDefault() {
        assertEquals(23, parser.parse("Hi!".asReader(), defElement(23)))
    }

    @Test fun testToString() {
        assertFalse(parser.toString().isNullOrBlank())
    }
}