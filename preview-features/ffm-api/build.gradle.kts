plugins { id("plugins.kotlin") }

group = "dev.suresh"

version = libs.versions.ffm.api.get()

dependencies { implementation(kotlin("stdlib-jdk8")) }
