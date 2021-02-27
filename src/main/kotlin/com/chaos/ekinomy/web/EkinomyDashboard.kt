package com.chaos.ekinomy.web

import com.chaos.ekinomy.util.config.Config
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

object EkinomyDashboard {
    fun init() {
        embeddedServer(Netty, Config.SERVER.webPort.get()) {
            routing {
                get("/") {
                    call.respondText("Ekinomy", ContentType.Text.Html)
                }
            }
        }.start(wait = true)
    }
}