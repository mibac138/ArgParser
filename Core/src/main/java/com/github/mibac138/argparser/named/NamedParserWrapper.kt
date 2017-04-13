package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.Parser
import com.github.mibac138.argparser.PrecheckedParser
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.SyntaxElement
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by mibac138 on 09-04-2017.
 */
class NamedParserWrapper(private val parser: Parser, private val name: String) : PrecheckedParser<Any>(), NamedParser {
    private val pattern: Pattern = "[^ ]*".toNamedPattern(name)

    override fun getSupportedTypes(): Set<Class<*>>
            = parser.getSupportedTypes()

    override fun getSupportedNames(): Set<String>
            = setOf(name)

    override fun getPattern(): Pattern
            = pattern

    override fun parse(matcher: Matcher, element: SyntaxElement<*>): Any {
        return parser.parse(matcher.group(2)!!.asReader(), element)
    }
}

fun Parser.toNamedParser(name: String): NamedParser {
    return NamedParserWrapper(this, name)
}

fun String.toNamedPattern(name: String, flags: Int = 0): Pattern {
    return Pattern.compile("--($name)(?:=|: ?)($this+)", flags)
}