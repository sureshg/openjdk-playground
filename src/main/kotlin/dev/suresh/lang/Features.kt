package dev.suresh.lang

import kotlin.contracts.contract

class Logger(val name: String) {
  fun log(message: String) = println("$name: $message")
}

context(Logger)
fun store(msg: String) {
  log("Stored $msg in the file")
}

fun main() {
  val logger = Logger("main")
  with(logger) { store("Hello") }

  val anon = User.Anonymous
  val auth = User.Authenticated("Suresh")

  smartCast(anon)
  smartCast(auth)
}

fun smartCast(user: User) =
    when (user.isAuthenticated()) {
      true -> user.signOut()
      false -> user.signIn()
    }

sealed class User {

  fun isAuthenticated(): Boolean {
    contract {
      returns(true) implies (this@User is Authenticated)
      returns(false) implies (this@User is Anonymous)
    }
    return this is Authenticated
  }

  data object Anonymous : User() {
    fun signIn() = println("Please sign in")
  }

  data class Authenticated(val name: String) : User() {
    fun signOut() = println("Signing out $name")
  }
}
