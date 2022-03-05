// package dev.suresh.foojay
//
// import App
// import eu.hansolo.jdktools.*
// import eu.hansolo.jdktools.versioning.*
// import io.foojay.api.discoclient.*
// import io.foojay.api.discoclient.pkg.*
//
// object Disco {
//
//  fun run() {
//    val discoClient = DiscoClient()
//    println("Getting OpenJDK ${App.JAVA_VERSION} packages...")
//
//    // Get pkgs works only after the `distributions` call.
//    discoClient.distributions
//
//    val packagesFound = discoClient.getPkgs(
//      listOf(DiscoClient.getDistributionFromText("oracle_open_jdk")),
//      VersionNumber.fromText(App.JAVA_VERSION),
//      Latest.NONE,
//      OperatingSystem.LINUX,
//      LibCType.NONE,
//      Architecture.X64,
//      Bitness.BIT_64,
//      ArchiveType.TAR_GZ,
//      PackageType.NONE,
//      false,
//      true,
//      listOf(ReleaseStatus.EA),
//      TermOfSupport.NONE,
//      listOf(Scope.NONE),
//      Match.ANY
//    )
//
//    packagesFound.forEach {
//      println(it)
//    }
//
//    println("Done")
//  }
// }
//
// fun main() {
//  Disco.run()
// }
