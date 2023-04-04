package com.appdrone.Thread

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


class Connexion {
    val host = "10.0.2.2"
    val port = 8080

    fun recevoir(){
        val thread: Thread = Thread(Runnable {
            try {
                val client = Socket(host, port)
                val output = PrintWriter(client.getOutputStream(), true)
                val input = BufferedReader(InputStreamReader(client.inputStream))

                input.useLines { lines ->
                    lines.forEach {
                        val fields = it.split(",")
                        if (fields[0] == "\$GPRMC") {
                            var longitude = fields[5]
                            var latitude = fields[3]
                            var vitesse = fields[7]
                            longitude = longitude.substring(0, 3) + "." + longitude.substring(3,5) + "°"
                            latitude = latitude.substring(0, 2) + "." + latitude.substring(2,4) + "°"
                            println(" longitude : " + longitude)
                            println(" latitude : " + latitude )
                            println(" vitesse : " + vitesse)
                            return@forEach
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }
}