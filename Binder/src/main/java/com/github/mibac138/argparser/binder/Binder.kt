package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.binder.Arg.NO_NAME
import com.github.mibac138.argparser.named.appendNamedType
import com.github.mibac138.argparser.syntax.SyntaxBuilder
import com.github.mibac138.argparser.syntax.SyntaxElement
import java.lang.reflect.Method

/**
 * Created by mibac138 on 13-04-2017.
 */
object Binder {
    @JvmStatic
    fun bind(owner: Any, method: Method): Binding {
        return bind(ReflectionBoundMethod(owner, method))
    }

    @JvmStatic
    fun bind(method: BoundMethod): Binding {
        return Binding(method)
    }

    class ReflectionBoundMethod(private val owner: Any, private val method: Method) : BoundMethod {
        private val syntax: SyntaxElement<*>

        init {
            val builder = SyntaxBuilder.start()

            for (i in 0 until method.parameterCount) {
                val arg = findAnnotation(method.parameterAnnotations[i]) ?: throw NonArgParameterException()
                val type = method.parameterTypes[i]
                val name = arg.name

                if (name == NO_NAME || arg.name.isEmpty())
                    builder.appendType(type)
                else
                    builder.appendNamedType(type, name)
            }

            syntax = builder.build()
        }

        override fun invoke(parameters: Array<*>): Any?
                = method.invoke(owner, *parameters)

        override fun getSyntax(): SyntaxElement<*>
                = syntax

        private fun findAnnotation(array: Array<Annotation>): Arg?
                = array.firstOrNull { it is Arg } as Arg?
    }
}