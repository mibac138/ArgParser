package com.github.mibac138.argparser

import com.github.mibac138.argparser.reader.asReader
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by mibac138 on 07-04-2017.
 */
class ParserExtensionsTest : ParserTest() {
    @Test fun parseOrDefault() {
        parseOrDefault(reqElement(), { Any() })
    }

    @Test(expected = Exception::class) fun exceptionInParseOrDefault() {
        parseOrDefault(reqElement(), this::exception)
    }

    @Test fun defaultParseOrDefault() {
        assertTrue(parseOrDefault(defElement(true), this::exception))
        assertFalse(parseOrDefault(defElement(false), this::exception))
    }

    @Test(expected = NumberFormatException::class) fun readUntilSpaceOrDefaultException() {
        "".asReader().readUntilSpaceOrDefault(reqElement(), String::toInt)
    }

    @Test fun readUntilSpaceOrDefaultWithDefault() {
        assertEquals(3, "".asReader().readUntilSpaceOrDefault(defElement(3), String::toInt))
    }

    private fun exception(): Boolean {
        throw Exception()
    }
}