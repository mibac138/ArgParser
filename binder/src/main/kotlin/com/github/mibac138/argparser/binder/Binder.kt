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
import kotlin.reflect.full.functions
import kotlin.reflect.full.instanceParameter

/**
 * Created by mibac138 on 13-04-2017.
 */
object Binder {
    @JvmStatic
    fun bind(owner: Any): Map<String, Binding> {
        val functions = owner::class.declaredMemberFunctions
        val map = HashMap<String, Binding>(minOf(10, functions.size))

        for (func in owner::class.functions) {
            val annotation = func.annotations.firstOrNull { it is BindMethod } as BindMethod? ?:
                    continue

            map[annotation.name] = bind(func, owner)
        }

        return map
    }

    @JvmStatic
    fun bind(owner: Any, method: Method): Binding {
        return bind(ReflectionBoundMethod(owner, method))
    }

    @JvmStatic
    fun bind(method: KCallable<*>, owner: Any? = null): Binding {
        if ((method.instanceParameter != null || method.extensionReceiverParameter != null)
                && owner == null)
            throw IllegalArgumentException("Method requires instance variable (owner) but it's null")
        return bind(CallableBoundMethod(method, owner))
    }

    @JvmStatic
    fun bind(method: BoundMethod): Binding {
        return BindingImpl(method)
    }
}