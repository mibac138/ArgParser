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
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class SyntaxDSLComponentProperty<T, COMPONENT : SyntaxComponent>(
        protected val componentType: Class<COMPONENT>,
        protected var toComponent: T?.() -> COMPONENT?,
        protected var toValue: COMPONENT?.() -> T?,
        protected val allowReassigning: Boolean = false,
        /**
         * If false values must be unique across the parent syntax element or the parent element and it's [children][SyntaxContainer.content]
         * if it's a [container][SyntaxContainer]
         */
        protected val allowDuplicates: Boolean = true
                                                                     ) : ReadWriteProperty<SyntaxElementDSL, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: SyntaxElementDSL, property: KProperty<*>)
            = (thisRef.components.firstOrNull { componentType.isInstance(it) } as COMPONENT?).toValue()

    override fun setValue(thisRef: SyntaxElementDSL, property: KProperty<*>, value: T?) {
        if (allowReassigning)
            thisRef.components.removeIf { componentType.isInstance(it) }
        else if (thisRef.components.firstOrNull { componentType.isInstance(it) } != null)
            throw IllegalStateException("Can't reassign ${componentType.simpleName}'s value")

        if (!allowDuplicates && value != null && thisRef.parent?.let { it.elements.any { it.index == value } } == true)
            throw IllegalArgumentException(
                    "There can't be two elements with the same component ($componentType) value ($value)")


        value.toComponent()?.let {
            thisRef.components.add(it)
        }
    }
}