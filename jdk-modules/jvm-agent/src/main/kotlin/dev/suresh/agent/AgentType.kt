package dev.suresh.agent

import jdk.jfr.Category
import jdk.jfr.Description
import jdk.jfr.Event
import jdk.jfr.Label
import jdk.jfr.Name
import jdk.jfr.Period
import jdk.jfr.StackTrace

@Name("dev.suresh.agent.AgentType")
@Label("JVM Agent Type")
@Description("JVM agent attach type")
@Category("JVM Agent", "Agent")
@Period("5 s")
@StackTrace(false)
class AgentType(@Label("Agent Type") val type: String = "static") : Event()
