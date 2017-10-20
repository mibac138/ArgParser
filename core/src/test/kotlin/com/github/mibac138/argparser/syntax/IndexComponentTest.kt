package com.github.mibac138.argparser.syntax

import com.github.mibac138.argparser.syntax.dsl.element
import com.github.mibac138.argparser.syntax.dsl.syntaxContainer
import org.junit.Test
import kotlin.test.assertEquals

class IndexComponentTest {
    @Test
    fun autoAssign1() {
        val syntax = syntaxContainer {
            element(Any::class.java) { index = 3 }
            element(Any::class.java) { autoIndex() }
        }

        assertEquals(3, syntax.content[0].index)
        assertEquals(4, syntax.content[1].index)
    }

    @Test
    fun autoAssign2() {
        val syntax = syntaxContainer {
            element(Any::class.java) { autoIndex() }
            element(Any::class.java) { autoIndex() }
        }

        assertEquals(0, syntax.content[0].index)
        assertEquals(1, syntax.content[1].index)
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeIndex() {
        syntaxContainer {
            element(Any::class.java) { index = -2 }
        }
    }
}