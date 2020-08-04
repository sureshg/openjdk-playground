package dev.suresh.server

import okhttp3.*
import okhttp3.mockwebserver.*
import okhttp3.tls.*
import okhttp3.tls.internal.*
import java.net.HttpURLConnection.HTTP_MOVED_TEMP
import java.security.cert.*
import java.time.*

/**
 * More OKHttp samples can found in this [repo](https://github.com/square/okhttp/tree/master/samples/guide/src/main/java/okhttp3/recipes/kt)
 */
class DevServer {
    /**
     * Generate a self-signed cert for the server to serve and the client to trust.
     */
    val selfSignedCert = TlsUtil.localhost()

    /**
     * Start the HTTPS server with a self signed cert.
     */
    val server = MockWebServer().apply {
        useHttps(selfSignedCert.sslSocketFactory(), false)
    }

    val clientCerts = HandshakeCertificates.Builder()
        .addPlatformTrustedCertificates()
        .addInsecureHost(server.hostName)
        .build()

    val client = OkHttpClient.Builder()
        .sslSocketFactory(clientCerts.sslSocketFactory(), clientCerts.trustManager)
        .callTimeout(Duration.ofSeconds(5))
        .build()

    /**
     * Enqueue a request, run a client and shutdown the server.
     */
    fun run() {
        server.use { server ->
            // Enqueue the request
            server.enqueue(
                MockResponse().setResponseCode(HTTP_MOVED_TEMP)
                    .setHeader("Location", "https://www.google.com/robots.txt")
            )

            val req = Request.Builder()
                .header("User-Agent", OkHttp.VERSION)
                .url(server.url("/"))
                .build()

            client.newCall(req)
                .execute()
                .use { res ->
                    when (res.isSuccessful) {
                        true -> {
                            println("\nGot response from server: ${res.request.url}")
                            val resHeaders = res.headers

                            println("Response headers are,")
                            resHeaders.forEach {
                                println("${it.first} : ${it.second}")
                            }

                            println("${res.protocol} Peer certificates are,")
                            res.handshake?.peerCertificates?.forEach {
                                val cert = it as X509Certificate
                                println(cert.subjectDN)
                            }
                        }
                        else -> throw error("Unexpected code $res")
                    }
                }
        }
    }
}
