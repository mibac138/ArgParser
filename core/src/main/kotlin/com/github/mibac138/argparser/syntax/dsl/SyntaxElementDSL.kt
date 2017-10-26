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

@file:JvmName("SyntaxElementDSLCreator")

package com.github.mibac138.argparser.syntax.dsl

import com.github.mibac138.argparser.named.name
import com.github.mibac138.argparser.syntax.*
import java.util.function.Consumer

/**
 * Created by mibac138 on 07-05-2017.
 */
@SyntaxDSL
open class SyntaxElementDSL(val type: Class<*>, val parent: SyntaxContainerDSL<*>? = null) {
    var required: Boolean = true
    // TODO shouldn't `components` be val?
    var components: MutableList<SyntaxComponent> = ArrayList()

    inline fun required(init: SyntaxElementDSL.() -> Boolean) = apply { required = init() }
    inline fun component(init: SyntaxElementDSL.() -> SyntaxComponent) = apply { components.add(init()) }
    inline fun components(init: SyntaxElementDSL.() -> Collection<SyntaxComponent>) = apply {
        components.addAll(init())
    }

    open fun build(): SyntaxElement {
        if (this.index == null && this.name == null)
            this.autoIndex()
        return SyntaxElementImpl(type, required, components)
    }
}

// Doesn't call SyntaxElementImpl directly because of
// autoIndexing (see SyntaxElementDSL.build)
fun syntaxElement(type: Class<*>): SyntaxElement
        = SyntaxElementDSL(type).build()

fun syntaxElement(type: Class<*>, init: SyntaxElementDSL.() -> Unit): SyntaxElement
        = syntaxElement(type, null, init)

fun syntaxElement(type: Class<*>, init: Consumer<SyntaxElementDSL>): SyntaxElement
        = syntaxElement(type, null, init)

fun syntaxElement(type: Class<*>,
                  parent: SyntaxContainerDSL<*>?,
                  init: SyntaxElementDSL.() -> Unit): SyntaxElement {
    val element = SyntaxElementDSL(type, parent)
    element.init()
    return element.build()
}

@JvmOverloads
fun syntaxElement(type: Class<*>,
                  parent: SyntaxContainerDSL<*>?,
                  init: Consumer<SyntaxElementDSL> = Consumer {}): SyntaxElement {
    val element = SyntaxElementDSL(type, parent)
    init.accept(element)
    return element.build()
}