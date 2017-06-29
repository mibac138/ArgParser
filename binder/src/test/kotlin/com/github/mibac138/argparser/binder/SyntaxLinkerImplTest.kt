package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.syntax.dsl.syntaxElement
import org.junit.Assert.assertArrayEquals
import org.junit.Test

/**
 * Created by mibac138 on 29-06-2017.
 */
class SyntaxLinkerImplTest {

    @Test fun valueAsInputRoot() {
        class A

        val aInstance = A()
        val linker = SyntaxLinkerImpl(syntaxElement(A::class.java))
        val output = linker.link(aInstance)

        assertArrayEquals(arrayOf<Any?>(aInstance), output)
    }
}