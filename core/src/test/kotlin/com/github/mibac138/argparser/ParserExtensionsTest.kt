package com.github.mibac138.argparser

import com.github.mibac138.argparser.parser.parseOrDefault
import com.github.mibac138.argparser.parser.readUntilCharOrDefault
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.EmptySyntaxContainer
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
        assertTrue(parseOrDefault(defElement(true), this::exception)!!)
        assertFalse(parseOrDefault(defElement(false), this::exception)!!)
    }

    @Test(expected = NumberFormatException::class) fun readUntilSpaceOrDefaultException() {
        "".asReader().readUntilCharOrDefault(reqElement(), String::toInt)
    }

    @Test fun readUntilSpaceOrDefaultWithDefault() {
        assertEquals(3, "".asReader().readUntilCharOrDefault(defElement(3), String::toInt))
    }

    @Test fun readUntilCharOrDefault() {
        assertEquals("321", "1234".asReader().readUntilCharOrDefault(EmptySyntaxContainer, {
            it.reversed()
        }, '4'))
    }

    private fun exception(): Boolean {
        throw Exception()
    }
}