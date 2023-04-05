package com.appdrone

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.FragmentTransaction
import com.appdrone.Thread.Monthread

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.appdrone.databinding.ActivityVue1Binding
import com.appdrone.entities.Drone
import com.google.android.gms.maps.model.PolylineOptions
import java.lang.Math.round
import java.util.*


class Vue1 : AppCompatActivity(), OnMapReadyCallback, ActionBar.TabListener {

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
        var direction = ""
        val polylineOptions = PolylineOptions().color(Color.RED).width(5f)

        val markerbateau = mMap.addMarker(MarkerOptions().position(LatLng(0.00, 0.00)).title(drone1.name).icon(drone1.icon).snippet(""))
        if (markerbateau != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerbateau.position, 8f))
        }
        val timer = Timer()
        val task = object : TimerTask(){
            override fun run() {
                longitude = MonThread.longitude
                latitude = MonThread.latitude
                vitesse = MonThread.vitesse
                direction = MonThread.direction

                println("longitude : " + longitude)
                println("latitude : " + latitude)
                println("vitesse : " + vitesse)
                println("rotation : " + direction)
                println("--------------------")
                if (longitude != "" && latitude != "" && vitesse != "" && direction != ""){
                    drone1.position.x = longitude.toDouble()
                    drone1.position.y = latitude.toDouble()
                    drone1.position.direction = direction.toDouble()
                    drone1.vitesse = vitesse.toDouble()
                    this@Vue1 .runOnUiThread {
                        if (markerbateau != null) {
                            markerbateau.position = LatLng(drone1.position.x, drone1.position.y)
                            markerbateau.rotation = drone1.position.direction.toFloat()

                            markerbateau.snippet = "Vitesse : " + drone1.vitesse + " knots" + " -> " + round(drone1.vitesse * 1.852) + " km/h"
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

    override fun onTabSelected(tab: ActionBar.Tab?, ft: FragmentTransaction?) {
        TODO("Not yet implemented")
    }

    override fun onTabUnselected(tab: ActionBar.Tab?, ft: FragmentTransaction?) {
        TODO("Not yet implemented")
    }

    override fun onTabReselected(tab: ActionBar.Tab?, ft: FragmentTransaction?) {
        TODO("Not yet implemented")
    }
}