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

  val wasmBuild by
      registering(JavaExec::class) {
        val wasmDir = project.layout.buildDirectory.map { it.dir("wasm") }
        doFirst { mkdir(wasmDir) }

        group = "build"
        description = "Builds the WASM module"
        mainClass = "de.mirkosertic.bytecoder.cli.BytecoderCLI"
        classpath = sourceSets.main.get().runtimeClasspath
        args =
            listOf(
                "compile",
                "wasm",
                "-builddirectory=${wasmDir.get().asFile.absolutePath}",
                "-classpath=${sourceSets.main.get().runtimeClasspath.asPath}",
                "-mainclass=Main",
                "-filenameprefix=app-",
                "-optimizationlevel=ALL")

        doLast {
          wasmDir
              .get()
              .file("index.html")
              .asFile
              .writeText(
                  """
                  | <html>
                  | <meta charset="UTF-8">
                  | <script src="app-runtime.js"></script>
                  | <script>
                  |     bytecoder.instantiate('app-wasmclasses.wasm').then(function() {
                  |         console.log("Bootstrapped");
                  |         bytecoder.instance.exports.main(null, bytecoder.instance.exports.newObjectArray(null, 0));
                  |         console.log("WebAssembly app is ready!");
                  |     });
                  | </script>
                  | </html>
                  """
                      .trimMargin())
          println("WebAssembly app built at: ${wasmDir.get().asFile.path}")
        }
      }
}

dependencies { implementation(libs.wasm.bytecoder) }
