package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.named.NamedParserRegistryImpl
import com.github.mibac138.argparser.parser.IntParser
import com.github.mibac138.argparser.parser.SequenceParser
import com.github.mibac138.argparser.parser.SimpleParserRegistry
import com.github.mibac138.argparser.reader.asReader
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 14-04-2017.
 */
class BindingTest {
    @Test fun annotations() {
        val bound = Binder.bind(this)

        assertEquals(1, bound.size)
        val hi = bound["Hi"]!!

        assertEquals("Hiya" to true, hi.invoke("Hiya true".asReader(), SimpleParserRegistry()))
    }

    @BindMethod("Hi")
    fun hi(a: String, bool: Boolean) = Pair(a, bool)


    @Test fun invokeUnnamedWithValidInput() {
        val binding = Binder.bind(Tester()::method)

        assertEquals(Pair(10, "hi!"), binding.invoke("10 hi!".asReader(), SimpleParserRegistry()))
    }

    @Test fun invokeUnnamedWithInvalidInput() {
        val binding = Binder.bind(Tester()::method)

        binding.invoke("hello! 11".asReader(), SimpleParserRegistry())
        assertFalse(binding.exceptions.isEmpty())
    }

    @Test fun invokeNamed() {
        val binding = Binder.bind(Tester()::method)

        val parser = NamedParserRegistryImpl()
        parser.registerParser(SequenceParser())
        parser.registerParser(IntParser())

        parser.associateParserWithName(Integer::class.java, "number")
        parser.associateParserWithName(String::class.java, "greeting")

        assertEquals(
                Pair(12, "Hi!"),
                binding.invoke("--number:12 --greeting=Hi!".asReader(), parser))
        assertEquals(
                Pair(123456, "HelloWorld!"),
                binding.invoke("--greeting:HelloWorld! --number:123456".asReader(), parser))
    }

    @Test(expected = IllegalArgumentException::class) fun invokeNamedWithInvalidInput() {
        val binding = Binder.bind(UnnamedTester()::method)

        val parser = NamedParserRegistryImpl()
        parser.registerParser(SequenceParser())
        parser.registerParser(IntParser())

        parser.associateParserWithName(Integer::class.java, "number")
        parser.associateParserWithName(String::class.java, "greeting")

        assertEquals(Pair("Hi!", 12), binding.invoke("Hi! 12".asReader(), parser))
    }


    class UnnamedTester {
        fun method(@Arg name: String, @Arg value: Int): Pair<String, Int> {
            return Pair(name, value)
        }
    }

    class Tester {
        fun method(@Arg(name = "number") num: Int?, @Arg(name = "greeting") hi: String?): Pair<Int?, String?> {
            return Pair(num, hi)
        }
    }
}