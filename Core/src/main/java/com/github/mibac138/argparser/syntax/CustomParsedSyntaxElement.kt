package com.github.mibac138.argparser.syntax

import com.github.mibac138.argparser.Parser

/**
 * Created by mibac138 on 06-04-2017.
 */
class CustomParsedSyntaxElement<T>(private val element: SyntaxElement<T>, private val parser: Parser) : CustomSyntaxElement<T> {
    constructor(outputType: Class<T>, defaultValue: T?, parser: Parser)
            : this(BasicSyntaxElement(outputType, defaultValue), parser)

    override fun isRequired(): Boolean
            = element.isRequired()

    override fun getDefaultValue(): T?
            = element.getDefaultValue()

    override fun getOutputType(): Class<T>
            = element.getOutputType()

    override fun getParser(): Parser
            = parser
}