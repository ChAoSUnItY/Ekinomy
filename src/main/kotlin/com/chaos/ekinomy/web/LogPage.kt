package com.chaos.ekinomy.web

import com.chaos.ekinomy.data.OperationType
import com.chaos.ekinomy.handler.EkinomyManager
import io.ktor.html.*
import kotlinx.html.*

class LogPage : Template<HTML> {
    val logContent = TemplatePlaceholder<LogContent>()

    override fun HTML.apply() {
        head {
            link(rel = "stylesheet", href = "/main.css", type = "text/css")
            title {
                +"Ekinomy Logs"
            }
        }

        body {
            h1 {
                +"Ekinomy: Player Economy History Viewer!"
            }

            insert(LogContent(EkinomyManager.getBalanceDataCollection().first().playerName), logContent)
        }
    }

    class LogContent(private val playerName: String) : Template<FlowContent> {
        private val logHistory = EkinomyManager.getAllLogCollection().filter { it.data.playerName == playerName }

        override fun FlowContent.apply() {
            article {
                h2 {
                    +"$playerName's Balance History"
                }

                table {
                    thead {
                        tr {
                            th {
                                +"Operation Type"
                            }

                            th{
                                +"Operation Value"
                            }
                        }
                    }

                    tbody {
                        for (log in logHistory) {
                            tr {
                                td {
                                    +if (log.operationType is OperationType.DATA) log.operationType.targetType.name else log.operationType.type.name
                                }

                                td {
                                    +log.operationType.balance.toString()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}