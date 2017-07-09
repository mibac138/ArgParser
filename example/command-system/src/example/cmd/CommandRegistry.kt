package example.cmd

/**
 * Created by mibac138 on 08-07-2017.
 */

interface CommandRegistry {
    fun getCommand(name: String): Command?
    fun addCommand(command: Command)
    fun removeCommand(command: Command)
    fun getCommands(): Collection<Command>

    // TODO operator functions ([i], +=)
}

class CommandRegistryImpl : CommandRegistry {
    private val commandMap = mutableMapOf<String, Command>()

    override fun getCommand(name: String): Command?
            = commandMap[name.toLowerCase()]

    override fun addCommand(command: Command) {
        commandMap[command.name.toLowerCase()] = command
    }

    override fun removeCommand(command: Command) {
        commandMap.remove(command.name.toLowerCase())
    }

    override fun getCommands(): Collection<Command>
            = commandMap.values
}