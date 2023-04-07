package com.appdrone

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.appdrone.databinding.ActivityVue3Binding
import com.appdrone.entities.Drone
import com.appdrone.entities.Waypoint
import com.appdrone.Utilitaire.Parsergpx
import java.io.File
import java.io.FileOutputStream

class Vue3 : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityVue3Binding
    private val tab : ArrayList<Waypoint> = ArrayList<Waypoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVue3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val buttonFichier = findViewById<Button>(R.id.buttonSuivi)

        buttonFichier.setOnClickListener {
            val parser = Parsergpx()

            val fos : FileOutputStream = openFileOutput("test.gpx", Context.MODE_PRIVATE)
            parser.writeToFile(fos, "fichier1", "test", tab)
            fos.close()
        }
    }

    override fun onMapClick(latLng: LatLng) {
        mMap.addMarker(MarkerOptions().position(latLng))
        tab.add(Waypoint(latLng.longitude, latLng.latitude))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val drone = Drone("drone1")
        mMap.setOnMapClickListener(this)


        val markerbateau = mMap.addMarker(MarkerOptions().position(LatLng(0.00, 0.00)).title(drone.name).icon(drone.icon).snippet(""))
        if (markerbateau != null) {
            markerbateau.position = LatLng(drone.position!!.longitude.toDouble(), drone.position!!.latitude.toDouble())
            markerbateau.rotation = drone.orientation!!.toFloat()
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerbateau.position))
        }
    }

}