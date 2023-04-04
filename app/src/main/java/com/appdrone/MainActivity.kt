package com.appdrone

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View.INVISIBLE
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button);
        val vue1activity : Intent = Intent(this,Vue1::class.java);

        button.setOnClickListener {
            startActivity(vue1activity)
        }
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val thread : Thread = Thread(Runnable {
            try {
                tcptest()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }
fun tcptest(){

        val client = Socket("10.0.2.2", 8080)
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
            }
        }
    }
    println("hello")
    client.close()
}
}
