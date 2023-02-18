plugins { id("plugins.kotlin") }

group = libs.versions.group.get()

version = libs.versions.ffm.api.get()

dependencies { implementation(kotlin("stdlib")) }
