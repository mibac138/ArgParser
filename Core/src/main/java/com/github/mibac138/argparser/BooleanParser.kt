package com.github.mibac138.argparser

import com.github.mibac138.argparser.exception.ParserInvalidInputException
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 06-04-2017.
 */
class BooleanParser : Parser {
    override fun getSupportedTypes(): Set<Class<*>> = setOf(Boolean::class.java)

    private val VALUES = mapOf(
            "yes" to true,
            "y" to true,
            "true" to true,
            "t" to true,
            "1" to true,

            "no" to false,
            "n" to false,
            "false" to false,
            "f" to false,
            "0" to false
    )

    override fun parse(input: ArgumentReader, syntax: SyntaxElement<*>): Any {
        return input.readUntilSpaceOrDefault(syntax, {
            VALUES[it] ?: throw ParserInvalidInputException("Couldn't match text \"$it\". Valid values are $VALUES")
        })
    }
}