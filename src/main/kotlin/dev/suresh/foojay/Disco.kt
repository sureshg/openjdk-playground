package dev.suresh.foojay

import App
import io.foojay.api.discoclient.*
import io.foojay.api.discoclient.pkg.*
import io.foojay.api.discoclient.pkg.Distribution.ORACLE_OPEN_JDK

object Disco {

  fun run() {
    val disco = DiscoClient()
    println("Getting OpenJDK ${App.JAVA_VERSION} packages...")

    val pkgs = disco.getPkgs(
      ORACLE_OPEN_JDK,
      VersionNumber.fromText(App.JAVA_VERSION),
      Latest.NONE,
      OperatingSystem.MACOS,
      LibCType.LIBC,
      Architecture.X64,
      Bitness.BIT_64,
      ArchiveType.TAR_GZ,
      PackageType.NONE,
      false,
      true,
      ReleaseStatus.EA,
      TermOfSupport.NONE,
      Scope.NONE
    )

    pkgs.forEach {
      println(disco.getPkgDirectDownloadUri(it.ephemeralId, it.javaVersion))
    }
  }
}
