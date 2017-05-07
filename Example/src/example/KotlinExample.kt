package example

import com.github.mibac138.argparser.SimpleParserRegistry
import com.github.mibac138.argparser.reader.asReader
import com.github.mibac138.argparser.syntax.dsl.element
import com.github.mibac138.argparser.syntax.dsl.syntaxContainer


/**
 * A rather basic example. Uses only core. Kind of uncomfortable.
 * Please check KotlinBinderExample for a more pleasant
 * solution
 */
fun main(args: Array<String>) {
    val example = KotlinExample()
    val parser = SimpleParserRegistry()
    val reader = "Luke 100".asReader()
    val syntax =
            syntaxContainer(Nothing::class.java) {
                element(String::class.java)
                element(Int::class.java)
            }

    val result = parser.parse(reader, syntax)
    example.lendMoney(result[0] as String, result[1] as Int)
}

class KotlinExample {
    fun lendMoney(name: String, amount: Int) {
        System.out.println("Lent $$amount to $name")
    }
}