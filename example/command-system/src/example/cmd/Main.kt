package example.cmd

import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val cmdRegistry = CommandRegistryImpl()
    cmdRegistry.addCommand(HelloWorldCommand())
    cmdRegistry.addCommand(HelpCommand(cmdRegistry))
    cmdRegistry.addCommand(CommandListCommand(cmdRegistry))

    println("Hello!")

    while (true) {
        val input = scanner.nextLine()
        val cmdName = input.substringBefore(' ')
        val cmdInput = input.substringAfter(' ', "")

        if (cmdName.equals("exit", true)) break

        val command = cmdRegistry.getCommand(cmdName)

        if (command == null) {
            println("I don't know this command :(")
        } else {
            command.run(cmdInput)
        }

        // Makes output easier to read by separating commands' output
        println()
    }

    println("Goodbye!")
}