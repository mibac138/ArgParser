package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 07-07-2017.
 */
class KotlinDefaultValueSyntaxGeneratorTest {
    private val generator = KotlinDefaultValueSyntaxGenerator()

    @Test fun testKotlin() {
        val dsl = SyntaxElementDSL(Any::class.java)
        val param = this::kotlinFunction.parameters[0]

        generator.generate(dsl, param)

        assertEquals(dsl.required, false)
    }

    @Test fun testNonDefault() {
        val dsl = SyntaxElementDSL(Any::class.java)
        val param = this::kotlinFunction2.parameters[0]

        generator.generate(dsl, param)

        assertEquals(dsl.required, true)
    }

    private fun kotlinFunction(string: String = "Hi!") {
        println(string)
    }

    private fun kotlinFunction2(string: String) {
        println(string)
    }
}