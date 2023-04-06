plugins { java }

group = libs.versions.group.get()

version = libs.versions.module.jdk.get()

tasks {
  withType<JavaCompile>().configureEach {
    options.apply {
      encoding = "UTF-8"
      release = 20
      isIncremental = true
    }
  }

  val wasmOutDir = layout.buildDirectory.dir("wasm")
  val htmlCopy by
      registering(Copy::class) {
        from(layout.projectDirectory.file("src/main/resources/index.html"))
        into(wasmOutDir)
      }

  val wasmBuild by
      registering(JavaExec::class) {
        doFirst { mkdir(wasmOutDir) }
        group = "build"
        description = "Builds the WASM module"
        mainClass = "de.mirkosertic.bytecoder.cli.BytecoderCLI"
        classpath = sourceSets.main.get().runtimeClasspath
        args =
            listOf(
                "compile",
                "wasm",
                "-builddirectory=${wasmOutDir.get().asFile.absolutePath}",
                "-classpath=${sourceSets.main.get().runtimeClasspath.asPath}",
                "-mainclass=Main",
                "-filenameprefix=app-",
                "-optimizationlevel=ALL")

        doLast {
          // wasmDir.get().file("index.html").asFile.writeText("")
          println("WebAssembly app built at: ${wasmOutDir.get().asFile.path}")
        }

        outputs.dir(wasmOutDir)
        finalizedBy(htmlCopy)
      }
}

dependencies {
  implementation(libs.wasm.bytecoder)
  implementation(libs.slf4j.simple)
}
