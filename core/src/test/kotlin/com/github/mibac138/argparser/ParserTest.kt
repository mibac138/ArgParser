package com.github.mibac138.argparser

import com.github.mibac138.argparser.syntax.DefaultValueComponent
import com.github.mibac138.argparser.syntax.SyntaxComponent
import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 06-04-2017.
 */
abstract class ParserTest {
    protected fun <T> defElement(default: T): SyntaxElement {
        return object : SyntaxElement {
            override val required: Boolean
                get() = false
            override val outputType: Class<T>
                get() = TODO()

            @Suppress("UNCHECKED_CAST")
            override fun <T : SyntaxComponent> get(clazz: Class<T>): T? =
                    if (clazz == DefaultValueComponent::class.java) DefaultValueComponent(default) as T?
                    else null
        }
    }

    protected fun reqElement(): SyntaxElement {
        return object : SyntaxElement {
            override val required: Boolean
                get() = true

            override val outputType: Class<Any>
                get() = TODO()

            override fun <T : SyntaxComponent> get(clazz: Class<T>): T? = null
        }
    }
}