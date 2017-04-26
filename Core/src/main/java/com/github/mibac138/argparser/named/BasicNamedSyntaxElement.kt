package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.syntax.BasicSyntaxElement
import com.github.mibac138.argparser.syntax.SyntaxBuilder
import com.github.mibac138.argparser.syntax.SyntaxElement

class BasicNamedSyntaxElement<T>(private val name: String, private val syntaxElement: SyntaxElement<T>) : NamedSyntaxElement<T> {
    constructor(name: String, type: Class<T>, defaultValue: T?) : this(name, BasicSyntaxElement(type, defaultValue))

    override fun isRequired(): Boolean
            = syntaxElement.isRequired()

    override fun getDefaultValue(): T?
            = syntaxElement.getDefaultValue()

    override fun getOutputType(): Class<T>
            = syntaxElement.getOutputType()

    override fun getName(): String
            = name
}


fun SyntaxBuilder.appendNamedType(type: Class<*>, name: String): SyntaxBuilder {
    return append(BasicNamedSyntaxElement(name, type, null))
}

fun <T> SyntaxBuilder.appendNamedOptionalType(type: Class<T>, name: String, defaultValue: T?): SyntaxBuilder {
    return append(BasicNamedSyntaxElement(name, type, defaultValue))
}