package dev.suresh.service

import com.google.auto.service.*
import javax.annotation.processing.*
import javax.lang.model.element.*

@AutoService(Processor::class)
class MyProcessor : AbstractProcessor() {
  override fun init(processingEnv: ProcessingEnvironment?) {
    super.init(processingEnv)
    println("Initializing MyProcessor...")
  }

  override fun process(
    annotations: MutableSet<out TypeElement>?,
    roundEnv: RoundEnvironment?,
  ): Boolean {
    return true
  }
}
