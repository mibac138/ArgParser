package example.cmd

import com.github.mibac138.argparser.binder.MethodBinder
import com.github.mibac138.argparser.parser.Parser
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.readUntilChar
import com.github.mibac138.argparser.syntax.SyntaxElement

/**
 * Created by mibac138 on 09-07-2017.
 */
class HelpCommand(registry: CommandRegistry) : BoundCommand() {
    override val name = "help"
    override val description = "I'm here to help you. Type \"help <command>\" to get any command's description"
    override val parser = CommandParser(registry)
    override val method = MethodBinder.bindMethod(this::printHelp)

    fun printHelp(cmd: Command? = null) {
        if (cmd == null)
            println("To get help for a command type \"help <command>\"")
        else {
            println("Help for command '${cmd.name}'")
            println(cmd.description)
        }
    }
}

class CommandParser(private val registry: CommandRegistry) : Parser {
    override fun getSupportedTypes(): Set<Class<*>> = setOf(Command::class.java)

    override fun parse(input: ArgumentReader, syntax: SyntaxElement): Command? {
        return registry.getCommand(input.readUntilChar(' '))
    }
}