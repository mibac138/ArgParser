package com.github.mibac138.argparser.syntax

/**
 * Created by mibac138 on 06-04-2017.
 */
class BasicParserSyntax<T>(private val syntax: List<SyntaxElement<*>>, outputType: Class<T>, value: T?) : BasicSyntaxElement<T>(outputType, value), SyntaxContainer<T> {
    override fun getContent(): List<SyntaxElement<*>>
            = syntax

    override fun toString(): String {
        val builder: StringBuilder = StringBuilder()
        if (syntax.isEmpty())
            return ""

        for (element in syntax) {
            builder.appendArgument(element).append(' ')
        }

        return builder.dropLast(1).toString()
    }

    private fun StringBuilder.appendArgument(element: SyntaxElement<*>): StringBuilder {
        if (element.isRequired())
            append('<')
        else
            append('[')

        append(element.getOutputType().simpleName)

        if (!element.isRequired() && element.getDefaultValue() != null)
            append('|').append(element.getDefaultValue())

        if (element.isRequired())
            append('>')
        else
            append(']')

        return this
    }
}