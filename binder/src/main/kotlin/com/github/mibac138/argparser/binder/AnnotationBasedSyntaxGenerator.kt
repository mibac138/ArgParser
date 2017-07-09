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

import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL
import kotlin.reflect.KParameter

/**
 * A little convenience class. Does the param checking for you
 */
abstract class AnnotationBasedSyntaxGenerator<in T : Annotation>(private val type: Class<T>) : SyntaxGenerator {
    override final fun generate(dsl: SyntaxElementDSL, param: KParameter) {
        @Suppress("UNCHECKED_CAST")
        val annotation = param.annotations.firstOrNull { type.isInstance(it) } as T? ?: return

        generate(dsl, annotation)
    }

    /**
     * This is called only when the annotation is found. If it's not then the [AnnotationBasedSyntaxGenerator] just
     * doesn't do anything
     */
    abstract fun generate(dsl: SyntaxElementDSL, annotation: T)
}