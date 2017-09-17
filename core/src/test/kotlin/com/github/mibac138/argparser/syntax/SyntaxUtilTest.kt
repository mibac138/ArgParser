package com.github.mibac138.argparser.syntax

import com.github.mibac138.argparser.syntax.dsl.SyntaxContainerDSL
import com.github.mibac138.argparser.syntax.dsl.element
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
        assertEquals(1, SyntaxElementImpl(Any::class.java).size)
        assertEquals(5, SyntaxContainerDSL(Any::class.java)
                .element(Any::class.java).element(Any::class.java)
                .element(Any::class.java).element(Any::class.java)
                .element(Any::class.java).build().size)
    }

    @Test
    fun testGetRequiredSize() {
        assertEquals(0, null.requiredSize)
        assertEquals(0, EmptySyntaxContainer.requiredSize)

        assertEquals(1, SyntaxElementImpl(Any::class.java).requiredSize)
        assertEquals(0, SyntaxElementImpl(Any::class.java, false).requiredSize)

        assertEquals(5, SyntaxContainerDSL(Any::class.java)
                .element(Any::class.java).element(Any::class.java)
                .element(Any::class.java).element(Any::class.java)
                .element(Any::class.java).build().requiredSize)

        assertEquals(3, SyntaxContainerDSL(Any::class.java)
                .element(Any::class.java) { required = false }
                .element(Any::class.java) { required = false }
                .element(Any::class.java)
                .element(Any::class.java)
                .element(Any::class.java)
                .build().requiredSize)
    }

    @Test
    fun testIterator() {
        assertContentEquals(emptyList<Any>(), EmptySyntaxContainer.iterator())
        assertContentEquals(emptyList<Any>(), null.iterator())
        assertContentEquals(listOf(SyntaxElementImpl(Any::class.java)),
                            SyntaxContainerDSL(Any::class.java).element(Any::class.java).build().iterator())
        assertContentEquals(listOf(SyntaxElementImpl(Any::class.java)), SyntaxElementImpl(Any::class.java).iterator())
    }

    private fun <T> assertContentEquals(expected: List<T>, actual: Iterator<T>) {
        expected.forEach { assertEquals(it, actual.next()) }
        assertFalse(actual.hasNext())
    }
}