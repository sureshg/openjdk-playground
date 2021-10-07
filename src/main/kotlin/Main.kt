import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import javax.crypto.*
import javax.crypto.spec.*

fun main() {
  println("Hello Kotlin! ${App.KOTLIN_VERSION}")
  val s: Result<Int> = Result.Success(10)
  when (s) {
    is Result.Success -> 1
    is Error -> 2
    is Result.Error.RecException -> TODO()
    is Result.Error.NonRecException -> TODO()
    Result.InProgress -> TODO()
  }

  println("Security Manager Allowed: ${System.getProperty("java.security.manager")}")
  println("Security Manager: ${System.getSecurityManager()}")
  // echo -n "test" | openssl dgst -sha256 -hmac 1234 -binary | base64
  println("test".hmacSha256("1234"))
}

fun String.hmacSha256(secret: String): String {
  val key = SecretKeySpec(secret.toByteArray(UTF_8), "HmacSHA256")
  val mac: Mac = Mac.getInstance("HmacSHA256")
  mac.init(key)
  val out = mac.doFinal(this.toByteArray(UTF_8))
  return Base64.getEncoder().encodeToString(out)
}

sealed class Result<out T> {
  data class Success<T : Any>(val data: T) : Result<T>()
  sealed class Error(val exec: Exception) : Result<Nothing>() {
    class RecException(val e: Exception) : Error(e)
    class NonRecException(val e: Exception) : Error(e)
  }

  object InProgress : Result<Nothing>()
}
