package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.syntax.dsl.syntaxElement
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 07-05-2017.
 */
class NameComponentKtTest {
    @Test fun test() {
        val syntax = syntaxElement(Any::class.java) {
            name = "Hello"
        }

        assertEquals("Hello", syntax.name)
        assertEquals("Hello", syntax.get(NameComponent::class.java)?.name)
    }

    @Test fun dsl() {
        val syntax = syntaxElement(Any::class.java) {
            name { "Hey" }
            name { "Hi" }
            name { "Hello" }
        }

        assertEquals("Hello", syntax.name)
    }
}