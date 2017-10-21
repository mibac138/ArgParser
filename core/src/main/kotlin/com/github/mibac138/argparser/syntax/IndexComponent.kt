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
import kotlin.reflect.KProperty

data class IndexComponent(val index: Int) : SyntaxComponent {
    init {
        if (index < 0)
            throw IllegalArgumentException("Index must be greater than or equal 0")
    }

    override val id: Class<out SyntaxComponent> = IndexComponent::class.java
}

/**
 * Returns this element's [ParserComponent]'s parser or null
 */
val SyntaxElement?.index: Int?
    get() = this?.get(IndexComponent::class.java)?.index

/**
 * Use `index = <int >= 0>` to define a index. If you want to auto assign an index just write `autoIndex()`
 * (assigns index based on the highest one already assigned plus 1 or 0 if there are no indexed syntax elements yet)
 */
var SyntaxElementDSL.index: Int? by object : SyntaxDSLComponentProperty<Int?, IndexComponent>(
        IndexComponent::class.java,
        {
            this?.let {
                IndexComponent(it)
            }
        },
        { this?.index }) {

    override fun setValue(thisRef: SyntaxElementDSL, property: KProperty<*>, value: Int?) {
        if (value != null && thisRef.parent?.let { it.elements.any { it.index == value } } == true)
            throw IllegalArgumentException("There can't be two elements with the same index ($value)")


        super.setValue(thisRef, property, value)
    }
}


inline fun SyntaxElementDSL.index(init: SyntaxElementDSL.() -> Int) = apply {
    index = init()
}

fun SyntaxElementDSL.autoIndex() = apply {
    if (parent == null) {
        index = 0
        return@apply
    }

    val highestIndex = parent.elements.maxBy {
        it.index ?: -1
    }.index

    index = (highestIndex ?: -1) + 1
}

