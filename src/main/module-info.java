// Not used now. Should be moved to src/main/kotlin/module-info.java

import dev.suresh.service.MyProcessor;

import javax.annotation.processing.Processor;

module dev.suresh.openjdkplayground {
    requires java.net.http;
    requires jdk.httpserver;
    requires java.compiler;
    requires java.desktop;
    requires kotlin.stdlib;
    requires kotlinx.serialization.core;
    requires org.eclipse.jetty.server;
    requires okhttp3;
    requires okhttp3.tls;
    requires okhttp3.mockwebserver;
    requires com.google.auto.service;
    requires org.slf4j.simple;
    requires gg.jte;
    requires gg.jte.runtime;
    requires com.squareup.moshi.kotlin;
    requires com.squareup.moshi;

    // opens dev.suresh;
    exports dev.suresh;

    provides Processor with
            MyProcessor;
}
