package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 14-04-2017.
 */
@FunctionalInterface
interface BoundMethod {
    fun invoke(parameters: Array<*>): Any?
    fun getSyntax(): SyntaxElement<*>
}