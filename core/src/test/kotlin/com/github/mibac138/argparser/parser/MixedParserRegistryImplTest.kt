package com.github.mibac138.argparser.parser

import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.named.withNamedParsers
import com.github.mibac138.argparser.parser.exception.ValueReassignmentException
import com.github.mibac138.argparser.reader.EmptyArgumentReader
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.defaultValue
import com.github.mibac138.argparser.syntax.dsl.element
import com.github.mibac138.argparser.syntax.dsl.syntaxContainer
import com.github.mibac138.argparser.syntax.dsl.syntaxElement
import com.github.mibac138.argparser.syntax.index
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 07-05-2017.
 */
class MixedParserRegistryImplTest {
    val parser = MixedParserRegistryImpl()

    @Test
    fun basicTest() {
        parser.registerParser(0, SequenceParser())
        parser.registerParser(1, SequenceParser())
        val output = parser.parse("Hello! :)".asReader(), syntaxContainer {
            element(String::class.java)
            element(String::class.java)
        })

        assertEquals(
                listOf("Hello!", ":)"),
                output[null])
    }

    @Test
    fun mediumTest() {
        parser.registerParser(0, IntParser())
        parser.registerParser("name", SequenceParser())
        val syntax = syntaxContainer {
            element(Int::class.java)
            element(String::class.java, { name = "name" })
        }
        val output = parser.parse("15 --name:Mike".asReader(), syntax)

        assertEquals(
                mapOf(
                        null to listOf(15), "name" to "Mike"
                     ),
                output.keyToValueMap)
    }

    @Test
    fun complexTest() {
        parser.
                withOrderedParsers(
                        BooleanParser(),
                        IntParser(),
                        BooleanParser()
                                  ).
                withNamedParsers(
                        "seq1" to SequenceParser(),
                        "seq2" to SequenceParser(),
                        "seq3" to SequenceParser()
                                )

        val syntax = syntaxContainer {
            element(Boolean::class.java)
            element(Int::class.java)
            element(Boolean::class.java)
            element(String::class.java, { name = "seq1" })
            element(String::class.java, { name = "seq2" })
            element(String::class.java, { name = "seq3" })
        }
        val output = parser.parse("true --seq1='Hiya!' --seq2: 'Lorem ipsum' 100 false --seq3=Hi".asReader(), syntax)

        assertEquals(
                mapOf(
                        null to listOf(true, 100, false),
                        "seq1" to "'Hiya!'",
                        "seq2" to "'Lorem ipsum'",
                        "seq3" to "Hi"
                     ), output.keyToValueMap)
    }


    @Test(expected = ValueReassignmentException.Named::class)
    fun namedElementReassign() {
        parser.
                withNamedParsers(
                        "a" to IntParser()).
                withOrderedParsers(
                        0 to IntParser())

        val syntax = syntaxContainer {
            element(Int::class.java) { name = "a" }
            element(Int::class.java)
        }

        parser.parse("--a: 1 --a=2".asReader(), syntax)
    }

    @Test
    fun issue10() {
        parser.registerParser(0, SequenceParser())
        parser.registerParser(1, SequenceParser())
        parser.registerParser("seq", SequenceParser())
        val syntax = syntaxContainer {
            element(String::class.java) { name = "seq" }
            element(String::class.java) /*{ autoIndex() } // implicit*/
            element(String::class.java) { required = false; defaultValue = "default" }
        }

        val output = parser.parse("yes --seq:sequence".asReader(), syntax)

        assertEquals(
                mapOf(
                        null to listOf("yes", "default"),
                        "seq" to "sequence"
                     ),
                output.keyToValueMap)
    }

    @Test
    fun issue9() {
        parser.registerParser(0, SequenceParser())

        val syntax = syntaxElement(String::class.java) {
            required = false
            defaultValue = "default"
        }

        val output = parser.parse(" ".asReader(), syntax)
        assertEquals<Map<String?, Any?>>(
                mapOf(
                        null to listOf("default")
                     ),
                output.keyToValueMap)
    }

