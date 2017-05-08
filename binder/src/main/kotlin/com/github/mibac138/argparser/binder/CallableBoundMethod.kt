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

import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.dsl.SyntaxContainerDSL
import com.github.mibac138.argparser.syntax.dsl.element
import java.lang.reflect.Array.get
import java.lang.reflect.Array.newInstance
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

/**
 * Kotlin's reflection based bound method
 */
class CallableBoundMethod(private val function: KCallable<*>, private val owner: Any? = null) : BoundMethod {
    override val syntax: SyntaxElement<*>

    init {
        if ((function.instanceParameter != null || function.extensionReceiverParameter != null)
                && owner == null)
            throw IllegalArgumentException("Method requires instance variable or extension receiver but it's null")

        val builder = SyntaxContainerDSL(Any::class.java)

        for (parameter in function.valueParameters) {
            if (parameter.index == 0 && parameter.kind == KParameter.Kind.INSTANCE)
                continue
            val arg = findAnnotation(parameter.annotations)
            val name = arg?.name ?: Arg.NO_NAME
            val type = parameter.type.jvmErasure.java.boxClass()

            if (name == Arg.NO_NAME || name.isEmpty())
                builder.element(type)
            else
                builder.element(type, { name { name } })
        }

        syntax = builder.build()
    }

    private fun Class<*>.boxClass(): Class<*> {
        if (this.isPrimitive)
            return get(newInstance(this, 1), 0).javaClass
        else return this
    }

    override fun invoke(parameters: Array<*>): Any? {
        if (function.parameters[0].kind == KParameter.Kind.INSTANCE)
            return function.call(owner, *parameters)
        else
            return function.call(*parameters)
    }

    private fun findAnnotation(array: List<Annotation>): Arg?
            = array.firstOrNull { it is Arg } as? Arg

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as CallableBoundMethod

        if (function != other.function) return false

        return true
    }

    override fun hashCode(): Int {
        return function.hashCode()
    }

    override fun toString(): String {
        return "CallableBoundMethod(function=$function, syntax=$syntax)"
    }
}