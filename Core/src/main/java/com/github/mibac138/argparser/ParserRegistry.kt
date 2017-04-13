package com.github.mibac138.argparser

import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 05-04-2017.
 */
interface ParserRegistry : Parser {
    override fun getSupportedTypes(): Set<Class<*>>
    override fun parse(input: ArgumentReader, syntax: SyntaxElement<*>): Any

    fun registerParser(parser: Parser)
    fun removeParser(parser: Parser)
}