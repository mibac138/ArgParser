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


package com.github.mibac138.argparser.syntax.dsl

import com.github.mibac138.argparser.syntax.EmptySyntaxContainer
import com.github.mibac138.argparser.syntax.SyntaxContainer
import com.github.mibac138.argparser.syntax.SyntaxContainerImpl
import com.github.mibac138.argparser.syntax.SyntaxElement
import java.util.function.Consumer

fun SyntaxContainerDSL(): SyntaxContainerDSL<*> = SyntaxContainerDSL(Any::class.java)

class SyntaxContainerDSL<T>(type: Class<T>) : SyntaxElementDSL<T>(type) {
    var elements: MutableList<SyntaxElement<*>> = ArrayList()

    fun add(element: SyntaxElement<*>) {
        elements.add(element)
    }

    fun addAll(elements: Collection<SyntaxElement<*>>) {
        this.elements.addAll(elements)
    }

    inline fun add(init: SyntaxContainerDSL<T>.() -> SyntaxElement<*>) = apply { elements.add(init()) }
    inline fun addAll(init: SyntaxContainerDSL<T>.() -> Collection<SyntaxElement<*>>) = apply { elements.addAll(init()) }

    override fun build(): SyntaxContainer<T> {
        return SyntaxContainerImpl(super.build(), elements)
    }
}


fun syntaxContainer(): SyntaxContainer<*>
        = EmptySyntaxContainer

fun syntaxContainer(init: SyntaxContainerDSL<*>.() -> Unit = {}): SyntaxContainer<*> {
    val element = SyntaxContainerDSL(Any::class.java)
    element.init()
    return element.build()
}

fun syntaxContainer(init: Consumer<SyntaxContainerDSL<*>> = Consumer {}): SyntaxContainer<*> {
    val element = SyntaxContainerDSL(Any::class.java)
    init.accept(element)
    return element.build()
}

fun <T> syntaxContainer(type: Class<T>, init: SyntaxContainerDSL<T>.() -> Unit = {}): SyntaxContainer<T> {
    val element = SyntaxContainerDSL(type)
    element.init()
    return element.build()
}

fun <T> syntaxContainer(type: Class<T>, init: Consumer<SyntaxContainerDSL<T>> = Consumer {}): SyntaxContainer<*> {
    val element = SyntaxContainerDSL(type)
    init.accept(element)
    return element.build()
}

@JvmOverloads
fun <T> SyntaxContainerDSL<*>.element(type: Class<T>, init: SyntaxElementDSL<T>.() -> Unit = {}) = apply {
    elements.add(syntaxElement(type, init))
}