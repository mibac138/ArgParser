package com.github.mibac138.argparser.reader

/**
 * Interface for reading arguments. Can be implemented to read
 * from various sources as just a plain string or files
 */
interface ArgumentReader {
    /**
     * Skips [count] characters.
     *
     * @throws IllegalArgumentException if underlying source isn't long enough
     * @param count amount of characters meant to be skipped
     * @since 1.0
     */
    fun skip(count: Int)

    /**
     * Reads [count] next characters and returns them
     *
     * @return string of length [count]
     * @throws IllegalArgumentException if underlying source isn't long enough
     */
    fun read(count: Int): String

    /**
     * Returns `true` if the underlying source can read more characters, `false` otherwise
     *
     * @return whether you can read safely read more characters (at least 1)
     */
    fun hasNext(): Boolean

    /**
     * Returns next character from the underlying source
     *
     * @throws IllegalArgumentException if underlying source is empty
     */
    fun next(): Char

    /**
     * Returns the sum of already read text length and [getAvailableCount].
     * Might not return actual full text length until [hasNext]
     * returns false
     */
    fun getLength(): Int

    /**
     * Returns guaranteed amount of text you can safely read at the moment
     */
    fun getAvailableCount(): Int

    /**
     * Marks current position in case there's a possibility you will need to go back
     */
    fun mark()

    /**
     * Removes last mark (doesn't return to it)
     *
     * @return `true` if removed mark, `false` otherwise (no marks)
     */
    fun removeMark(): Boolean

    /**
     * Removes last mark _and_ returns to it
     *
     * @return `true` if removed mark and returned, `false` otherwise
     */
    fun reset(): Boolean
}

/**
 * Reads the reader until hits a space
 * If possible, use [readUntilSpace] with
 * action as argument instead of this function
 * as it reverts read text in case your
 * code threw an exception
 */
fun ArgumentReader.readUntilSpace(): String {
    val s = StringBuilder()

    skipSpaces()
    while (hasNext()) {
        mark()
        val char = next()
        if (char == ' ') {
            reset()
            return s.toString()
        } else {
            s.append(char)
            removeMark()
        }
    }

    return s.toString()
}

/**
 * Reads text until hits a space and passes it to [action]
 *
 * In case executing [action] throws an exception read text
 * is reverted (you will be able to read it again) and exception
 * is passed on
 */
fun <T> ArgumentReader.readUntilSpace(action: (String) -> T): T {
    mark()
    try {
        val output = action(readUntilSpace())
        removeMark()

        return output
    } catch (e: Exception) {
        reset()
        throw e
    }
}

/**
 * Reads until has a non-space character
 */
fun ArgumentReader.skipSpaces(): ArgumentReader {
    while (hasNext()) {
        mark()
        val char = next()
        if (char != ' ') {
            reset()
            break
        }

        removeMark()
    }

    return this
}

/**
 * Returns your object as a reader [[StringArgumentReader] using `toString() on your object`]
 */
fun <T> T.asReader(): ArgumentReader {
    return StringArgumentReader(this.toString())
}