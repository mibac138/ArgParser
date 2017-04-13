package com.github.mibac138.argparser

import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 05-04-2017.
 */
class IntParser : Parser {
    override fun getSupportedTypes(): Set<Class<*>> = setOf(Int::class.java)

    /**
     * Reads text until hits space and tries to parse it as a [Int] then.
     * In case it fails text from [input] is reverted
     *
     * @throws NumberFormatException
     */
    override fun parse(input: ArgumentReader, syntax: SyntaxElement<*>): Int {
        return input.readUntilSpaceOrDefault(syntax, String::toInt)
    }
}