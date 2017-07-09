package example.cmd

import com.github.mibac138.argparser.binder.MethodBinder
import com.github.mibac138.argparser.parser.Parser
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.reader.readUntilChar
import com.github.mibac138.argparser.syntax.SyntaxElement

class HelloWorldCommand : BoundCommand() {
    override val name = "hello"
    override val description = "Use \"hello\" for a default hello world or type \"hello someText\" for \"Hello, someText!\""
    override val parser = ReadLineParser()
    override val method = MethodBinder.bindMethod(this::sayHello)

    fun sayHello(name: String = "World") {
        println("Hello, $name!")
    }
}

class ReadLineParser : Parser {
    override fun getSupportedTypes(): Set<Class<*>> = setOf(String::class.java)

    override fun parse(input: ArgumentReader, syntax: SyntaxElement): Any?
            = input.readUntilChar('\n').notEmptyOrNull()

    // Otherwise default value wouldn't be called
    private fun String.notEmptyOrNull()
            = if (this.isEmpty()) null else this
}