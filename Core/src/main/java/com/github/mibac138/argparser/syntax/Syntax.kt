package com.github.mibac138.argparser.syntax

import com.github.mibac138.argparser.Parser

/**
 * Created by mibac138 on 09-04-2017.
 */

interface SyntaxContainer<T> : SyntaxElement<T> {
    fun getContent(): List<SyntaxElement<*>>
}

interface SyntaxElement<T> {
    fun isRequired(): Boolean
    fun getDefaultValue(): T?
    fun getOutputType(): Class<T>
}

interface CustomSyntaxElement<T> : SyntaxElement<T> {
    fun getParser(): Parser
}

fun SyntaxElement<*>.getSize(): Int {
    return if (this is SyntaxContainer) getContent().size else 1
}