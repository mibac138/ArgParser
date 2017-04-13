package com.github.mibac138.argparser.exception

/**
 * Created by mibac138 on 05-04-2017.
 *
 * Usually means the input didn't match parser's syntax
 */
class ParserInvalidInputException : ParserException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}