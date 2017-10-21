package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.parser.*
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.dsl.element
import com.github.mibac138.argparser.syntax.dsl.syntaxContainer
import com.github.mibac138.argparser.syntax.index
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

    @Test
    fun testValid() {
        assertEquals(Pair("Hello!", 10), binding.invoke("Hello! 10".asReader(), SimpleParserRegistry()))
        assertEquals(Pair("Hi!", -90), binding.invoke("Hi! -90".asReader(), SimpleParserRegistry()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalid() {
        binding.invoke("".asReader(), SimpleParserRegistry())
    }

    @Test
    fun testDefaultValues() {
        val binding = Binder.bind(producer(obj::defaultValues))

        assertEquals(Pair("Hello!", 10), binding.invoke("Hello! 10".asReader(), SimpleParserRegistry()))
        assertEquals(Pair("Hello!", 5), binding.invoke("Hello!".asReader(), SimpleParserRegistry()))

        val parser = MixedParserRegistryImpl()
        parser.registerParser(SequenceParser())
        parser.registerParser(IntParser(), "num")
        assertEquals(Pair("Hiya", 10), binding.invoke("--num: 10".asReader(), parser))
    }

    @Test
    fun testInstanceParameterWithOwner() {
        val binding = Binder.bind(CallableBoundMethod(Tested()::method))

        assertEquals(Pair("Hello!", 10), binding.invoke("Hello! 10".asReader(), SimpleParserRegistry()))
    }

    @Test
    fun testExtensionParameterWithOwner() {
        val binding = Binder.bind(CallableBoundMethod("Instance param"::extension))

        assertEquals(Pair("Hello!", 10), binding.invoke("Hello! 10".asReader(), SimpleParserRegistry()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInstanceParameterWithoutOwner() {
        CallableBoundMethod(Tested::method)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testExtensionReceiverParameterWithoutOwner() {
        CallableBoundMethod(String::extension)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWithWrongOwner() {
        MethodBinder.bindMethod(String::extension, 123456)
    }

    @Test
    fun testSyntax1() {
        // fun method(name: String, num: Int)

        assertEquals(syntaxContainer {
            element(String::class.java) { index = 0 }
            element(Int::class.javaObjectType) { index = 1 }
        }, method.syntax)
    }

    @Test
    fun testSyntax2() {
        //fun defaultValues(name: String = "Hiya", @Name("num") num: Int = 5)

        assertEquals(syntaxContainer {
            element(String::class.java) { index = 0; required = false }
            element(Int::class.javaObjectType) { name = "num"; required = false }
        }, producer(Tested()::defaultValues).syntax)
    }

    @Test
    fun testEquality() {
        val a = producer(kMethod)
        val b = producer(kMethod)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a.toString(), b.toString())
    }

    fun function32(a: String, b: String) = Pair(a, b)

    @Test
    fun issue32() {
        val method = MethodBinder.bindMethod(this::function32)
        val linker = SyntaxLinkerImpl(method.syntax)
        assertEquals(Pair("a", "b"), method.invoke(linker.link(listOf("a", "b"))))
    }

}

@Suppress("unused")
fun String.extension(name: String, num: Int) = Pair(name, num)
