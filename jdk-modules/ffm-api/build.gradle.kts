plugins { id("plugins.kotlin") }

group = libs.versions.group.get()

version = libs.versions.module.jdk.get()

dependencies { implementation(libs.invokebinder) }
