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

package com.github.mibac138.argparser.named

import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.matchPattern
import java.util.regex.Pattern

/**
 * A simple argument matcher based on [Pattern]. Expects name to be at group 1. If it's not
 * please specify it in the constructor. [match] returns the given reader as result's reader
 */
class PatternArgumentMatcher @JvmOverloads constructor(
        private val pattern: Pattern,
        private val group: Int = 1)
    : ArgumentMatcher {

    override fun match(reader: ArgumentReader): ArgumentMatcher.Result? {
        val result = reader.matchPattern(pattern) ?: return null

        return BasicArgumentMatchResult(result.group(group), reader)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as PatternArgumentMatcher

        if (group != other.group) return false
        // Pattern itself doesn't override equals so we must check for equality ourselves
        if (pattern.pattern() != other.pattern.pattern()) return false
        if (pattern.flags() != other.pattern.flags()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group
        result = 31 * result + pattern.pattern().hashCode()
        result = 31 * result + pattern.flags()
        return result
    }

    override fun toString(): String
            = "PatternArgumentMatcher(pattern=$pattern)"
}