package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.dsl.syntaxElement
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 29-06-2017.
 */
class SyntaxLinkerImplTest {

    @Test fun subclasses() {
        open class A
        class B : A()

        val bInstance = B()
        val syntax = syntaxElement(A::class.java)
        val linker = SyntaxLinkerImpl(syntax)

        assertEquals<Map<SyntaxElement, Any?>>(
                mapOf(syntax to bInstance),
                linker.link(bInstance))
    }

    @Test fun valueAsInputRoot() {
        class A

        val aInstance = A()
        val syntax = syntaxElement(A::class.java)
        val linker = SyntaxLinkerImpl(syntax)

        assertEquals<Map<SyntaxElement, Any?>>(
                mapOf(syntax to aInstance),
                linker.link(aInstance))
    }
}