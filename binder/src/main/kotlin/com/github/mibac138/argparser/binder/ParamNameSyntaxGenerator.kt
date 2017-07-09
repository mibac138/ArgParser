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

import com.github.mibac138.argparser.named.NameComponent
import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL
import kotlin.reflect.KParameter

/**
 * Generates syntax based on [KParameter]'s name.
 * Works as a last resort option (generates syntax ([NameComponent])
 * only if it hasn't been already generated)
 */
class ParamNameSyntaxGenerator : SyntaxGenerator {
    private val pattern = Regex("arg\\d+")

    override fun generate(dsl: SyntaxElementDSL, param: KParameter) {
        // Check first as this is intended to be a last resort name generator
        val paramName = param.name ?: return

        // JVM's default param names are argN (arg0, arg1, etc)
        // (Oracle JDK doesn't save param names, don't know about other JDKs)
        // we probably don't want to use the default ones
        if (dsl.name == null && !pattern.matches(paramName))
            dsl.name = paramName
    }
}