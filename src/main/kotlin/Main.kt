fun main() {
    println("Hello Kotlin!")
    val s: Result<Int> = Result.Success(10)
    when (s) {
        is Result.Success -> 1
        is Error -> 2
        is Result.Error.RecException -> TODO()
        is Result.Error.NonRecException -> TODO()
        Result.InProgress -> TODO()
    }.exhaustive
}

sealed class Result<out T> {
    data class Success<T : Any>(val data: T) : Result<T>()
    sealed class Error(val exec: Exception) : Result<Nothing>() {
        class RecException(val e: Exception) : Error(e)
        class NonRecException(val e: Exception) : Error(e)
    }

    object InProgress : Result<Nothing>()
}

val <T> T.exhaustive get() = this