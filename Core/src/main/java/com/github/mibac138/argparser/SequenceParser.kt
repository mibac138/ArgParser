package com.github.mibac138.argparser

import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.readUntilSpace
import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 05-04-2017.
 */
class SequenceParser : Parser {
    override fun getSupportedTypes(): Set<Class<*>> = setOf(String::class.java)

    override fun parse(input: ArgumentReader, syntax: SyntaxElement<*>): String {
        return input.readUntilSpace { it }
    }
}