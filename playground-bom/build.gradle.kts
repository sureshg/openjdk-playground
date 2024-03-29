plugins {
  `java-platform`
  id("plugins.publishing")
}

description = "A platform (BOM) used to align all module versions"

dependencies {
  constraints {
    api(projects.openjdkPlayground)
    api(projects.playgroundCatalog)
    api(projects.playgroundK8s)
  }
}
