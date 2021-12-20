package dev.suresh.lang

import java.net.*
import java.util.*

/**
 * Convert [Optional] to Kotlin's nullable type.
 */
fun <T> Optional<T>.orNull(): T? = orElse(null)

/**
 * Returns the method name contains this call-site
 */
inline val methodName get() = StackWalker.getInstance().walk { it.findFirst().orNull()?.methodName }

/**
 * Read the [Class] as [ByteArray]
 */
fun <T : Class<*>> T.toBytes(): ByteArray? {
  val classAsPath = "${name.replace('.', '/')}.class"
  return classLoader.getResourceAsStream(classAsPath)?.readBytes()
}

/**
 * Returns the actual class [URL]
 *
 * ```
 * val url = LogManager::class.java.resourcePath
 * ```
 */
val <T : Class<*>> T.resourcePath: URL? get() = getResource("${simpleName}.class")
