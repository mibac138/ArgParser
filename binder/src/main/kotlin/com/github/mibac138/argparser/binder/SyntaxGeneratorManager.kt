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
import java.util.*
import kotlin.reflect.KParameter

/**
 * Created by mibac138 on 22-05-2017.
 */
class SyntaxGeneratorManager() : SyntaxGenerator {
    private val generators: MutableList<SyntaxGenerator> = ArrayList()

    constructor(vararg generators: SyntaxGenerator) : this() {
        this.generators += generators
    }

    fun add(generator: SyntaxGenerator) {
        generators += generator
    }

    fun remove(generator: SyntaxGenerator) {
        generators -= generator
    }

    override fun generate(dsl: SyntaxElementDSL<*>, param: KParameter) {
        for (generator in generators)
            generator.generate(dsl, param)
    }
}