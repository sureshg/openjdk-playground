## Install [Jextract][Jextract]

Execute this [script](../../scripts/jextract.sh)

### Build Jextract

```bash
$ git clone https://github.com/openjdk/jextract.git
$ cd jextract
$ git checkout panama
$ sdk u java 20.0.1-zulu
$ chmod +x gradlew
$ ./gradlew -Pjdk21_home="/Users/sgopal1/.sdkman/candidates/java/openjdk-ea-21" \
            -Pllvm_home="/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr" \
            clean verify
$ mv build/jextract ~/install/openjdk
```

### Generate Java Bindings

#### LibC

```bash
$ export PATH=$PATH:~/install/openjdk/jextract/bin/
$ ./gradlew build -PenableJextract
# Copy the generated sources from "build/generated/sources/jextract" to "src/main/java"
```

#### Thrid-Party Libraries

```bash
# Shows an example of generating java bindings of https://github.com/WebAssembly/binaryen
$ wget "https://github.com/WebAssembly/binaryen/releases/download/version_112/binaryen-version_112-arm64-macos.tar.gz"
$ mkdir binaryen
$ tar xvzf binaryen-version_112-arm64-macos.tar.gz --strip-components=1 -C binaryen

$ jextract --library libbinaryen \
           --include-dir /Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/include \
           --include-dir binaryen/include \
           --target-package dev.suresh.binaryen \
           --header-class-name Binaryen \
           --source \
           binaryen/include/binaryen-c.h
```

* [Jextract Samples](https://github.com/openjdk/jextract/tree/master/samples)
* [Project Panama](https://github.com/openjdk/panama-foreign)
* [Linux syscall tables](https://syscalls.mebeim.net/)

[Jextract]:  https://github.com/openjdk/jextract
