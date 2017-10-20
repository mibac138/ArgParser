package example.cmd

import com.github.mibac138.argparser.binder.BoundMethod
import com.github.mibac138.argparser.parser.Parser
import com.github.mibac138.argparser.parser.parseReturnSyntaxLinkedMap
import com.github.mibac138.argparser.reader.asReader

abstract class BoundCommand : Command {
    protected abstract val parser: Parser
    protected abstract val method: BoundMethod
//    private val linker by lazy { SyntaxLinkerImpl(method.syntax) }

    override fun run(args: String) {
        val parsed = parser.parseReturnSyntaxLinkedMap(args.asReader(), method.syntax)

//        val linked: Map<SyntaxElement, Any?>
//        if (parsed != null)
//            linked = linker.link(parsed)
//        else
//            linked = emptyMap()

        method.invoke(parsed.syntaxToValueMap)
    }
}