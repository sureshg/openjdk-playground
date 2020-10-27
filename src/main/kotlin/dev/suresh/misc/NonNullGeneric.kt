package dev.suresh.misc

class Foo<T>(val bar: T)

/**
 * Unbounded T is same as "T: Any?"
 */
fun main() {

    val f1 :Foo<String>  = Foo("hello")
    println(f1.bar)

    val f2 = Foo(null)
    println(f2.bar)

    val f3 : Foo<String?> = Foo("NullableString")
    println(f3.bar)
}