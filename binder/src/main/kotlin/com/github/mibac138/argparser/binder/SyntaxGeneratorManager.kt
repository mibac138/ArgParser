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
class SyntaxGeneratorManager() : SyntaxGenerator {
    private val generators = mutableListOf<SyntaxGenerator>()

    constructor(generators: Iterable<SyntaxGenerator>) : this() {
        this.generators += generators
    }

    constructor(vararg generators: SyntaxGenerator) : this() {
        this.generators += generators
    }

    fun add(vararg add: SyntaxGenerator) {
        generators += add
    }

    fun add(add: Iterable<SyntaxGenerator>) {
        generators += add
    }

    fun remove(vararg remove: SyntaxGenerator) {
        generators -= remove
    }

    fun remove(remove: Iterable<SyntaxGenerator>) {
        generators -= remove
    }

    fun clear() {
        generators.clear()
    }

    override fun generate(dsl: SyntaxElementDSL, param: KParameter) {
        for (generator in generators)
            generator.generate(dsl, param)
    }


    operator fun plus(generator: SyntaxGenerator): SyntaxGeneratorManager
            = SyntaxGeneratorManager(this, generator)

    operator fun plusAssign(generator: SyntaxGenerator)
            = add(generator)

    operator fun plusAssign(generators: Iterable<SyntaxGenerator>)
            = add(generators)


    operator fun minus(generator: SyntaxGenerator): SyntaxGeneratorManager
            = SyntaxGeneratorManager(generators.except(generator))

    operator fun minusAssign(generator: SyntaxGenerator)
            = remove(generator)


    operator fun minusAssign(generators: Iterable<SyntaxGenerator>)
            = remove(generators)


    private fun <T> List<T>.except(element: T): List<T>
            = this.filterTo(ArrayList(this.size.div(0.75).toInt()), { it != element })
}