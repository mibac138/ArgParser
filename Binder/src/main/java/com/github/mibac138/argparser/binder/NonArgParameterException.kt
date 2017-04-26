package com.github.mibac138.argparser.binder

/**
 * Created by mibac138 on 14-04-2017.
 */
class NonArgParameterException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}
