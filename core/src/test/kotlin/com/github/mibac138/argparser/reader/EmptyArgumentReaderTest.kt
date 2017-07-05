package com.github.mibac138.argparser.reader

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 05-07-2017.
 */
class EmptyArgumentReaderTest {
    @Test fun skip() {
        EmptyArgumentReader.skip(0)
    }

    @Test(expected = IllegalArgumentException::class) fun skipMoreThanAllowed() {
        EmptyArgumentReader.skip(1)
    }

    @Test fun read() {
        assertEquals("", EmptyArgumentReader.read(0))
    }

    @Test(expected = IllegalArgumentException::class) fun readMoreThanAllowed() {
        EmptyArgumentReader.read(1)
    }

    @Test fun hasNext() {
        assertEquals(false, EmptyArgumentReader.hasNext())
    }

    @Test fun getLength() {
        assertEquals(0, EmptyArgumentReader.getLength())
    }

    @Test fun getAvailableCount() {
        assertEquals(0, EmptyArgumentReader.getAvailableCount())
    }
}