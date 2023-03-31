## JVM to WebAssembly using [ByteCoder](https://mirkosertic.github.io/Bytecoder/)

##### Build & Run

```bash
# Wasm Build
$ ./gradlew :jdk-modules:jvm-wasm:{clean,wasmBuild}

# Run the app
$ jwebserver -d $(pwd)/jdk-modules/jvm-wasm/build/wasm

# Open the browser
$ open http://localhost:8000
```
