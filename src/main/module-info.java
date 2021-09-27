// Not used now. Should be moved to src/kotlin
import dev.suresh.service.MyProcessor;
import javax.annotation.processing.Processor;

module dev.suresh.openjdkplayground {
  requires java.net.http;
  requires jdk.httpserver;
  requires java.compiler;
  requires java.desktop;
  requires kotlin.stdlib.jdk8;
  requires kotlinx.serialization.core;
  requires org.eclipse.jetty.server;
  requires org.eclipse.jetty.servlet;
  requires okhttp3;
  requires okhttp3.tls;
  requires okhttp3.mockwebserver;
  requires com.google.auto.service;
  requires com.google.common;
  requires org.slf4j.simple;
  requires gg.jte;
  requires gg.jte.runtime;
  requires shrinkwrap.resolver.api;
  requires shrinkwrap.resolver.spi;

  // opens dev.suresh;
  exports dev.suresh;

  provides Processor with
      MyProcessor;
}
