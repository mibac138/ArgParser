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

@file:JvmName("ParserComponentHelper")

package com.github.mibac138.argparser.syntax

import com.github.mibac138.argparser.parser.Parser
import com.github.mibac138.argparser.syntax.dsl.SyntaxElementDSL

/**
 * Asks the processing [Parser] to use the specified [parser] to parse input for this [SyntaxElement]
 */
data class ParserComponent(val parser: Parser) : SyntaxComponent {
    override val id = ParserComponent::class.java
}

/**
 * Returns this element's [ParserComponent]'s parser or null
 */
val SyntaxElement?.parser: Parser?
    get() = this?.get(ParserComponent::class.java)?.parser


/**
 * Adds given parser upon creation. Note: there can be only one parser per syntax element and setting this multiple
 * times overwrites the previous value
 */
var SyntaxElementDSL.parser: Parser? by SyntaxDSLComponentProperty<Parser?, ParserComponent>(ParserComponent::class.java,
                                                                                             {
                                                                                                 this?.let {
                                                                                                     ParserComponent(it)
                                                                                                 }
                                                                                             },
                                                                                             { this?.parser })

/**
 * Adds given parser upon creation. Note: there can be only one parser per syntax element and setting this multiple
 * times overwrites the previous value
 * @see SyntaxElementDSL.parser
 */
inline fun SyntaxElementDSL.parser(init: SyntaxElementDSL.() -> Parser) = apply {
    parser = init()
}

