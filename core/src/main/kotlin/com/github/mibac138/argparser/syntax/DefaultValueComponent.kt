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

package com.github.mibac138.argparser.syntax

import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL
import kotlin.reflect.KParameter

/**
 * Makes the parser return you your defined default value instead of you doing some if-null checks and doing it yourself
 *
 * This is basically a port of Kotlin's [optional](https://kotlinlang.org/docs/reference/functions.html#default-arguments) [KParameter]s.
 */
data class DefaultValueComponent(val defaultValue: Any?) : SyntaxComponent {
    override val id = DefaultValueComponent::class.java
}

val SyntaxElement?.defaultValue: Any?
    get() = this?.get(DefaultValueComponent::class.java)?.defaultValue

var SyntaxElementDSL.defaultValue by SyntaxDSLComponentProperty<Any?, DefaultValueComponent>(DefaultValueComponent::class.java,
        { DefaultValueComponent(this) },
        { this?.defaultValue })

inline fun SyntaxElementDSL.defaultValue(init: SyntaxElementDSL.() -> Any?) = apply {
    defaultValue = init()
}