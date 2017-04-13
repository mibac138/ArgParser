package com.github.mibac138.argparser.syntax

/**
 * Created by mibac138 on 05-04-2017.
 */
interface ParserSyntax : SyntaxElement<Array<*>> {

    fun getSyntax(): List<SyntaxElement<*>>
}