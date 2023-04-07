package com.appdrone

import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import com.appdrone.Utilitaire.Monthread
import com.appdrone.databinding.ActivityVue1Binding
import com.appdrone.entities.Drone
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import java.lang.Math.round
import java.util.*


class Vue1 : AppCompatActivity(), OnMapReadyCallback, CoroutineScope by MainScope() {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityVue1Binding

    // Création d'un thread pour récupérer les données du drone en temps réel
    val MonThread : Monthread = Monthread()

    // Création d'un timer pour mettre à jour la position du drone
    val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVue1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        // Création d'un drone
        val drone : Drone = Drone("drone")

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        MonThread.start()

        var longitude = ""
        var latitude = ""
        var vitesse = ""
        var direction = ""
        val polylineOptions = PolylineOptions().color(Color.RED).width(5f)

        val markerbateau = mMap.addMarker(MarkerOptions().position(LatLng(0.00, 0.00)).title(drone.name).icon(drone.icon).snippet(""))
        if (markerbateau != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerbateau.position, 8f))
        }

        val task = object : TimerTask(){
            override fun run() {
                longitude = MonThread.longitude
                latitude = MonThread.latitude
                vitesse = MonThread.vitesse
                direction = MonThread.direction

                if (longitude != "" && latitude != "" && vitesse != "" && direction != ""){
                    drone.position!!.x = longitude.toDouble()
                    drone.position!!.y = latitude.toDouble()
                    drone.direction = direction.toDouble()
                    drone.vitesse = vitesse.toDouble()
                    this@Vue1 .runOnUiThread {
                        if (markerbateau != null) {
                            markerbateau.position = LatLng(drone.position!!.x, drone.position!!.y)
                            markerbateau.rotation = drone.direction!!.toFloat()

                            markerbateau.snippet = "Vitesse : " + drone.vitesse + " knots" + " -> " + round(drone.vitesse!! * 1.852) + " km/h"
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerbateau.position))
                            polylineOptions.add(markerbateau.position)
                            mMap.addPolyline(polylineOptions)
                        }
                    }
                }
            }
        }
        timer.schedule(task, 0, 1000)

        }
    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        MonThread.interrupt()
        finish()
    }
}