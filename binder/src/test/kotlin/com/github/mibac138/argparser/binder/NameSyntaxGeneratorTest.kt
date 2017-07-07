package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 07-07-2017.
 */
class NameSyntaxGeneratorTest {
    private val generator = NameSyntaxGenerator()

    @Test fun testKotlin() {
        val dsl = SyntaxElementDSL(Any::class.java)
        val param = this::kotlinFunction.parameters[0]

        generator.generate(dsl, param)

        assertEquals("name", dsl.name)
    }

    @Test fun testJava() {
        val dsl = SyntaxElementDSL(Any::class.java)
        val param = JavaClass()::javaMethod.parameters[0]

        generator.generate(dsl, param)

        assertEquals("a", dsl.name)
    }

    private fun kotlinFunction(@Name("name") string: String) {
        println(string)
    }
}