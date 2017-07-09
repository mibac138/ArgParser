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
    @JvmStatic
    val generator = SyntaxGeneratorManager(
            NameSyntaxGenerator(),
            KotlinDefaultValueSyntaxGenerator()/*,
            Disabled by default as it might cause
            unexpected results
            ParamNameSyntaxGenerator()*/
    )

    @JvmStatic
    @JvmOverloads
    fun bindMethod(owner: Any, name: String, vararg defaultValues: Any? = emptyArray()): BoundMethod? {
        val func = owner::class.java.declaredMethods.firstOrNull { it.name == name } ?: return null

        return bindMethod(owner, func, defaultValues)
    }

    @JvmStatic
    fun bindMethod(method: KCallable<*>, owner: Any? = null): BoundMethod
            = CallableBoundMethod(method, owner, generator)

    @JvmStatic
    @JvmOverloads
    fun bindMethod(owner: Any, method: Method, vararg defaultValues: Any? = emptyArray()): BoundMethod
            = CallableBoundMethod(method.kotlinFunction!!, owner,
            SyntaxGeneratorManager(generator, JavaDefaultValueSyntaxGenerator(defaultValues)))
}