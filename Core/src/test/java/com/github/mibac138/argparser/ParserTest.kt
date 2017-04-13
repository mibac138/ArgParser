package com.github.mibac138.argparser

import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 06-04-2017.
 */
abstract class ParserTest {
    protected fun <T> defElement(default: T): SyntaxElement<T> {
        return object : SyntaxElement<T> {
            override fun isRequired(): Boolean
                    = false

            override fun getDefaultValue(): T?
                    = default

            override fun getOutputType(): Class<T>
                    = TODO()
        }
    }

    protected fun reqElement(): SyntaxElement<*> {
        return object : SyntaxElement<Any> {
            override fun isRequired(): Boolean
                    = true

            override fun getDefaultValue(): Any?
                    = TODO()

            override fun getOutputType(): Class<Any>
                    = TODO()
        }
    }
}