package com.chaos.ekinomy.web

import com.chaos.ekinomy.data.PlayerBalanceData
import com.chaos.ekinomy.handler.EkinomyManager
import io.ktor.html.*
import kotlinx.html.*

class MainPage : Template<HTML> {
    override fun HTML.apply() {
        head {
            link(rel = "stylesheet", href = "/main.css", type = "text/css")
            title("Ekinomy")
        }

        body {
            h1 {
                +"Ekinomy: A modern economy mod!"
            }

            table {
                thead {
                    tr {
                        th {
                            +"PlayerName"
                        }

                        th {
                            +"PlayerUUID"
                        }

                        th {
                            +"Balance"
                        }
                    }
                }

                tbody {
                    val sortedBalanceList = EkinomyManager.getBalanceDataCollection().sortedByDescending(
                        PlayerBalanceData::balance
                    )

                    for (balanceData in sortedBalanceList) {
                        tr {
                            td {
                                +balanceData.playerName
                            }

                            td {
                                +balanceData.playerUUID.toString()
                            }

                            td {
                                +balanceData.balance.toString()
                            }
                        }
                    }
                }
            }
        }
    }
}