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

import java.lang.reflect.Method
import kotlin.reflect.KCallable
import kotlin.reflect.jvm.kotlinFunction

/**
 * Created by mibac138 on 10-05-2017.
 */
object MethodBinder {
    /**
     * Default [SyntaxGenerator] used by [BoundMethod]s when generating syntax.
     */
    @JvmStatic
    val generator = SyntaxGeneratorManager(
            NameSyntaxGenerator(),
            KotlinDefaultValueSyntaxGenerator()/*,
            Disabled by default as it might cause
            unexpected results
            ParamNameSyntaxGenerator()*/
                                          )

    /**
     * Binds the given [method]
     *
     * @param owner required only when the method is instanceless (e.g. `String::substring`).
     *              When it has a instance bound though (e.g. `"Something"::substring`) the parameter is ignored
     */
    @JvmStatic
    fun bindMethod(method: KCallable<*>, owner: Any? = null): BoundMethod
            = CallableBoundMethod(method, owner, generator)

    /**
     * Binds the given [method]. Intended to be used with Java.
     *
     * @param defaultValues when input isn't specified this will be used by default (port of Kotlin's default parameters)
     */
    @JvmStatic
    @JvmOverloads
    fun bindMethod(owner: Any, method: Method, vararg defaultValues: Any? = emptyArray()): BoundMethod
            = CallableBoundMethod(method.kotlinFunction!!, owner,
                                  generator + JavaDefaultValueSyntaxGenerator(defaultValues))

    /**
     * Searches for a method with the given [name] inside that class's methods. Returns null on failure (method not found)
     * Intended to be used with Java.
     *
     * @param defaultValues when input isn't specified this will be used by default (port of Kotlin's default parameters)
     */
    @JvmStatic
    @JvmOverloads
    fun bindMethod(owner: Any, name: String, vararg defaultValues: Any? = emptyArray()): BoundMethod? {
        val func = owner::class.java.declaredMethods.firstOrNull { it.name == name } ?: return null

        return bindMethod(owner, func, defaultValues)
    }
}