package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 07-07-2017.
 */
class ParamNameSyntaxGeneratorTest {
    private val generator = ParamNameSyntaxGenerator()

    @Test fun testKotlinGeneration() {
        val dsl = SyntaxElementDSL(Any::class.java)
        val param = this::kotlinFunction.parameters[0]

        generator.generate(dsl, param)

        assertEquals("string", dsl.name)
    }

//    On Oracle JVM you can't get a param's name (only arg0, arg1)
//    so this test is pointless
//    @Test fun testJavaGeneration() {
//        val dsl = SyntaxElementDSL(Any::class.java)
//        val param = JavaClass()::javaMethod.parameters[0]
//
//        generator.generate(dsl, param)
//
//        assertEquals("string", dsl.name)
//    }


    private fun kotlinFunction(string: String) {
        println(string)
    }
}