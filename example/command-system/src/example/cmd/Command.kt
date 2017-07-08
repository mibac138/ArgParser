package example.cmd

/**
 * Created by mibac138 on 08-07-2017.
 */
interface Command {
    val name: String
    val description: String

    fun run(args: String)
}