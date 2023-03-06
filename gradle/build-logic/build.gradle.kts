plugins { alias(libs.plugins.benmanes) }

tasks.dependencyUpdates {
  checkForGradleUpdate = true
  checkConstraints = true
}
