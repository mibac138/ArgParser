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

/**
 * Created by mibac138 on 05-05-2017.
 */
internal class SyntaxElementImpl<T> : SyntaxElement {
    override val outputType: Class<T>
    override val required: Boolean
    private val components: Map<Class<out SyntaxComponent>, SyntaxComponent>


    @JvmOverloads
    constructor(outputType: Class<T>, required: Boolean = true) {
        this.outputType = outputType
        this.required = required
        this.components = emptyMap()
    }

    /*@JvmOverloads
    constructor(outputType: Class<T>, required: Boolean = true, vararg components: SyntaxComponent) {
        this.outputType = outputType
        this.required = required
        this.components = HashMap(components.size)
        components.forEach {
            checkInstance(it)
            this.components[it.id] = it
        }
    }*/

    @JvmOverloads
    constructor(outputType: Class<T>, required: Boolean = true, components: Collection<SyntaxComponent>) {
        this.outputType = outputType
        this.required = required
        this.components = HashMap(components.size)
        components.forEach {
            checkInstance(it)
            this.components[it.id] = it
        }
    }

    private fun checkInstance(component: SyntaxComponent) {
        if (!component.id.isInstance(component))
            throw IllegalArgumentException("SyntaxComponent's id must be a instance (or superclass, etc) of it")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : SyntaxComponent> get(clazz: Class<T>): T? {
        return components[clazz] as T?
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as SyntaxElementImpl<*>

        if (outputType != other.outputType) return false
        if (defaultValue != other.defaultValue) return false
        if (components != other.components) return false

        return true
    }

    override fun hashCode(): Int {
        var result = outputType.hashCode()
        result = 31 * result + (defaultValue?.hashCode() ?: 0)
        result = 31 * result + components.hashCode()
        return result
    }

    override fun toString(): String =
            "SyntaxElementImpl(outputType=$outputType, defaultValue=$defaultValue, components=$components)"
}