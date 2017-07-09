package example.cmd

/**
 * Created by mibac138 on 09-07-2017.
 */
class CommandListCommand(private val cmdRegistry: CommandRegistry) : Command {
    override val name = "list"
    override val description = "shows you the list of commands"

    override fun run(args: String) {
        println("Commands:")
        for (command in cmdRegistry.getCommands()) {
            println("- ${command.name}")
        }
    }
}