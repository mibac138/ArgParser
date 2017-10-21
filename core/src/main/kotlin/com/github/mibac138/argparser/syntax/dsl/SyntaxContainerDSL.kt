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


@file:JvmName("SyntaxContainerDSLCreator")

package com.github.mibac138.argparser.syntax.dsl

import com.github.mibac138.argparser.syntax.*
import java.util.function.Consumer

fun SyntaxContainerDSL(): SyntaxContainerDSL<*> = SyntaxContainerDSL(Any::class.java)

class SyntaxContainerDSL<T>(type: Class<T>) : SyntaxElementDSL(type) {
    var elements: MutableList<SyntaxElement> = ArrayList()

    fun add(element: SyntaxElement) {
        elements.add(element)
    }

    fun add(dsl: SyntaxElementDSL) {
        add(dsl.build())
    }

    fun addAll(elements: Collection<SyntaxElement>) {
        this.elements.addAll(elements)
    }

    inline fun add(init: SyntaxContainerDSL<T>.() -> SyntaxElement) = apply { elements.add(init()) }
    inline fun addAll(init: SyntaxContainerDSL<T>.() -> Collection<SyntaxElement>) = apply { elements.addAll(init()) }

    override fun build(): SyntaxContainer {
        return SyntaxContainerImpl(super.build(), elements)
    }
}


fun syntaxContainer(): SyntaxContainer
        = EmptySyntaxContainer

fun syntaxContainer(init: SyntaxContainerDSL<*>.() -> Unit): SyntaxContainer {
    val element = SyntaxContainerDSL(Any::class.java)
    element.init()
    return element.build()
}

fun syntaxContainer(init: Consumer<SyntaxContainerDSL<*>>): SyntaxContainer {
    val element = SyntaxContainerDSL(Any::class.java)
    init.accept(element)
    return element.build()
}

fun <T> syntaxContainer(type: Class<T>): SyntaxContainer
        = SyntaxContainerDSL(type).build()

fun <T> syntaxContainer(type: Class<T>, init: SyntaxContainerDSL<T>.() -> Unit): SyntaxContainer {
    val element = SyntaxContainerDSL(type)
    element.init()
    return element.build()
}

fun <T> syntaxContainer(type: Class<T>, init: Consumer<SyntaxContainerDSL<T>>): SyntaxContainer {
    val element = SyntaxContainerDSL(type)
    init.accept(element)
    return element.build()
}


fun <T> SyntaxContainerDSL<*>.element(type: Class<T>) = apply {
    elements.add(syntaxElement(type, this))
}

fun <T> SyntaxContainerDSL<*>.element(type: Class<T>, init: SyntaxElementDSL.() -> Unit) = apply {
    elements.add(syntaxElement(type, this, init))
}

fun <T> SyntaxContainerDSL<*>.element(type: Class<T>, init: Consumer<SyntaxElementDSL>) = apply {
    elements.add(syntaxElement(type, this, init))
}

/**
 * Works like [syntaxElement] with `type` and `parent` but this one is preferred as it
 * will automatically add the element to the syntax container as soon as it's built
 */
fun <T> SyntaxContainerDSL<*>.elementDsl(type: Class<T>): SyntaxElementDSL =
        ChildSyntaxElementDSL(type, this)


private class ChildSyntaxElementDSL(type: Class<*>, parent: SyntaxContainerDSL<*>) : SyntaxElementDSL(type, parent) {
    override fun build(): SyntaxElement {
        val element = super.build()
        parent!!.add(element)
        return element
    }
}