package com.github.mibac138.argparser.reader

import com.github.mibac138.argparser.reader.ArgumentString.Companion.NOT_REQUIRED
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

/**
 * Created by mibac138 on 08-04-2017.
 */
@RunWith(Parameterized::class)
class CommonArgumentStringTest(private val producer: () -> ArgumentString) {
    companion object {
        @JvmStatic
        @Parameters
        fun getStrings(): Collection<() -> ArgumentString> =
                listOf({ RegularString() }, { EcoFriendlyString() })
    }

    private lateinit var argument: ArgumentString

    @Before fun setUp() {
        argument = producer()
    }

    @Test fun testRecycle() {
        argument.append("1234567890".toCharArray(), 0, 10)
        argument.read(3)
        // Recycling shouldn't affect other functions (results should be the same)
        argument.recycle()

        assertEquals(10, argument.getLength())
        assertEquals(3, argument.getPosition())
        assertEquals(10 - 3, argument.getAvailableAmount())
        assertEquals("456", argument.read(3))
        argument.recycle()

        assertEquals(10, argument.getLength())
        assertEquals(6, argument.getPosition())
        assertEquals(10 - 3 - 3, argument.getAvailableAmount())
    }

    @Test fun testAppend() {
        assertTrue(argument.getLength() == 0)

        argument.append('a')
        assertTrue(argument.getLength() == 1)
        assertTrue(argument.getAvailableAmount() == 1)
        assertTrue(argument.getPosition() == 0)

        assertEquals("a", argument.read(1))
        assertEquals(1, argument.getLength())
        assertEquals(0, argument.getAvailableAmount())
        assertEquals(1, argument.getPosition())
    }

    @Test fun testAppendArray() {
        argument.append("1234567890".toCharArray(), 0, 3)

        assertEquals(3, argument.getLength())
        assertEquals(0, argument.getPosition())
        assertEquals(3, argument.getAvailableAmount())
        assertEquals("123", argument.read(3))
        assertEquals(0, argument.getAvailableAmount())
        assertEquals(3, argument.getLength())
    }

    @Test fun testAppendArray2() {
        argument.append("1234567890".toCharArray(), 0, 10)

        assertEquals(10, argument.getLength())
        assertEquals(0, argument.getPosition())
        assertEquals(10, argument.getAvailableAmount())
        assertEquals("1234", argument.read(4))
        assertEquals(10 - "1234".length, argument.getAvailableAmount())
        assertEquals(10, argument.getLength())
    }

    @Test fun testSetPosition() {
        argument.setPosition(5)
        assertEquals(5, argument.getPosition())
    }

    @Test fun testResetToPosition() {
        argument.append("1234567890".toCharArray(), 0, 10)

        argument.read(2)
        argument.markRequiredPosition()
        argument.read(6)
        argument.recycle()
        argument.resetToRequiredPosition()
        assertEquals("34567890", argument.read(8))
        assertEquals(NOT_REQUIRED, argument.getRequiredPosition())
    }

    @Test fun testRemoveRequiredPosition() {
        argument.append("1234567890".toCharArray(), 0, 10)

        argument.read(2)
        argument.markRequiredPosition()
        argument.read(6)
        argument.removeRequiredPosition()
        assertEquals("90", argument.read(2))
        assertEquals(NOT_REQUIRED, argument.getRequiredPosition())
    }

    @Test fun testBigInput() {
        // 2000 character long string with random characters
        val input = "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"
        val argument = producer()
        argument.append(input.toCharArray(), 0, 2000)

        argument.setPosition(100)
        argument.recycle()

        assertEquals(100, argument.getPosition())
        assertEquals(2000 - 100, argument.getAvailableAmount())
        assertEquals(2000, argument.getLength())

        argument.markRequiredPosition()
        argument.read(900)

        assertEquals(1000, argument.getPosition())
        assertEquals(2000 - 1000, argument.getAvailableAmount())
        assertEquals(100, argument.getRequiredPosition())
        assertEquals(2000, argument.getLength())

        argument.read(100)
        argument.recycle()

        assertEquals(1100, argument.getPosition())
        assertEquals(2000 - 1000 - 100, argument.getAvailableAmount())
        assertEquals(100, argument.getRequiredPosition())
        assertEquals(2000, argument.getLength())


        argument.resetToRequiredPosition()


        assertEquals(100, argument.getPosition())
        assertEquals(2000 - 100, argument.getAvailableAmount())
        assertEquals(2000, argument.getLength())
    }

    @Test fun testAddPosition() {
        argument.append("Hi!".toCharArray(), 0, 3)
        argument.addPosition(2)

        assertEquals(1, argument.getAvailableAmount())
        assertEquals(2, argument.getPosition())
    }

    @Test(expected = IllegalStateException::class) fun `reset to marker without it being set`() {
        argument.resetToRequiredPosition()
    }

    @Test(expected = IllegalStateException::class) fun `remove required position without it being set`() {
        argument.removeRequiredPosition()
    }

    @Test(expected = IllegalArgumentException::class) fun `read more than available`() {
        argument.read(10)
    }

    @Test(expected = IllegalArgumentException::class) fun `read negative amount`() {
        argument.read(-10)
    }

    @Test(expected = IllegalArgumentException::class) fun `set position to negative number`() {
        argument.setPosition(-1)
    }

    @Test(expected = IllegalArgumentException::class) fun `add position to negative number`() {
        argument.addPosition(-3)
    }

    @Test fun testEquality() {
        val first = producer()
        val second = producer()

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first.toString(), second.toString())

        first.append("Hello!".toCharArray(), 0, "Hello!".length)
        second.append("Hello!".toCharArray(), 0, "Hello!".length)

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first.toString(), second.toString())
    }
}