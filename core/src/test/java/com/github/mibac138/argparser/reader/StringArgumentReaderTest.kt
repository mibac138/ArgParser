package com.github.mibac138.argparser.reader

import org.junit.Assert.*
import org.junit.Test

/**
 * Created by mibac138 on 06-04-2017.
 */
class StringArgumentReaderTest {
    private val READER_TEXT: String = "1234567890"
    private val reader: ArgumentReader = StringArgumentReader(READER_TEXT)
    @Test fun skipTest() {
        reader.skip(9)
        assertTrue(reader.next() == '0')
        assertFalse(reader.hasNext())
    }

    @Test fun testContinuity() {
        for (i in 0 until READER_TEXT.length) {
            assertEquals(READER_TEXT[i], reader.next())
            assertTrue(i == READER_TEXT.length - 1 || reader.hasNext())
        }
    }

    @Test fun testRead() {
        assertEquals(READER_TEXT, reader.read(reader.getAvailableCount()))
    }

    @Test fun testAvailableCount() {
        assertEquals(READER_TEXT.length, reader.getAvailableCount())
        assertEquals(READER_TEXT.length, reader.getLength())

        reader.skip(reader.getAvailableCount())
        assertEquals(0, reader.getAvailableCount())
        assertEquals(READER_TEXT.length, reader.getLength())
    }

    @Test fun testLength() {
        assertEquals(reader.getLength(), reader.getAvailableCount())
        reader.read(3)
        assertEquals(reader.getLength() - 3, reader.getAvailableCount())
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
        reader.read(reader.getAvailableCount() + 1)
    }

    @Test(expected = IllegalArgumentException::class) fun testCharOverRead() {
        try {
            reader.read(reader.getAvailableCount())
        } catch (e: Exception) {
            throw Error()
        }

        assertFalse(reader.hasNext())
        reader.next()
    }

    @Test fun testNoMarkers() {
        assertFalse(reader.removeMark())
        assertFalse(reader.reset())
    }

    @Test fun testReadUntilSpace() {
        val reader = StringArgumentReader("Hey! Hi! Hello!")
        assertEquals("Hey!", reader.readUntilChar(' '))
        reader.skipChar(' ')
        assertEquals("Hi!", reader.readUntilChar(' '))
        reader.skipChar(' ')
        assertEquals("Hello!", reader.readUntilChar(' '))
    }

    @Test fun testSpacelessReadUntilSpace() {
        assertEquals(READER_TEXT, reader.readUntilChar(' '))
    }

    @Test fun testSkipSpaces() {
        fun StringArgumentReader.readAvailable(): String {
            mark()
            val read = read(getAvailableCount())
            reset()
            return read
        }

        val reader = StringArgumentReader("    Hey!   Hi!     Hello!")

        reader.skipChar(' ')
        assertEquals("Hey!   Hi!     Hello!", reader.readAvailable())
        reader.skip("Hey!".length)

        reader.skipChar(' ')
        assertEquals("Hi!     Hello!", reader.readAvailable())
        reader.skip("Hi!".length)

        reader.skipChar(' ')
        assertEquals("Hello!", reader.readAvailable())

        // Verify not overskipping
        reader.skipChar(' ')
        assertEquals("Hello!", reader.readAvailable())
    }

    @Test fun testSafeReadUntilSpace() {
        val reader = StringArgumentReader("Hey! Hi! Hello!")

        repeat(3) {
            reader.mark()
            val original = reader.readUntilChar(' ')
            reader.reset()
            try {
                reader.readUntilChar(' ') { throw Exception() }
            } catch (e: Exception) {
            } finally {
                assertEquals(original, reader.readUntilChar(' '))
            }
        }
    }

    @Test fun testAsReader() {
        assertEquals("1234567890", "1234567890".asReader().read("1234567890".length))
        assertEquals("1234567890", 1234567890.asReader().read("1234567890".length))
    }

    @Test fun testEquality() {
        val first = StringArgumentReader("Hello world!")
        val second = StringArgumentReader("Hello world!")

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first.toString(), second.toString())

        first.skip(4)
        second.skip(4)

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first.toString(), second.toString())
    }
}