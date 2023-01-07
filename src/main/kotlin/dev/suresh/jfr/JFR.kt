package dev.suresh.jfr

import java.awt.Desktop
import java.net.URI
import jdk.jfr.Category
import jdk.jfr.Event
import jdk.jfr.Name

@Name("example.Tiger") @Category("Mammal") class JfrEvent : Event()

fun main() {
  Desktop.getDesktop().browse(URI(""))
}
