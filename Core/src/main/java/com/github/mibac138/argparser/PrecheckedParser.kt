package com.github.mibac138.argparser

import com.github.mibac138.argparser.exception.ParserInternalException
import com.github.mibac138.argparser.exception.ParserInvalidInputException
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.skipSpaces
import com.github.mibac138.argparser.syntax.SyntaxElement
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by mibac138 on 05-04-2017.
 */
abstract class PrecheckedParser<out T : Any> : Parser {

    protected val NO_LENGTH: Int = -1

    /**
     * Returned value means how much more to read of input (NOT in total)
     */
    protected open fun getMatchingLength(alreadyRead: Int): Int = if (alreadyRead == NO_LENGTH) 25 else 25

    protected abstract fun getPattern(): Pattern

    protected abstract fun parse(matcher: Matcher, element: SyntaxElement<*>): T

    override fun parse(input: ArgumentReader, syntax: SyntaxElement<*>): T {
        input.mark()

        val output: Pair<Matcher, String>
        try {
            output = matchInput(input.skipSpaces())
        } catch(e: Exception) {
            input.reset()
            throw e
        }

        if (!output.first.find() || output.first.start() != 0) {
            input.reset()
            if (syntax.isRequired())
                throw ParserInvalidInputException("Pattern [${output.first.pattern()}] didn't match the input [${output.second}]")

            @Suppress("UNCHECKED_CAST")
            return syntax.getDefaultValue()!! as T
        }

        // Revert if matcher took too much text
        if (output.first.end() != output.second.length) {
            input.reset()
            input.skip(output.first.end())
        }

        try {
            val parsed = parse(output.first, syntax)
            input.removeMark()
            return parsed
        } catch (e: Exception) {
            input.reset()
            throw e
        }
    }

    private fun matchInput(input: ArgumentReader): Pair<Matcher, String> {
        if (input.getAvailableCount() == 0)
            throw ParserInternalException()

        var total = NO_LENGTH
        var read: String = ""
        val matcher: Matcher = getPattern().matcher("")
        var available = input.getAvailableCount()

        do {
            val length = getMatchingLength(total)
            total += length

            read += input.read(Math.min(length, available))
            matcher.reset(read)
            available = input.getAvailableCount()
        } while (!matcher.hitEnd() && available != 0)

        return Pair(matcher, read)
    }
}
