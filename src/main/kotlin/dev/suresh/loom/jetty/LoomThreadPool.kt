package dev.suresh.loom.jetty

import org.eclipse.jetty.util.component.*
import org.eclipse.jetty.util.thread.*
import java.util.concurrent.*

class LoomThreadPool : AbstractLifeCycle(), ThreadPool {

  private val execSvc = Executors.newVirtualThreadPerTaskExecutor()

  override fun execute(cmd: Runnable) {
    execSvc.submit(cmd)
  }

  override fun join() {
    while (!execSvc.isTerminated) {
      Thread.onSpinWait()
    }
  }

  override fun doStop() {
    super.doStop()
    execSvc.shutdown()
  }

  override fun getThreads() = Int.MAX_VALUE
  override fun getIdleThreads() = Int.MAX_VALUE
  override fun isLowOnThreads() = false
}
