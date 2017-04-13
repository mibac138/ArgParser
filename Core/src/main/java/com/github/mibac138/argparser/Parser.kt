package com.github.mibac138.argparser

import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.readUntilSpace
import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 05-04-2017.
 */
interface Parser {
    fun getSupportedTypes(): Set<Class<*>>
    fun parse(input: ArgumentReader, syntax: SyntaxElement<*>): Any
}

fun <T> parseOrDefault(syntax: SyntaxElement<*>, action: () -> T): T {
    try {
        return action()
    } catch (e: Exception) {
        @Suppress("UNCHECKED_CAST")
        if (!syntax.isRequired() && syntax.getDefaultValue() != null)
            return syntax.getDefaultValue() as T
        else
            throw e
    }
}


fun <T> ArgumentReader.readUntilSpaceOrDefault(syntax: SyntaxElement<*>, action: (String) -> T): T {
    mark()
    try {
        val output = action(readUntilSpace())
        removeMark()

        return output
    } catch (e: Exception) {
        if (!syntax.isRequired() && syntax.getDefaultValue() != null) {
            removeMark()
            @Suppress("UNCHECKED_CAST")
            return syntax.getDefaultValue() as T
        }

        reset()
        throw e
    }
}