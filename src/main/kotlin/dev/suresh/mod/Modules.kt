package dev.suresh.mod

fun main() {

    ModuleLayer.boot().modules().forEach {
        println(it)
    }
}