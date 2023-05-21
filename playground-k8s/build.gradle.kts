plugins {
  id("plugins.kotlin")
  id("plugins.publishing")
}

group = libs.versions.group.get()

dependencies {
  implementation(platform(libs.testcontainers.bom))
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.testcontainers.core)
  testImplementation(libs.testcontainers.junit)
  testImplementation(libs.testcontainers.k3s)
  testImplementation(libs.kubernetes.client)
  testImplementation(libs.slf4j.simple)
}

tasks.test { useJUnitPlatform() }
