package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.reader.asReader
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 03-05-2017.
 */
class BasicArgumentMatchResultTest {
    @Test fun testEquality() {
        val value = "".asReader()
        val a = BasicArgumentMatchResult("hi", value)
        val b = BasicArgumentMatchResult("hi", value)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a.toString(), b.toString())
    }
}