    @Suppress("FunctionName")
    @Test
    fun issue29_1() {
        val somethingParser = SequenceParser()
        parser.registerParser("name", somethingParser)
        parser.registerParser(0, somethingParser)

        val syntax = syntaxElement(String::class.java) { name = "name" }

        assertEquals<Map<String?, *>>(
                mapOf("name" to "123"),
                parser.parse("--name:123".asReader(), syntax).keyToValueMap)
    }

    @Suppress("FunctionName")
    @Test
    fun issue29_2() {
        val somethingParser = SequenceParser()
        parser.registerParser("name", somethingParser)
        parser.registerParser(0, somethingParser)

        val syntax = syntaxElement(String::class.java) { name = "name"; index = 0 }

        assertEquals<Map<String?, *>>(
                mapOf("name" to "123"),
                parser.parse("123".asReader(), syntax).keyToValueMap)
    }

    @Suppress("FunctionName")
    @Test
    fun issue29_3() {
        val somethingParser = SequenceParser()
        parser.registerParser("name", somethingParser)
        parser.registerParser(0, somethingParser)

        val syntax = syntaxContainer {
            element(String::class.java) { name = "name" }
            element(String::class.java)
        }

        assertEquals(
                mapOf(null to listOf("123"), "name" to ""),
                parser.parse("123".asReader(), syntax).keyToValueMap)
    }

    @Test
    fun complexTestLevel2() {
        parser.
                withNamedParsers(
                        "a" to SequenceParser(),
                        "b" to SequenceParser()).
                withOrderedParsers(
                        SequenceParser(),
                        SequenceParser())

        val syntax = syntaxContainer {
            element(String::class.java) { name = "a"; index = 0 }
            element(String::class.java) { name = "b"; index = 1 }
        }

        assertEquals<Map<String?, Any?>>(
                mapOf(
                        "a" to "Hi",
                        "b" to "Hello"
                     ),
                parser.parse("--a: Hi Hello".asReader(), syntax).keyToValueMap)

    }

    @Test
    fun namingISHard() {
        parser.
                withNamedParsers(
                        "a" to SequenceParser(),
                        "b" to SequenceParser())

        val syntax = syntaxContainer {
            element(String::class.java) { name = "a" }
            element(String::class.java) { name = "b" }
        }

        parser.parse(EmptyArgumentReader, syntax)
    }


    @Test
    fun complexTestLevel3() {
        parser.
                withNamedParsers(
                        "a" to SequenceParser(),
                        "b" to SequenceParser()).
                withOrderedParsers(
                        SequenceParser(),
                        SequenceParser(),
                        SequenceParser())

        val syntax = syntaxContainer {
            element(String::class.java) { index = 0 }
            element(String::class.java) { name = "a"; index = 1 }
            element(String::class.java) { name = "b"; index = 2 }
        }

        assertEquals(
                mapOf(
                        "a" to "Hi",
                        "b" to "Hello",
                        null to listOf("ordered")
                     ),
                parser.parse("--b: Hello ordered Hi".asReader(), syntax).keyToValueMap)

    }

    @Suppress("FunctionName")
    @Test
    fun issue29_reassignGood() {
        val nameParser = SequenceParser()
        parser.registerParser("name", nameParser)
        parser.registerParser(0, nameParser)

        val syntax = syntaxContainer {
            element(String::class.java) { name = "name"; index = 0 }
        }

        parser.parse("aa --name:aa".asReader(), syntax)
    }

    @Test(expected = ValueReassignmentException.Named::class)
    fun issue29_reassignBad() {
        val nameParser = SequenceParser()
        parser.registerParser("name", nameParser)
        parser.registerParser(0, nameParser)

        val syntax = syntaxContainer {
            element(String::class.java) { name = "name"; index = 0 }
            element(Any::class.java)
        }

        parser.parse("aa --name:aa".asReader(), syntax)
    }
}

