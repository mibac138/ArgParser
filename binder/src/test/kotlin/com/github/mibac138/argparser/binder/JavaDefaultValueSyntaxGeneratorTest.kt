package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.binder.JavaDefaultValueSyntaxGenerator.NO_DEFAULT_VALUE
import com.github.mibac138.argparser.syntax.DefaultValueComponent
import com.github.mibac138.argparser.syntax.defaultValue
import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 09-07-2017.
 */
class JavaDefaultValueSyntaxGeneratorTest {
    private val generator: (Array<Any?>) -> JavaDefaultValueSyntaxGenerator = { JavaDefaultValueSyntaxGenerator(*it) }

    @Test fun test() {
        val dsl = SyntaxElementDSL(Any::class.java)
        val param = this::kotlinFunction.parameters[0]

        generator(arrayOf("default")).generate(dsl, param)

        assertEquals("default", dsl.defaultValue)
    }

    @Test fun test2() {
        val dsl = SyntaxElementDSL(Any::class.java)
        val param = this::kotlinFunction.parameters[1]

        generator(arrayOf(null, 10)).generate(dsl, param)

        assertEquals(10, dsl.defaultValue)
    }

    @Test fun testNoDefaultValue() {
        val dsl = SyntaxElementDSL(Any::class.java)
        val param = this::kotlinFunction.parameters[0]

        generator(arrayOf(NO_DEFAULT_VALUE)).generate(dsl, param)

        assertEquals(null, dsl.defaultValue)
        assertEquals(null, dsl.components.firstOrNull { it is DefaultValueComponent })
    }

    private fun kotlinFunction(string: String, int: Int) {
        println("string = $string, int = $int")
    }
}