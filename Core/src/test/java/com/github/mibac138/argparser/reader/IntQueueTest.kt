package com.github.mibac138.argparser.reader

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by mibac138 on 07-04-2017.
 */
class IntQueueTest {
    private val queue: IntQueue = IntQueue()

    @Test fun testShift() {
        queue.add(5)
        queue.add(6)
        queue.add(7)
        queue.add(8)
        queue.add(9)
        queue.shift(5)
        assertEquals(9 - 5, queue.remove())
        assertEquals(8 - 5, queue.remove())
        assertEquals(7 - 5, queue.remove())
        assertEquals(6 - 5, queue.remove())
        assertEquals(5 - 5, queue.remove())
    }
}