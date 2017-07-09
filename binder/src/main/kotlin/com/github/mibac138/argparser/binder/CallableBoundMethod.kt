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
import java.lang.reflect.Array.get
import java.lang.reflect.Array.newInstance
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.jvm.kotlinFunction

/**
 * Kotlin's reflection based bound method (works for Java too*)
 *
 * *if [kotlinFunction] works (basically method can't be synthetic)
 *
 *
 * @constructor
 *
 * Example [method] requiring [owner]
 * ```
 * Object::toString
 * ```
 * Example [method] *not* requiring [owner] (the function reference
 * is on a instance, not on a class as in the previous example)
 * ```
 * Object()::toString
 * ```
 *
 *
 * @param method the method to bound
 * @param owner instance parameter or extension receiver parameter. Required only if the [method] requires it
 * @param generator the [SyntaxGenerator]  you want to use when generating syntax
 *
 */
class CallableBoundMethod(override val method: KCallable<*>, private val owner: Any? = null, generator: SyntaxGenerator = MethodBinder.generator) : BoundMethod {
    override val syntax: SyntaxElement
    private val syntaxToParamMap: Map<SyntaxElement, KParameter>
    private val instanceParam: KParameter? = method.instanceParameter ?: method.extensionReceiverParameter

    init {
        verifyInstanceParam()

        val builder = SyntaxContainerDSL(Any::class.java)
        val mutableSyntaxToParamMap = mutableMapOf<SyntaxElement, KParameter>()

        for (parameter in method.valueParameters) {
            if (parameter.index == 0 && parameter.kind == KParameter.Kind.INSTANCE) continue

            val type = parameter.type.jvmErasure.java.boxClass()
            val element = SyntaxElementDSL(type)

            generator.generate(element, parameter)

            val syntaxElement = element.build()
            mutableSyntaxToParamMap[syntaxElement] = parameter
            builder.add(syntaxElement)
        }

        syntax = builder.build()
        syntaxToParamMap = mutableSyntaxToParamMap
    }

    private fun verifyInstanceParam() {
        if (instanceParam != null) {
            if (owner == null)
                throw IllegalArgumentException("Method requires instance variable or extension receiver but it's null")

            val paramTypeClass = instanceParam.type.jvmErasure
            val ownerClass = owner::class
            if (!ownerClass.isSubclassOf(paramTypeClass))
                throw IllegalArgumentException("Provided owner (${ownerClass.jvmName}) isn't an subclass of required " +
                        "instance param's value's class (${paramTypeClass.jvmName})")
        }
    }

    private fun Class<*>.boxClass(): Class<*> {
        if (this.isPrimitive)
            return get(newInstance(this, 1), 0).javaClass
        else return this
    }

    override fun invoke(parameters: Map<SyntaxElement, Any?>): Any? {
        var paramMap = mapSyntaxMapToParamMap(parameters)

        if (instanceParam != null)
            paramMap = paramMap.plus(instanceParam to owner)

        return method.callBy(paramMap)
    }

    private fun mapSyntaxMapToParamMap(syntax: Map<SyntaxElement, Any?>): Map<KParameter, Any?>
            // filter allows optional parameters with null value to use default value instead
            = syntax.mapKeys { syntaxToParamMap[it.key]!! }.filter { !it.key.isOptional || it.value != null }

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