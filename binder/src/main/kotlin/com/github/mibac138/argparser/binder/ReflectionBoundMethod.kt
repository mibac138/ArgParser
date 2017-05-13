/*
 * Copyright (c) 2017 Michał Bączkowski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.binder.Arg.NO_NAME
import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.dsl.SyntaxContainerDSL
import com.github.mibac138.argparser.syntax.dsl.element
import java.lang.reflect.Method

/**
 * Java's reflection based bound method
 */
class ReflectionBoundMethod(private val owner: Any, private val method: Method) : BoundMethod {
    override val syntax: SyntaxElement<*>

    init {
        val builder = SyntaxContainerDSL(Any::class.java)

        for (i in 0 until method.parameterCount) {
            val arg = findAnnotation(method.parameterAnnotations[i])
            val name = arg?.name ?: Arg.NO_NAME
            val type = method.parameterTypes[i].boxClass()

            if (name == NO_NAME || name.isEmpty())
                builder.element(type)
            else
                builder.element(type, { name { name } })
        }

        syntax = builder.build()
    }

    private fun Class<*>.boxClass(): Class<*> {
        if (this.isPrimitive)
            return java.lang.reflect.Array.get(java.lang.reflect.Array.newInstance(this, 1), 0).javaClass
        else return this
    }

    override fun invoke(vararg parameters: Any?): Any?
            = method.invoke(owner, *parameters)

    private fun findAnnotation(array: Array<Annotation>): Arg?
            = array.firstOrNull { it is Arg } as Arg?

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ReflectionBoundMethod

        if (owner != other.owner) return false
        if (method != other.method) return false

        return true
    }

    override fun hashCode(): Int {
        var result = owner.hashCode()
        result = 31 * result + method.hashCode()
        return result
    }

    override fun toString(): String {
        return "ReflectionBoundMethod(owner=$owner, method=$method, syntax=$syntax)"
    }
}