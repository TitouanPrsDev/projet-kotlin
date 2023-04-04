package com.appdrone.Thread

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Math.floor
import java.net.Socket


class Monthread : Thread() {
    val host = "10.0.2.2"
    val port = 8080

    var longitude = ""
    var latitude = ""
    var vitesse = ""
    var rotation = ""

    override fun run(){
        try {
            val client = Socket(host, port)
            val input = BufferedReader(InputStreamReader(client.inputStream))

           input.useLines { lines ->
               lines.forEach {
                    val fields = it.split(",")
                    if (fields[0] == "\$GPRMC") {
                        longitude = fields[3]

                        latitude = fields[5]
                        vitesse = fields[7]
                        rotation = fields[8]
                        longitude = (-1* ddm_to_dd(longitude.toDouble())).toString()
                        latitude =  ddm_to_dd(latitude.toDouble()).toString()
                        vitesse = ddm_to_dd(vitesse.toDouble()).toString()
                    }
               }
           }
            client.close()
        } catch (e: IOException) {
           e.printStackTrace()
        }
    }

    fun ddm_to_dd(ddm: Double): Double {
        val degrees: Double = floor(ddm / 100.0)
        val minutes = ddm - degrees * 100.0
        return degrees + minutes / 60.0
    }
}
