package com.github.mibac138.argparser.syntax

/**
 * Created by mibac138 on 09-04-2017.
 */

class SingleSyntaxElementIterator(private val syntax: SyntaxElement<*>) : Iterator<SyntaxElement<*>> {
    private var iterated: Boolean = false

    override fun hasNext(): Boolean
            = iterated

    override fun next(): SyntaxElement<*> {
        if (iterated)
            throw IndexOutOfBoundsException()

        return syntax
    }
}

fun SyntaxElement<*>.iterator(): Iterator<SyntaxElement<*>> {
    if (this is SyntaxContainer)
        return this.getContent().iterator()
    return SingleSyntaxElementIterator(this)
}