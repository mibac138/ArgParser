package com.github.mibac138.argparser.syntax

/**
 * Created by mibac138 on 06-04-2017.
 */
class EmptySyntax : SyntaxContainer<Any> {
    override fun getContent(): List<SyntaxElement<*>>
            = emptyList()

    override fun isRequired(): Boolean
            = false

    override fun getDefaultValue(): Any?
            = null

    override fun getOutputType(): Class<Any>
            = Any::class.java
}