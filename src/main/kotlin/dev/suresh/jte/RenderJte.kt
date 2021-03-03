package dev.suresh.jte

import App
import gg.jte.*
import gg.jte.output.*

data class KtConfig(
    val language: String = "Kotlin",
    val version: String,
    val appVersion: String?
)

class RenderJte {
    fun run() {
        val tmplEngine = TemplateEngine.createPrecompiled(ContentType.Plain)
        val params = mapOf(
            "config" to Config(),
            "ktConfig" to KtConfig(
                version = App.KOTLIN_VERSION,
                appVersion = App.VERSION
            ),
        )

        listOf("hello.jte", "hello-kt.kte").forEach {
            val output = StringOutput()
            tmplEngine.render(it, params, output)
            println(output)
        }
    }
}