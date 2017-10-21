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

import com.github.mibac138.argparser.syntax.defaultValue
import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL
import java.util.*
import kotlin.reflect.KParameter

/**
 * Basically a port of Kotlin's [optional](https://kotlinlang.org/docs/reference/functions.html#default-arguments) [KParameter]s.
 * When setting default values parsers *should* return them if they couldn't parse the input
 *
 * @constructor
 * @param defaultValues the array of default values.
 *        Must be in the same order as the method's parameters.
 *        In case you, e.g. want to have a default value for the
 *        2nd parameter but not for the 1st then pass [NO_DEFAULT_VALUE]
 *        as the 1st default value
 */
class JavaDefaultValueSyntaxGenerator(private vararg val defaultValues: Any?) : SyntaxGenerator {

    /**
     * Use this when you don't want the param to have a default value
     * but want a next param (or any after this one) to have it
     */
    @Suppress("ClassName")
    object NO_DEFAULT_VALUE

    override fun generate(dsl: SyntaxElementDSL, param: KParameter) {
        if (param.index > defaultValues.size) return

        val defaultValue = defaultValues[param.index]

        if (defaultValue != NO_DEFAULT_VALUE) {
            dsl.defaultValue = defaultValue
            dsl.required = false
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as JavaDefaultValueSyntaxGenerator

        if (!Arrays.equals(defaultValues, other.defaultValues)) return false

        return true
    }

    override fun hashCode(): Int = Arrays.hashCode(defaultValues)

    override fun toString(): String = "JavaDefaultValueSyntaxGenerator(defaultValues=${Arrays.toString(defaultValues)})"
}