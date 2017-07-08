package com.github.mibac138.argparser.reader

import org.junit.Test
import java.util.regex.Pattern
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Created by mibac138 on 02-05-2017.
 */
class ArgumentReaderUtilTest {
    @Test fun readUntilChar() {
        assertEquals("", "".asReader().readUntilChar())
        assertEquals("", "     ".asReader().readUntilChar())
        assertEquals("", "  Hiya!  ".asReader().readUntilChar())
        assertEquals("Hey!", "Hey!  ".asReader().readUntilChar())
        assertEquals("1234", "1234567890".asReader().readUntilChar('5'))
    }

    @Test fun readUntilCharWithAction() {
        "".asReader().readUntilChar {
            assertEquals("", it)
        }
        "     ".asReader().readUntilChar {
            assertEquals("", it)
        }
        "  Hiya!  ".asReader().readUntilChar {
            assertEquals("", it)
        }
        "Hey!  ".asReader().readUntilChar {
            assertEquals("Hey!", it)
        }
        "1234567890".asReader().readUntilChar('5') {
            assertEquals("1234", it)
        }

        val reader = "1234 5678".asReader()
        try {
            reader.readUntilChar {
                throw IllegalArgumentException()
            }
            fail("Exception not thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("1234 5678", reader.readUntilChar('\r'))
        }
    }

    @Test fun skipChar() {
        assertEquals("Hi", "    Hi".asReader().skipCharAndReadRest())
        assertEquals("1", "00001".asReader().skipCharAndReadRest('0'))
    }

    @Test fun matchPattern() {
        val pattern = Pattern.compile("a")
        val reader = " abc".asReader()

        reader.mark()
        reader.read(1)
        reader.matchPattern(pattern)
        reader.reset()

        assertEquals(" abc", reader.readUntilChar('\r'))
    }

    private fun ArgumentReader.skipCharAndReadRest(char: Char = ' '): String {
        skipChar(char)
        return readUntilChar('\r')
    }
}