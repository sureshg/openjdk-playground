import org.slf4j.*
import kotlin.properties.*
import kotlin.reflect.*

/** Build source logger */
val logger = LoggerFactory.getLogger("buildSrc")

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
