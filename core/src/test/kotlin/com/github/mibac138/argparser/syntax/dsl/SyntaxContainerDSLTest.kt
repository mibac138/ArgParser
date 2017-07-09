package com.github.mibac138.argparser.syntax.dsl

import com.github.mibac138.argparser.syntax.EmptySyntaxContainer
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.SyntaxElementImpl
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by mibac138 on 07-05-2017.
 */
class SyntaxContainerDSLTest {
    @Test fun empty() {
        assertEquals(EmptySyntaxContainer, syntaxContainer())
    }

    @Test fun withInit() {
        val element = MyElement()
        val syntax = syntaxContainer({
            add(element)
            required = true
        })

        assertTrue(syntax.required)
        assertEquals(element, syntax.content[0])
    }

    @Test fun withClassWithInit() {
        val element = MyElement()
        val syntax = syntaxContainer(MyElement::class.java) {
            add(element)
            required = true
        }

        assertTrue(syntax.required)
        assertEquals(MyElement::class.java, syntax.outputType)
        assertEquals(element, syntax.content[0])
    }


    class MyElement : SyntaxElement by SyntaxElementImpl<Any>(Any::class.java)
}