plugins {
  `java-platform`
  id("plugins.publishing")
}

description = "A platform (BOM) used to align all module versions"

dependencies { constraints { add("api", projects.openjdkPlayground) } }
