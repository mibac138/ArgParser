package com.github.mibac138.argparser

import com.github.mibac138.argparser.reader.asReader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by mibac138 on 06-04-2017.
 */
class SequenceParserTest : ParserTest() {
    private val parser = SequenceParser()

    @Test fun supportedTypes() {
        assertTrue(parser.getSupportedTypes() == setOf(String::class.java))
    }

    @Test fun parse() {
        val reader = "Hey! Hi! Hello!".asReader()
        assertEquals("Hey!", parser.parse(reader, reqElement()))
        assertEquals("Hi!", parser.parse(reader, reqElement()))
        assertEquals("Hello!", parser.parse(reader, reqElement()))
    }
}