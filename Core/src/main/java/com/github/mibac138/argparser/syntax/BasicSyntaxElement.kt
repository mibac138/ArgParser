package com.github.mibac138.argparser.syntax

/**
 * Created by mibac138 on 06-04-2017.
 */
open class BasicSyntaxElement<T>(private val outputType: Class<T>, private val defaultValue: T?) : SyntaxElement<T> {
    override fun isRequired(): Boolean
            = defaultValue == null

    override fun getDefaultValue(): T?
            = if (isRequired()) defaultValue else null

    override fun getOutputType(): Class<T> {
        return outputType
    }
}