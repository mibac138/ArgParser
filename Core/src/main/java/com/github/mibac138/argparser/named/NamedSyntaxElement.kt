package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.syntax.SyntaxElement

interface NamedSyntaxElement<T> : SyntaxElement<T> {
    fun getName(): String
}