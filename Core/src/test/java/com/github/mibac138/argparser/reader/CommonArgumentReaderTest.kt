package com.github.mibac138.argparser.reader

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.core.Is
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.StringReader

/**
 * Created by mibac138 on 07-04-2017.
 */
@RunWith(Parameterized::class)
class CommonArgumentReaderTest(private val producer: (String) -> ArgumentReader) {
    companion object {
        @JvmStatic
        @Parameters
        fun getReaders(): Collection<(String) -> ArgumentReader>
                = listOf(
                { s -> ReaderArgumentReader(StringReader(s)) },
                { s -> StringArgumentReader(s) })
    }

    private val READER_TEXT: String = "1234567890"
    private lateinit var reader: ArgumentReader

    @Before
    fun setUp() {
        reader = producer(READER_TEXT)
    }

    @Test fun autoStartFeed() {
        assertTrue(reader.getAvailableCount() > 0)
    }

    @Test fun fullRead() {
        val read = reader.read(10)
        assertEquals(READER_TEXT, read)
        assertEquals(10, reader.getLength())
        assertEquals(0, reader.getAvailableCount())
    }

    @Test fun fullReadByStep() {
        var output = ""
        var i = 0

        while (reader.hasNext()) {
            assertTrue(reader.getAvailableCount() > 0)
            output += reader.next()
            assertTrue(reader.getLength() > i++)
        }

        assertEquals(READER_TEXT, output)
        assertEquals(0, reader.getAvailableCount())
        assertEquals(READER_TEXT.length, reader.getLength())
    }

    @Test fun testSkip() {
        reader.skip(READER_TEXT.length)
        assertEquals(0, reader.getAvailableCount())
        assertEquals(READER_TEXT.length, reader.getLength())
    }

    @Test fun testSkip2() {
        reader.skip(9)
        assertEquals('0', reader.next())
        assertFalse(reader.hasNext())
    }

    @Test fun testContinuity() {
        for (i in 0 until READER_TEXT.length) {
            assertTrue(reader.hasNext())
            assertThat(reader.next(), Is(equalTo(READER_TEXT[i])))
        }

        assertEquals(READER_TEXT.length, reader.getLength())
    }


    @Test fun testLength() {
        assertTrue(reader.getLength() >= reader.getAvailableCount())
        reader.read(3)
        assertTrue(reader.getLength() + 3 >= reader.getAvailableCount())
        reader.skip(3)
        assertTrue(reader.getLength() + 3 + 3 >= reader.getAvailableCount())
    }

    @Test fun testMarkers() {
        while (reader.hasNext()) {
            reader.mark()
            reader.next()
        }

        for (i in 0 until READER_TEXT.length) {
            if (i % 2 == 0)
                reader.removeMark()
            else {
                reader.reset()
                assertEquals(READER_TEXT.substring(READER_TEXT.length - i - 2 + 1, READER_TEXT.length - i + 1), reader.read(2))
            }
        }
    }

    @Test(expected = IllegalArgumentException::class) fun testNegativeRead() {
        reader.read(-3)
    }

    @Test(expected = IllegalArgumentException::class) fun testOverRead() {
        reader.read(READER_TEXT.length + 1)
    }

    @Test(expected = IllegalArgumentException::class) fun testOverRead2() {
        reader.read(1000)
    }

    @Test(expected = IllegalArgumentException::class) fun testCharOverRead() {
        try {
            reader.readAll()
        } catch (e: Exception) {
            throw Error(e)
        }

        assertFalse(reader.hasNext())
        reader.next()
    }

    @Test fun testNoMarkers() {
        assertFalse(reader.removeMark())
        assertFalse(reader.reset())
    }

    @Test fun testRemoveMark() {
        reader.skip(2)

        reader.mark()
        reader.skip(6)
        reader.removeMark()

        assertEquals(READER_TEXT[8], reader.next())
    }

    @Test fun testReset() {
        reader.skip(2)

        reader.mark()
        reader.skip(6)
        reader.reset()

        assertEquals(READER_TEXT[2], reader.next())
    }

    private fun ArgumentReader.readAll() {
        while (hasNext())
            next()
    }
}