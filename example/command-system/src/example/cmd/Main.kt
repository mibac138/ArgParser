package example.cmd

import java.util.*

/**
 * Created by mibac138 on 08-07-2017.
 */

fun main(args: Array<String>) {
    val commands = listOf<Any>()
    val scanner = Scanner(System.`in`)

    while (true) {
        val input = scanner.nextLine()

        if (input.equals("exit", true)) break

    }

    println("Goodbye!")
}