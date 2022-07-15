package dev.suresh.gradle

import java.io.*
import java.nio.file.*
import kotlin.properties.*
import kotlin.reflect.*

/** OS temp location */
val tmp: String = System.getProperty("java.io.tmpdir")

val File.mebiSize get() = "%.2f MiB".format(length() / (1024 * 1024f))

/** Find the file ends with given [format] under the directory. */
fun File.findPkg(format: String?) = when (format != null) {
  true -> walk().firstOrNull { it.isFile && it.name.endsWith(format, ignoreCase = true) }
  else -> null
}

/** List files based on the glob [pattern] */
fun Path.glob(pattern: String): List<Path> {
  val matcher = FileSystems.getDefault().getPathMatcher("glob:$pattern")
  return Files.walk(this).filter(matcher::matches).toList()
}

/** System property delegate */
@Suppress("IMPLICIT_CAST_TO_ANY")
inline fun <reified T> sysProp(): ReadOnlyProperty<Any?, T> =
  ReadOnlyProperty { _, property ->
    val propVal = System.getProperty(property.name, "")
    val propVals = propVal.split(",", " ").filter { it.isNotBlank() }

    val kType = typeOf<T>()
    when (kType) {
      typeOf<String>() -> propVal
      typeOf<Int>() -> propVal.toInt()
      typeOf<Boolean>() -> propVal.toBoolean()
      typeOf<Long>() -> propVal.toLong()
      typeOf<Double>() -> propVal.toDouble()
      typeOf<List<String>>() -> propVals
      typeOf<List<Int>>() -> propVals.map { it.toInt() }
      typeOf<List<Long>>() -> propVals.map { it.toLong() }
      typeOf<List<Double>>() -> propVals.map { it.toDouble() }
      typeOf<List<Boolean>>() -> propVals.map { it.toBoolean() }
      else -> error("'${property.name}' system property type ($kType) is not supported!")
    } as T
  }
