package com.appdrone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import com.appdrone.Thread.Monthread

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.appdrone.databinding.ActivityVue1Binding
import com.appdrone.entities.Drone
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*


class Vue1 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityVue1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVue1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        //Create a data source and add it to the map.
        mMap = googleMap
        val drone1 : Drone = Drone("drone1")

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val MonThread : Monthread = Monthread()
        MonThread.start()

        var longitude = ""
        var latitude = ""
        var vitesse = ""
        var rotation = ""

        val markerbateau = mMap.addMarker(MarkerOptions().position(LatLng(0.00, 0.00)).title(drone1.name).icon(drone1.icon))
        if (markerbateau != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerbateau.position, 8f))
        }
        val timer = Timer()
        val task = object : TimerTask(){
            override fun run() {
                longitude = MonThread.longitude
                latitude = MonThread.latitude
                vitesse = MonThread.vitesse
                rotation = MonThread.rotation

                println("longitude : " + longitude)
                println("latitude : " + latitude)
                println("vitesse : " + vitesse)
                println("rotation : " + rotation)
                println("--------------------")
                if (longitude != "" && latitude != "" && vitesse != "" && rotation != ""){
                    drone1.position.x = longitude.toDouble()
                    drone1.position.y = latitude.toDouble()
                    this@Vue1 .runOnUiThread {
                        if (markerbateau != null) {
                            markerbateau.position = LatLng(drone1.position.x, drone1.position.y)
                            markerbateau.rotation = rotation.toFloat()
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerbateau.position))
                        }
                    }
                }
            }
        }
        timer.schedule(task, 0, 1000)

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