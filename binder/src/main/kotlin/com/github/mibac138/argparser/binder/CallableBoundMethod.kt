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

import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.dsl.SyntaxContainerDSL
import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL
import java.util.*
import kotlin.collections.set
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction

/**
 * Kotlin's reflection based bound method (works for Java too*)
 *
 * *if [kotlinFunction] works (basically method can't be synthetic)
 *
 *
 * @constructor
 *
 * Example [method] that *won't* work
 * ```
 * Object::toString
 * ```
 * Example [method] that will work (the function reference
 * is on a instance, not on a class as in the previous example)
 * ```
 * Object()::toString
 * ```
 *
 * @param method the method to bound
 * @param generator the [SyntaxGenerator]  you want to use when generating syntax
 *
 */
class CallableBoundMethod(override val method: KCallable<*>,
                          generator: SyntaxGenerator = MethodBinder.generator
                         ) : BoundMethod {
    override val syntax: SyntaxElement
    private val syntaxToParamMap: Map<SyntaxElement, KParameter>

    init {
        if ((method.instanceParameter ?: method.extensionReceiverParameter) != null)
            throw IllegalArgumentException(
                    "CallableBoundMethod doesn't accept methods with instance (or extension) params")

        val builder = SyntaxContainerDSL(Any::class.java)
        val mutableSyntaxToParamMap = IdentityHashMap<SyntaxElement, KParameter>()

        for (parameter in method.valueParameters) {
            val type = parameter.type.jvmErasure.javaObjectType
            val element = SyntaxElementDSL(type)

            generator.generate(element, parameter)

            val syntaxElement = element.build()
            mutableSyntaxToParamMap[syntaxElement] = parameter
            builder.add(syntaxElement)
        }

        syntax = builder.build()
        syntaxToParamMap = mutableSyntaxToParamMap
    }

    override fun invoke(parameters: Map<SyntaxElement, Any?>): Any?
            = method.callBy(mapSyntaxMapToParamMap(parameters))

    private fun mapSyntaxMapToParamMap(syntax: Map<SyntaxElement, Any?>): Map<KParameter, Any?>
            // filter allows optional parameters with null value to use default value instead
            = syntax.mapKeys {
        /* TODO some more meaningful exception */
        syntaxToParamMap[it.key]!!
    }.filter { !it.key.isOptional || it.value != null }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as CallableBoundMethod

        if (method != other.method) return false

        return true
    }

    override fun hashCode(): Int {
        return method.hashCode()
    }

    override fun toString(): String {
        return "CallableBoundMethod(method=$method, syntax=$syntax)"
    }
}