package dev.suresh.jte

import App
import gg.jte.*
import gg.jte.output.*

data class KtConfig(val language: String, val version: String)

class RenderJte {
    fun run() {
        val tmplEngine = TemplateEngine.createPrecompiled(ContentType.Plain)
        val output = StringOutput()
        val params = mapOf(
            "config" to Config(),
            "ktConfig" to KtConfig("Kotlin", App.KOTLIN_VERSION),
        )
        tmplEngine.render("hello.jte", params, output)
        println(output)
    }
}


