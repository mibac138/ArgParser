package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.reader.readUntilChar
import org.junit.Test
import java.util.regex.Pattern
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 03-05-2017.
 */
class DefaultArgumentMatcherTest {
    @Test fun testEquality() {
        val a = DefaultArgumentMatcher(Pattern.compile(""))
        val b = DefaultArgumentMatcher(Pattern.compile(""))

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a.toString(), b.toString())
    }

    @Test fun testMatch() {
        val matcher = DefaultArgumentMatcher(Pattern.compile("--([a-zA-Z]+)(?:=|: ?)"))
        var match = matcher.match("--test: Hello!".asReader())!!

        assertEquals("test", match.getName())
        assertEquals("Hello!", match.getValue().readAll())

        match = matcher.match("--name=Mike".asReader())!!

        assertEquals("name", match.getName())
        assertEquals("Mike", match.getValue().readAll())
    }

    private fun ArgumentReader.readAll(): String {
        return readUntilChar('\r') // \r should never occur so it should read until the end of stream
    }
}