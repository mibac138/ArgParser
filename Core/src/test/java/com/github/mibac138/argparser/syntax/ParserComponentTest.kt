package com.github.mibac138.argparser.syntax

import com.github.mibac138.argparser.parser.BooleanParser
import com.github.mibac138.argparser.parser.IntParser
import com.github.mibac138.argparser.parser.SequenceParser
import com.github.mibac138.argparser.syntax.dsl.syntaxElement
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mibac138 on 07-05-2017.
 */
class ParserComponentTest {
    @Test fun test() {
        val parser = BooleanParser()
        val syntax = syntaxElement(Any::class.java) {
            parser { parser }
        }

        assertEquals(parser, syntax.parser)
        assertEquals(parser, syntax.get(ParserComponent::class.java)?.parser)
    }

    @Test fun dsl() {
        val parser = SequenceParser()
        val syntax = syntaxElement(Any::class.java) {
            parser { BooleanParser() }
            parser { IntParser() }
            parser { parser }
        }

        assertEquals(parser, syntax.parser)
    }
}