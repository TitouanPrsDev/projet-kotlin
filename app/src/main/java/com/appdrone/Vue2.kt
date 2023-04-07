package com.appdrone

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.appdrone.databinding.ActivityVue2Binding
import com.appdrone.entities.Drone
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.SphericalUtil

import java.util.*

class Vue2 : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityVue2Binding
    val timer = Timer()
    val timer2 = Timer()

    private var doFirst:Boolean = false
    private var timerBool = true
    private var angle = 0.0
    private var vitesseGlobale = 0.1
    private var isGauche:Int = 1
    private lateinit var sensorManager: SensorManager
    var home: LatLng = LatLng(46.15507352200837, -1.154131833798576)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVue2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setUpSensorStuff()
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

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        // Création drone
        var drone1:Drone = Drone("Principal")
        // Position : vieux port
        drone1.position!!.longitude = home.longitude
        drone1.position!!.latitude = home.latitude
        drone1.orientation = 0.0
        drone1.vitesse = 0.1


        val markerbateau = mMap
            .addMarker(MarkerOptions()
                .position(LatLng(drone1.position!!.latitude, drone1.position!!.longitude))
                .title(drone1.name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon))
                .snippet(""))
        if (markerbateau != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerbateau.position, 8f))
        }

        val task = object : TimerTask() {
            override fun run() {
                drone1.orientation = angle
                this@Vue2.runOnUiThread {
                    if (markerbateau != null) {
                        markerbateau.position = LatLng(drone1.position!!.longitude, drone1.position!!.latitude)
                        markerbateau.rotation = drone1.orientation!!.toFloat()

                        markerbateau.snippet =
                            "Vitesse : " + drone1.vitesse + " knots" + " -> " + Math.round(
                                drone1.vitesse!! * 1.852
                            ) + " km/h"
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerbateau.position))
                    }
                }
            }
        }

        // timer 2 get angle new pos
        val task2 = object : TimerTask() {
            override fun run() {
                // knot -> m/s
                var metresecondes:Double = drone1.vitesse!! / 1.94384
                var nvPos: LatLng = getDestinationLatLng(LatLng(drone1.position!!.longitude, drone1.position!!.latitude), metresecondes , drone1.orientation!!)
                drone1!!.position!!.longitude = nvPos.latitude
                drone1!!.position!!.latitude = nvPos.longitude

                timerBool = true // remettre le boolean toutes les secondes pour l'acceleromètre
            }
        }
        timer2.schedule(task2, 0, 1000)
        timer.schedule(task, 0, 1000)
    }


    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            if (timerBool) { // Si une seconde est passée
                if (!doFirst) {
                    if (event.values[0] < 0) isGauche = -1
                    println("isGauche set to " + isGauche)
                    doFirst = true
                } else {
                    val accel: Float = event.values[1]
                    val diviseurVitesse:Int = (16 - (vitesseGlobale / 2.33)).toInt()
                    angle += (accel * 9.17 * isGauche) / diviseurVitesse // tourne + ou - vite selon vitesse
                    if (angle > 180) angle -= 180 // Remettre à zero
                    if (angle < -180) angle += 180 // Remettre à zero
                    timerBool = false
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
        timer.cancel()
        timer2.cancel()
        finish()
    }

    fun getDestinationLatLng(originLatLng: LatLng, distance: Double, heading: Double): LatLng {
        // Utilise la méthode computeOffset de SphericalUtil pour calculer les coordonnées du point de destination
        return SphericalUtil.computeOffset(originLatLng, distance, heading)
    }
}