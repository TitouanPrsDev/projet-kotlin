package com.appdrone

import android.content.Intent
import android.net.InetAddresses
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Inet4Address
import java.net.InetAddress
import java.net.Socket


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
        this@MainActivity.runOnUiThread(java.lang.Runnable {
            run() {
               tcptest()
            }
        })
    }
fun tcptest(){
    val client = Socket("192.168.1.13", 5555)
    val output = PrintWriter(client.getOutputStream(), true)
    val input = BufferedReader(InputStreamReader(client.inputStream))



    println("Client sending [Hello]")
    output.println("Hello")
    println("Client receiving [${input.readLine()}]")
    client.close()
}
}
