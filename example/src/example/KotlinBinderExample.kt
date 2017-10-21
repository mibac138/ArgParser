package example

import com.github.mibac138.argparser.binder.MethodBinder
import com.github.mibac138.argparser.binder.invoke
import com.github.mibac138.argparser.parser.SimpleParserRegistry
import com.github.mibac138.argparser.reader.asReader


fun main(args: Array<String>) {
    val binding = MethodBinder.bindMethod(KotlinBinderExample()::lendMoney)

    val lent = binding.invoke("Luke 100".asReader(), SimpleParserRegistry()) as Boolean

    if (lent)
        System.out.println("Money lent!")
    else
        System.out.println("Didn't lend money")
}

class KotlinBinderExample {
    fun lendMoney(name: String, amount: Int): Boolean {
        return if (amount < 200) {
            System.out.println("Lent $$amount to $name")
            true
        } else {
            System.out.println("Didn't lend $$amount to $name")
            false
        }
    }
}