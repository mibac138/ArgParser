package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.SimpleParserRegistry
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.dsl.SyntaxContainerDSL
import com.github.mibac138.argparser.syntax.dsl.element
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 30-04-2017.
 */
@RunWith(Parameterized::class)
class BoundMethodTest(private val producer: () -> BoundMethod) {
    companion object {
        private val obj = Tested()
        private val jMethod = Tested::class.java.getMethod("method", String::class.java, Int::class.java)
        private val kMethod = obj::method

        @JvmStatic
        @Parameters
        fun data(): Array<() -> BoundMethod> {
            return arrayOf({ CallableBoundMethod(kMethod) },
                    { ReflectionBoundMethod(obj, jMethod) })
        }

        class Tested {
            fun method(name: String, num: Int): Pair<String, Int>
                    = Pair(name, num)
        }
    }

    private val method = producer()
    private val binding = Binder.bind(method)

    @Test fun testValid() {
        assertEquals(Pair("Hello!", 10), binding.invoke("Hello! 10".asReader(), SimpleParserRegistry()))
        assertEquals(Pair("Hi!", -90), binding.invoke("Hi! -90".asReader(), SimpleParserRegistry()))
    }

    @Test(expected = IllegalArgumentException::class) fun testInvalid() {
        binding.invoke("".asReader(), SimpleParserRegistry())
    }

    @Test fun testSyntax() {
        assertEquals(SyntaxContainerDSL().element(String::class.java).element(Integer::class.java).build(),
                method.syntax)
    }

    @Test fun testEquality() {
        val a = producer()
        val b = producer()

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a.toString(), b.toString())
    }

}