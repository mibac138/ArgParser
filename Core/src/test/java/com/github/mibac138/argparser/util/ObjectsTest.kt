package com.github.mibac138.argparser.util

import org.junit.Test

/**
 * Created by mibac138 on 06-04-2017.
 */
class ObjectsTest {
    @Test(expected = NullPointerException::class) fun testNull() {
        Objects.requireNonNull(null)
    }

    @Test fun testValid() {
        Objects.requireNonNull(Any())
    }
}