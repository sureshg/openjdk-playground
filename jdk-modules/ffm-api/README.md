## Install [Jextract][Jextract]

Execute this [script](../../scripts/jextract.sh)

## Generate Java Bindings

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

$ tree dev | more
dev
└── suresh
    └── binaryen
        ├── Binaryen.java
        ├── BinaryenBufferSizes.java
        ├── BinaryenLiteral.java
        ├── BinaryenModuleAllocateAndWriteResult.java
        ├── Binaryen_1.java
        ├── Constants$root.java
        ├── RuntimeHelper.java
        ...
```

* [Jextract Samples](https://github.com/openjdk/jextract/tree/master/samples)
* [Project Panama](https://github.com/openjdk/panama-foreign)

[Jextract]:  https://github.com/openjdk/jextract
