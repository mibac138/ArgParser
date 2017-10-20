package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL
import com.github.mibac138.argparser.syntax.dsl.syntaxElement
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 07-05-2017.
 */
class NameComponentHelperTest {
    @Test fun test() {
        val syntax = syntaxElement(Any::class.java) {
            name = "Hello"
        }

        assertEquals("Hello", syntax.name)
        assertEquals("Hello", syntax.get(NameComponent::class.java)?.name)
    }

    @Test(expected = IllegalStateException::class)
    fun dsl() {
        syntaxElement(Any::class.java) {
            name = "Hey"
            name = "Hi"
        }
    }

    @Test fun dslDefaultValueShouldBeNull() {
        val dsl = SyntaxElementDSL(Any::class.java)

        assertEquals(null, dsl.name)
    }

    @Test fun issue14() {
        val dsl = SyntaxElementDSL(Any::class.java)
        val anotherDsl = SyntaxElementDSL(Any::class.java)

        dsl.name = "dsl1 name"

        assertEquals(null, anotherDsl.name)
    }
}