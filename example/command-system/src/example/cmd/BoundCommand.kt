package example.cmd

import com.github.mibac138.argparser.binder.BoundMethod
import com.github.mibac138.argparser.binder.invoke
import com.github.mibac138.argparser.parser.Parser
import com.github.mibac138.argparser.reader.asReader

abstract class BoundCommand : Command {
    protected abstract val parser: Parser
    protected abstract val method: BoundMethod

    override fun run(args: String) {
        method.invoke(args.asReader(), parser)
    }
}