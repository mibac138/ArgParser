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
 * Syntax generator consisting of other generators. Does nothing on it's own.
 * When running [generate] it invokes all of the added [generators].
 */
class SyntaxGeneratorContainer() : SyntaxGenerator {
    private val generators = mutableListOf<SyntaxGenerator>()

    constructor(generators: Iterable<SyntaxGenerator>) : this() {
        this.generators += generators
    }

    constructor(vararg generators: SyntaxGenerator) : this() {
        this.generators += generators
    }

    fun clear()
            = this.generators.clear()

    override fun generate(dsl: SyntaxElementDSL, param: KParameter) {
        for (generator in generators)
            generator.generate(dsl, param)
    }

    operator fun plus(generator: SyntaxGenerator): SyntaxGeneratorContainer
            = SyntaxGeneratorContainer(this, generator)

    operator fun minus(generator: SyntaxGenerator): SyntaxGeneratorContainer
            = SyntaxGeneratorContainer(generators.except(generator))


    operator fun plusAssign(generator: SyntaxGenerator) {
        this.generators.add(generator)
    }

    operator fun plusAssign(generators: Iterable<SyntaxGenerator>) {
        this.generators.addAll(generators)
    }

    operator fun minusAssign(generator: SyntaxGenerator) {
        this.generators.remove(generator)
    }

    operator fun minusAssign(generators: Iterable<SyntaxGenerator>) {
        this.generators.removeAll(generators)
    }

    private fun <T> List<T>.except(element: T): List<T>
            = this.filterTo(ArrayList(((size - 1) / 0.75).toInt()), { it != element })
}