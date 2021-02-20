package dev.suresh.hidden

import java.lang.invoke.*
import java.util.*

/**
 * Run with "-XX:+UnlockDiagnosticVMOptions -XX:+ShowHiddenFrames"
 * to see the hidden classes.
 *
 * @since JDK15
 */
fun main() {
    val clazz = "${Foo::class.java.name.replace('.', '/')}.class"
    val clBytes = ClassLoader.getSystemClassLoader().getResourceAsStream(clazz)?.readBytes()
        ?: error("Can't load $clazz!")
    val lookup = MethodHandles.lookup().defineHiddenClass(clBytes, true)
    val run = lookup.findStatic(lookup.lookupClass(), "run", MethodType.methodType(Void.TYPE))
    run.invokeExact()
}

interface Foo {
    companion object {

        @JvmStatic
        fun run() {
            println("Inside Foo")
            error("Error from $methodName")
        }
    }
}

inline val methodName: String?
    get() = StackWalker.getInstance().walk { it.findFirst().orNull()?.methodName }

fun <T> Optional<T>.orNull(): T? = orElse(null)