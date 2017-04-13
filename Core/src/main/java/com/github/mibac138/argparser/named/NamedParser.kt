package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.Parser

interface NamedParser : Parser {
    fun getSupportedNames(): Set<String>
}