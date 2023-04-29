import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun main() {
  println("Hello Kotlin! ${App.KOTLIN_VERSION}")
  println("Security Manager Allowed: ${System.getProperty("java.security.manager")}")
  println("Security Manager: ${System.getSecurityManager()}")
  // echo -n "test" | openssl dgst -sha256 -hmac 1234 -binary | base64
  println("test".hmacSha256("1234"))
}

fun String.hmacSha256(secret: String): String {
  val key = SecretKeySpec(secret.encodeToByteArray(), "HmacSHA256")
  val mac: Mac = Mac.getInstance("HmacSHA256")
  mac.init(key)
  val out = mac.doFinal(encodeToByteArray())
  return Base64.getEncoder().encodeToString(out)
}

/**
 * See the graph for more details,
 * ```
 * graph LR
 *   A[Christmas] -->|Get money| B(Go shopping)
 *   B --> C{Let me think}
 *   C -->|One| D[Laptop]
 *   C -->|Two| E[iPhone]
 *   C -->|Three| F[fa:fa-car Car]
 * ```
 */
sealed class Result<out T> {
  data class Success<T : Any>(val data: T) : Result<T>()

  sealed class Error(val exec: Exception) : Result<Nothing>() {
    class RecException(val e: Exception) : Error(e)

    class NonRecException(val e: Exception) : Error(e)
  }

  data object InProgress : Result<Nothing>()
}
