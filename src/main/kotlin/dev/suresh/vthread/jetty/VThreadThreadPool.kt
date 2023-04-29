package dev.suresh.vthread.jetty

import java.util.concurrent.Executors
import org.eclipse.jetty.util.thread.ThreadPool

class VirtualThreadPool : ThreadPool {

  private val execSvc = Executors.newVirtualThreadPerTaskExecutor()

  override fun execute(cmd: Runnable) {
    execSvc.submit(cmd)
  }

  override fun join() {
    while (!execSvc.isTerminated) {
      Thread.onSpinWait()
    }
  }

  override fun getThreads() = Int.MAX_VALUE

  override fun getIdleThreads() = Int.MAX_VALUE

  override fun isLowOnThreads() = false
}
