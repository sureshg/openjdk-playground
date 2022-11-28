package dev.suresh.lang

import java.net.URL
import kotlin.jvm.optionals.getOrNull

/** Returns the method name contains this call-site */
inline val methodName
  get() = StackWalker.getInstance().walk { it.findFirst().getOrNull()?.methodName }

/** Read the [Class] as [ByteArray] */
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
val <T : Class<*>> T.resourcePath: URL?
  get() = getResource("$simpleName.class")

/** Run the lambda in the context of the receiver classloader. */
fun ClassLoader.using(run: () -> Unit) {
  val cl = Thread.currentThread().contextClassLoader
  try {
    Thread.currentThread().contextClassLoader = this
    run()
  } finally {
    Thread.currentThread().contextClassLoader = cl
  }
}
