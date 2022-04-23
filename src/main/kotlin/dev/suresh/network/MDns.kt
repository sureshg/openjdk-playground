package dev.suresh.network

import java.net.*
import java.time.*
import javax.jmdns.*

/**
 *  On Mac : "$ dns-sd -B _services._dns-sd._udp ."
 */
fun main() {
    JmDNS.create(InetAddress.getLocalHost()).use {
        it.addServiceListener(
            "_googlecast._tcp.local.",
            object : ServiceListener {
                override fun serviceAdded(event: ServiceEvent?) =
                    println("--> ${event?.info?.name} added")

                override fun serviceRemoved(event: ServiceEvent?) =
                    println("<-- ${event?.info?.name} Removed")

                override fun serviceResolved(event: ServiceEvent?) =
                    println("${event?.info} resolved")
            }
        )

        it.addServiceTypeListener(object : ServiceTypeListener {
            override fun serviceTypeAdded(event: ServiceEvent?) =
                println("==>${event?.type} service added")

            override fun subTypeForServiceTypeAdded(event: ServiceEvent?) =
                println("==>${event?.type} service sub type added")
        })
        Thread.sleep(Duration.ofSeconds(5))
    }
}
