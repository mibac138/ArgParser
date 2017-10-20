package com.github.mibac138.argparser.syntax

import com.github.mibac138.argparser.syntax.dsl.element
import com.github.mibac138.argparser.syntax.dsl.syntaxContainer
import com.github.mibac138.argparser.syntax.dsl.syntaxElement
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Created by mibac138 on 02-05-2017.
 */
class SyntaxUtilTest {
    @Test
    fun testGetSize() {
        assertEquals(0, null.size)
        assertEquals(0, EmptySyntaxContainer.size)
        assertEquals(1, syntaxElement(Any::class.java).size)
        assertEquals(5, syntaxContainer(Any::class.java) {
            element(Any::class.java)
            element(Any::class.java)
            element(Any::class.java)
            element(Any::class.java)
            element(Any::class.java)
        }.size)
    }

    @Test
    fun testGetRequiredSize() {
        assertEquals(0, null.requiredSize)
        assertEquals(0, EmptySyntaxContainer.requiredSize)

        assertEquals(1, syntaxElement(Any::class.java).requiredSize)
        assertEquals(0, syntaxElement(Any::class.java) { required = false }.requiredSize)

        assertEquals(5, syntaxContainer(Any::class.java) {
            element(Any::class.java)
            element(Any::class.java)
            element(Any::class.java)
            element(Any::class.java)
            element(Any::class.java)
        }.requiredSize)

        assertEquals(3, syntaxContainer(Any::class.java) {
            element(Any::class.java) { required = false }
            element(Any::class.java) { required = false }
            element(Any::class.java)
            element(Any::class.java)
            element(Any::class.java)
        }.requiredSize)
    }

    @Test
    fun testIterator() {
        assertContentEquals(emptyList<Any>(), EmptySyntaxContainer.iterator())
        assertContentEquals(emptyList<Any>(), null.iterator())
        assertContentEquals(
                listOf(syntaxElement(Any::class.java)),
                syntaxContainer(Any::class.java) { element(Any::class.java) }.iterator())

        assertContentEquals(listOf(syntaxElement(Any::class.java)), syntaxElement(Any::class.java).iterator())
    }

    private fun <T> assertContentEquals(expected: List<T>, actual: Iterator<T>) {
        expected.forEach { assertEquals(it, actual.next()) }
        assertFalse(actual.hasNext())
    }
}