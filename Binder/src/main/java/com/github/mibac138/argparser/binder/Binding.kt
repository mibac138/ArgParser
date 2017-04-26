package com.github.mibac138.argparser.binder

import com.github.mibac138.argparser.Parser
import com.github.mibac138.argparser.named.NamedSyntaxElement
import com.github.mibac138.argparser.reader.ArgumentReader
import com.github.mibac138.argparser.syntax.SyntaxElement
import com.github.mibac138.argparser.syntax.iterator
import java.util.*
import java.util.Collections.emptyList
import kotlin.collections.HashMap

/**
 * Created by mibac138 on 14-04-2017.
 */
class Binding internal constructor(private val boundMethod: BoundMethod) {
    private val exceptions = ArrayList<Exception>()
    private var syntax: SyntaxElement<*> = boundMethod.getSyntax()

    private val argsMap: MutableMap<String, IndexedValue<SyntaxElement<*>>> = HashMap()
    private val noNameArgsMap: MutableMap<Class<*>, IndexedValue<SyntaxElement<*>>> = HashMap()

    init {
        for ((i, element) in syntax.iterator().withIndex())
            if (element is NamedSyntaxElement)
                argsMap[element.getName()] = IndexedValue(i, element)
            else
                noNameArgsMap[element.getOutputType()] = IndexedValue(i, element)
    }

    fun getSyntax(): SyntaxElement<*>
            = syntax

    fun getExceptions(): List<Exception>
            = exceptions

    fun invoke(input: ArgumentReader, parser: Parser): Any? {
        exceptions.clear()
        val args = parser.parse(input, syntax)

        val mapped: Array<*>

        if (args is List<*>)
            mapped = transform(args.iterator().asEntry())
        else if (args is Map<*, *>)
            mapped = transform(args.entries.iterator() as Iterator<Map.Entry<String?, *>>)
        else
            mapped = emptyArray<Any>()

        println("Args:  ${Arrays.toString(mapped)}")
        println("Types: ${mapped.map { it?.javaClass?.typeName ?: "--" }}")
        println("Len:   ${mapped.size}")

        return boundMethod.invoke(mapped)
    }

    fun syntaxChanged() {
        syntax = boundMethod.getSyntax()
        for ((i, element) in syntax.iterator().withIndex())
            if (element is NamedSyntaxElement)
                argsMap[element.getName()] = IndexedValue(i, element)
            else
                noNameArgsMap[element.getOutputType()] = IndexedValue(i, element)
    }

    private fun transform(iterator: Iterator<Map.Entry<String?, *>>): Array<*> {
        val list = HashMap<SyntaxElement<*>, Any?>()
        for (entry in iterator) {
            if (entry.value is Exception) {
                exceptions.add(entry.value as Exception)
            } else {
                val element = resolveArg(entry) ?: throw NonArgParameterException()

                if (list[element.value] != null)
                    throw IllegalStateException("Can't pass two values to one argument")

                list[element.value] = entry.value

                println("I: ${element.index}    V: ${element.value}")
            }
        }

        return list.values.toTypedArray()
    }

    private fun resolveArg(entry: Map.Entry<String?, *>): IndexedValue<SyntaxElement<*>>? {
        if (entry.key == null)
            return getArgByType(entry.value!!.javaClass)
        else
            return getArgByName(entry.key!!)
    }

    private fun map(list: List<*>): List<Any?> {
        if (list.isEmpty()) {
            return emptyList()
        }

        if (list.size == 1)
            if (list[0] is Map<*, *>)
                return map(list[0] as Map<*, *>)
            else
                return listOf(list[0])

        val output = ArrayList<Any?>(list.size)
        for (element in list)
            when (element) {
                null -> {
                }
                is Exception -> exceptions.add(element)
                is Map<*, *> -> output.addAll(map(element)) // wont this cause order problems?
                else -> {
                    val arg = getArgByType(element.javaClass)!!
                    if (output.getOrNull(arg.index) != null)
                        throw IllegalStateException()

                    output.ensureSize(arg.index + 1)
                    output[arg.index] = element
                }
            }

        return output
    }

    private fun map(map: Map<*, *>): List<Any?> {
        val list = ArrayList<Any?>()
        for ((key, value) in map) {
            if (value is List<*>) {
                // Unordered elements
                list.addAll(map(value))
            }
            val arg = getArgByName(key as String)!!
            list[arg.index] = value
        }

        return list
    }

    private fun getArgByType(type: Class<*>): IndexedValue<SyntaxElement<*>>? {
        for ((_, value) in argsMap)
            if (type.isAssignableFrom(value.value.getOutputType()))
                return value

        for ((clazz, value) in noNameArgsMap) {
            if (type.isAssignableFrom(clazz))
                return value
        }

        return null
    }

    private fun getArgByName(name: String): IndexedValue<SyntaxElement<*>>? {
        return argsMap[name]
    }

    private fun ArrayList<Any?>.ensureSize(size: Int) {
        ensureCapacity(size)
        while (this.size < size) {
            add(null)
        }
    }
}

private fun <T> Iterator<T>.asEntry(): Iterator<Map.Entry<String?, *>> {
    return ListAsEntryIterator(this)
}

private class ListAsEntryIterator<T>(private val iterator: Iterator<T>) : Iterator<Map.Entry<String?, T>> {
    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): Map.Entry<String?, T> {
        val value = iterator.next()
        return object : Map.Entry<String?, T> {
            override val key: String?
                get() = null

            override val value: T
                get() = value
        }
    }
}