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

@file:JvmName("SyntaxUtil")

package com.github.mibac138.argparser.syntax

import com.sun.xml.internal.ws.api.addressing.AddressingVersion.isRequired
import java.util.*

/**
 * Syntax element consisting of other nested syntax elements
 */
interface SyntaxContainer<T> : SyntaxElement<T> {
    val content: List<SyntaxElement<*>>
}

interface SyntaxElement<T> {

    /**
     * Indicate whether this element is required.
     * If not a parser *might* return [getDefaultValue]
     */
    val required: Boolean

    /**
     * Return element's default value.
     * Must not be null if [isRequired] is true,
     * otherwise might be any value
     */
    val defaultValue: T?

    /**
     * Returns the required object type for this element
     */
    val outputType: Class<T>

    fun <T : SyntaxComponent> get(clazz: Class<T>): T?
}

interface SyntaxComponent {
    val id: Class<out SyntaxComponent>
}

/**
 * Returns this SyntaxElement's size (meaning how big it actually is (does not count doubly nested elements))
 *
 * @return if element is:
 * - null: 0
 * - [SyntaxContainer]: content's size
 * - otherwise: 1
 */

fun SyntaxElement<*>?.getSize(): Int {
    if (this == null) return 0
    if (this is SyntaxContainer) return content.size
    return 1
}

fun SyntaxElement<*>?.getRequiredSize(): Int {
    if (this == null) return 0
    if (!required) return 0
    if (this is SyntaxContainer)
        return content.count { it.required }
    return 1
}

/**
 * Returns a iterator for given [SyntaxElement].
 * If it's a [SyntaxContainer] the iterator iterates over it's content.
 * If it's null the iterator is empty.
 * Otherwise returns a [SingleSyntaxElementIterator]
 *
 */
operator fun SyntaxElement<*>?.iterator(): Iterator<SyntaxElement<*>> {
    if (this == null || getSize() == 0)
        return Collections.emptyIterator()
    if (this is SyntaxContainer)
        return content.iterator()
    return SingleSyntaxElementIterator(this)
}
