package com.chaos.ekinomy.web

import com.chaos.ekinomy.util.config.Config
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.css.*

object EkinomyDashboard {
    var application: ApplicationEngine? = null

    fun init() {
        if (application != null) {
            application?.environment?.start()
        } else {
            application = embeddedServer(Netty, Config.SERVER.webPort.get()) {
                routing {
                    get("/") {
                        call.respondHtmlTemplate(MainPage()) {}
                    }

                    get("/log") {
                        call.respondHtmlTemplate(LogPage()) {}
                    }

                    get("/main.css") {
                        call.respondCss {
                            body {
                                backgroundColor = Color("#212121")
                                color = Color.white
                                textAlign = TextAlign.center
                                fontWeight = FontWeight.bolder
                                fontFamily = "Helvetica, sans-serif"
                            }

                            rule("*") {
                                margin = "0 auto"
                                padding = "20 10"
                            }

                            rule("table, td") {
                                border = "3px solid #333"
                                maxWidth = 80.pct
                            }

                            rule("article") {
                                marginTop = 20.px
                            }

                            rule("table") {
                                marginTop = 20.px
                            }

                            rule("td") {
                                overflow = Overflow.hidden
                                whiteSpace = WhiteSpace.nowrap
                                textOverflow = TextOverflow.ellipsis
                                width = 200.px
                            }
                        }
                    }
                }
            }.start()
        }
    }

    fun stop() {
        val environment = application?.environment

        environment?.stop()
    }
}