package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.parser.IntParser
import com.github.mibac138.argparser.parser.MixedParserRegistryImpl
import com.github.mibac138.argparser.parser.SequenceParser
import com.github.mibac138.argparser.parser.SimpleParserRegistry
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.dsl.SyntaxContainerDSL
import com.github.mibac138.argparser.syntax.dsl.element
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.reflect.KFunction
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 30-04-2017.
 */
@RunWith(Parameterized::class)
class BoundMethodTest(private val producer: (KFunction<*>) -> BoundMethod) {
    companion object {
        private val obj = Tested()
        private val kMethod = obj::method

        @JvmStatic
        @Parameters
        fun data(): Array<(KFunction<*>) -> BoundMethod> {
            return arrayOf({ func -> CallableBoundMethod(func) })
        }

        class Tested {
            fun method(name: String, num: Int)
                    = Pair(name, num)

            fun defaultValues(name: String = "Hiya", @Name("num") num: Int = 5)
                    = Pair(name, num)
        }
    }

    private val method = producer(kMethod)
    private val binding = Binder.bind(method)

    @Test fun testValid() {
        assertEquals(Pair("Hello!", 10), binding.invoke("Hello! 10".asReader(), SimpleParserRegistry()))
        assertEquals(Pair("Hi!", -90), binding.invoke("Hi! -90".asReader(), SimpleParserRegistry()))
    }

    @Test(expected = IllegalArgumentException::class) fun testInvalid() {
        binding.invoke("".asReader(), SimpleParserRegistry())
    }

    @Test fun testDefaultValues() {
        val binding = Binder.bind(producer(obj::defaultValues))

        assertEquals(Pair("Hello!", 10), binding.invoke("Hello! 10".asReader(), SimpleParserRegistry()))
        assertEquals(Pair("Hello!", 5), binding.invoke("Hello!".asReader(), SimpleParserRegistry()))

        val parser = MixedParserRegistryImpl()
        parser.registerParser(SequenceParser())
        parser.registerParser(IntParser(), "num")
        assertEquals(Pair("Hiya", 10), binding.invoke("--num: 10".asReader(), parser))
    }

    @Test fun testInstanceParameterWithOwner() {
        val binding = Binder.bind(CallableBoundMethod(Tested::method, Tested()))

        assertEquals(Pair("Hello!", 10), binding.invoke("Hello! 10".asReader(), SimpleParserRegistry()))
    }

    @Test fun testExtensionParameterWithOwner() {
        val binding = Binder.bind(CallableBoundMethod(String::extension, "Instance param"))

        assertEquals(Pair("Hello!", 10), binding.invoke("Hello! 10".asReader(), SimpleParserRegistry()))
    }

    @Test(expected = IllegalArgumentException::class) fun testInstanceParameterWithoutOwner() {
        CallableBoundMethod(Tested::method)
    }

    @Test(expected = IllegalArgumentException::class) fun testExtensionReceiverParameterWithoutOwner() {
        CallableBoundMethod(String::extension)
    }

    @Test(expected = IllegalArgumentException::class) fun testWithWrongOwner() {
        CallableBoundMethod(String::extension, 123456)
    }

    @Test fun testSyntax() {
        assertEquals(SyntaxContainerDSL()
                .element(String::class.java)
                .element(Int::class.javaObjectType)
                .build(), method.syntax)
    }

    @Test fun testEquality() {
        val a = producer(kMethod)
        val b = producer(kMethod)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a.toString(), b.toString())
    }

}

fun String.extension(name: String, num: Int) = Pair(name, num)
