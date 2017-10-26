package com.github.mibac138.argparser.syntax

import com.github.mibac138.argparser.syntax.dsl.element
import com.github.mibac138.argparser.syntax.dsl.syntaxContainer
import com.github.mibac138.argparser.syntax.dsl.syntaxElement
import org.junit.Test
import kotlin.test.assertEquals

class IndexComponentTest {
    @Test
    fun autoAssign1() {
        val syntax = syntaxContainer {
            element(Any::class.java) { index = 3 }
            element(Any::class.java) /*{ autoIndex() } // implicit*/
        }

        assertEquals(3, syntax.content[0].index)
        assertEquals(4, syntax.content[1].index)
    }

    @Test
    fun autoAssign2() {
        val syntax = syntaxContainer {
            element(Any::class.java) /*{ autoIndex() } // implicit*/
            element(Any::class.java) /*{ autoIndex() } // implicit*/
        }

        assertEquals(0, syntax.content[0].index)
        assertEquals(1, syntax.content[1].index)
    }

    @Test
    fun autoAssign3() {
        val syntax = syntaxElement(Any::class.java) /*{ autoIndex() } // implicit*/

        assertEquals(0, syntax.index)
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeIndex() {
        syntaxContainer {
            element(Any::class.java) { index = -2 }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun theSameIndex() {
        syntaxContainer {
            element(Any::class.java) { index = 0 }
            element(Any::class.java) { index = 0 }
        }
    }
}