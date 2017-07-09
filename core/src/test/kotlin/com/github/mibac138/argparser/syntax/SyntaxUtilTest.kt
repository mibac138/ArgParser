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
    @Test fun testGetSize() {
        assertEquals(0, null.getSize())
        assertEquals(0, EmptySyntaxContainer.getSize())
        assertEquals(1, SyntaxElementImpl(Any::class.java).getSize())
        assertEquals(5, SyntaxContainerDSL(Any::class.java)
                .element(Any::class.java).element(Any::class.java)
                .element(Any::class.java).element(Any::class.java)
                .element(Any::class.java).build().getSize())
    }

    @Test fun testGetRequiredSize() {
        assertEquals(0, null.getRequiredSize())
        assertEquals(0, EmptySyntaxContainer.getRequiredSize())

        assertEquals(1, SyntaxElementImpl(Any::class.java).getRequiredSize())
        assertEquals(0, SyntaxElementImpl(Any::class.java, false).getRequiredSize())

        assertEquals(5, SyntaxContainerDSL(Any::class.java)
                .element(Any::class.java).element(Any::class.java)
                .element(Any::class.java).element(Any::class.java)
                .element(Any::class.java).build().getRequiredSize())

        assertEquals(3, SyntaxContainerDSL(Any::class.java)
                .element(Any::class.java) { required = false }
                .element(Any::class.java) { required = false }
                .element(Any::class.java)
                .element(Any::class.java)
                .element(Any::class.java)
                .build().getRequiredSize())

    }

    @Test fun testIterator() {
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