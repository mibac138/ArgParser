package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.GlobalParserRegistry
import com.github.mibac138.argparser.IntParser
import com.github.mibac138.argparser.SequenceParser
import com.github.mibac138.argparser.named.NamedParserRegistry
import com.github.mibac138.argparser.named.toNamedParser
import com.github.mibac138.argparser.reader.asReader
import org.junit.Test

/**
 * Created by mibac138 on 14-04-2017.
 */
class BindingTest {
    @Test fun test() {
        val obj = Tester()
        val binding = Binder.bind(obj, obj.javaClass.getMethod("method", Integer::class.java, String::class.java))
        println("Syntax: ${binding.getSyntax()}")

        println("\n\n")
        binding.invoke("10 hi!".asReader(), GlobalParserRegistry.INSTANCE)

        println("\n\n")
        binding.invoke("hello! 11".asReader(), GlobalParserRegistry.INSTANCE)
        println(binding.getExceptions())
    }

    @Test fun test2() {
        val obj = Tester()
        val binding = Binder.bind(obj, obj.javaClass.getMethod("method", Integer::class.java, String::class.java))
        println("Syntax: ${binding.getSyntax()}")

        val parser = NamedParserRegistry()
        parser.registerParser(SequenceParser().toNamedParser("string"))
        parser.registerParser(IntParser().toNamedParser("int"))


        parser.associateParserWithName(Integer::class.java, "number")
        parser.associateParserWithName(String::class.java, "greeting")

        binding.invoke("--number:12 --greeting=Hi!".asReader(), parser)

        binding.invoke("--greeting:HelloWorld! --number:41242142141".asReader(), parser)
    }


    class Tester {
        fun method(@Arg(name = "number") num: Int?, @Arg(name = "greeting") hi: String?) {
            println("num: $num, hi: $hi")
        }
    }
}