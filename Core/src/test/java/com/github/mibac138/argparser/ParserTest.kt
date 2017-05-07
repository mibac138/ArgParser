package com.github.mibac138.argparser

import com.github.mibac138.argparser.syntax.SyntaxComponent
import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 06-04-2017.
 */
abstract class ParserTest {
    protected fun <T> defElement(default: T): SyntaxElement<T> {
        return object : SyntaxElement<T> {
            override val required: Boolean
                get() = false
            override val defaultValue: T?
                get() = default
            override val outputType: Class<T>
                get() = TODO()

            override fun <T : SyntaxComponent> get(clazz: Class<T>): T? = null
        }
    }

    protected fun reqElement(): SyntaxElement<*> {
        return object : SyntaxElement<Any> {
            override val required: Boolean
                get() = true

            override val defaultValue: Any?
                get() = TODO()

            override val outputType: Class<Any>
                get() = TODO()

            override fun <T : SyntaxComponent> get(clazz: Class<T>): T? = null
        }
    }
}