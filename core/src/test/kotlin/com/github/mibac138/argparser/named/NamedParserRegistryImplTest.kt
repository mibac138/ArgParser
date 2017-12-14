package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.exception.ParserInvalidInputException
import com.github.mibac138.argparser.parser.*
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.defaultValue
import com.github.mibac138.argparser.syntax.dsl.element
import com.github.mibac138.argparser.syntax.dsl.syntaxContainer
import com.github.mibac138.argparser.syntax.dsl.syntaxElement
import com.github.mibac138.argparser.syntax.parser
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 03-05-2017.
 */
class NamedParserRegistryImplTest {
    private val parser = NamedParserRegistryImpl()

    @Test
    fun test() {
        parser.registerParser(BooleanParser())
        parser.associateParserWithName(Boolean::class.java, "hi")
        val result = parser.parse("--hi: true".asReader(), syntaxElement(Boolean::class.java) { name = "hi" })
        assertEquals(mapOf("hi" to true), result.keyToValueMap)
    }

    @Test
    fun testSupportedTypes() {
        assertEquals(emptySet(), parser.supportedTypes)

        parser.registerParser(BooleanParser())

        assertEquals(setOf(Boolean::class.java, Boolean::class.javaObjectType), parser.supportedTypes)

        parser.removeParser(BooleanParser())

        assertEquals(emptySet(), parser.supportedTypes)
    }

    @Test
    fun testCustomParser() {
        val output = parser.parse("--hi: true".asReader(), syntaxElement(Boolean::class.java, {
            name = "hi"
            parser = InvertedBooleanParser(BooleanParser())
        }))

        assertEquals(mapOf("hi" to false), output.keyToValueMap)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRemoveParserWithAssociatedNames() {
        parser.registerParser("bool", BooleanParser())
        parser.associateParserWithName(Boolean::class.java, "value")
        parser.associateParserWithName(Boolean::class.java, "boolean")

        parser.removeParser(BooleanParser())

        // Expected to throw here. (couldn't find parser for name value)
        parser.parse("--value: true".asReader(), syntaxElement(Boolean::class.java) { name = "value" })
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAssocParserWithNameInvalid() {
        parser.associateParserWithName(Any::class.java, "object")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testUnknownType() {
        println(parser.parse("--hi: Hello!".asReader(), syntaxElement(String::class.java) { name = "hi" }))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidSyntax() {
        parser.parse("".asReader(), syntaxElement(Any::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidInput() {
        parser.parse("--".asReader(), syntaxElement(Any::class.java) { name = "" })
    }

    @Test(expected = Exception::class)
    fun testProblematicParser1() {
        parser.registerParser("color", object : Parser {
            override val supportedTypes = setOf(Any::class.java)

            override fun parse(input: ArgumentReader, syntax: SyntaxElement): Any
                    = throw Exception()
        })

        parser.parse("--color:".asReader(), syntaxElement(Any::class.java) { name = "color" })
    }


    @Test(expected = ParserInvalidInputException::class)
    fun testProblematicParser2() {
        parser.registerParser("color", object : Parser {
            override val supportedTypes = setOf(Any::class.java)

            override fun parse(input: ArgumentReader, syntax: SyntaxElement): Any
                    = throw ParserInvalidInputException()
        })

        parser.parse("--color:".asReader(), syntaxElement(Any::class.java) { name = "color" })
    }

    @Test
    fun issue10() {
        parser.registerParser("arg1", SequenceParser())
        parser.registerParser("arg2", SequenceParser())
        val syntax = syntaxContainer {
            element(String::class.java) { name = "arg1" }
            element(String::class.java) { name = "arg2"; required = false; defaultValue = "default" }
        }
        val output = parser.parse("--arg1=yes".asReader(), syntax)


        assertEquals(mapOf(
                "arg1" to "yes",
                "arg2" to "default"
                          ), output.keyToValueMap)
    }

    @Test
    fun testEquality() {
        val a = NamedParserRegistryImpl()
        val b = NamedParserRegistryImpl()

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a.toString(), b.toString())

        a.registerParser("bool", BooleanParser())
        b.registerParser("bool", BooleanParser())

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a.toString(), b.toString())
    }

    class InvertedBooleanParser(private val parser: BooleanParser) : Parser by parser {
        override fun parse(input: ArgumentReader, syntax: SyntaxElement): Boolean? = parser.parse(input, syntax)?.not()
    }
}