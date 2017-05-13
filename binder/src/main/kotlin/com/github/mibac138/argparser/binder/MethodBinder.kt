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
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter

/**
 * Created by mibac138 on 10-05-2017.
 */
object MethodBinder {
    @JvmStatic
    fun bindMethod(owner: Any, name: String): BoundMethod? {
        val func = owner::class.declaredMemberFunctions.firstOrNull { it.name == name } ?: return null

        return bindMethod(func, owner)
    }

    @JvmStatic
    fun bindMethod(method: KCallable<*>, owner: Any?): BoundMethod {
        if ((method.instanceParameter != null || method.extensionReceiverParameter != null)
                && owner == null)
            throw IllegalArgumentException("Method requires instance variable or extension receiver (owner) but it's null")
        return CallableBoundMethod(method, owner)
    }

    @JvmStatic
    fun bindMethod(owner: Any, method: Method): BoundMethod {
        return ReflectionBoundMethod(owner, method)
    }
}