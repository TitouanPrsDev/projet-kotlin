package com.appdrone

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.SeekBar

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
import kotlin.math.*

class Vue2 : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityVue2Binding
    val timer = Timer()
    val timer2 = Timer()

    private var doFirst:Boolean = false
    private var timerBool = true
    private var angle = 0.0
    private var vitesseGlobale = 0.0
    private var isGauche:Int = 1
    private lateinit var sensorManager: SensorManager
    private var home: LatLng = LatLng(46.15507352200837, -1.154131833798576)
    private var positionGlobale: LatLng = home

    private var stopAccelerometre:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVue2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // seekbar vitesse
        val vitesseBar: SeekBar = findViewById(R.id.vitessebar)
        vitesseBar.min = 0
        vitesseBar.max = 70

        vitesseBar.setOnSeekBarChangeListener(object:
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromuser: Boolean) {
                vitesseGlobale = progress.toDouble()
            }


            // fonctions obligatoires à implémenter pour l'interface
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        // boutons + listeners

        val buttonHome = findViewById<Button>(R.id.buttonHome)
        val buttonUrgence = findViewById<Button>(R.id.buttonUrgence)

        buttonHome.setOnClickListener {
            println("Home !")
            val distanceLong = home.longitude - positionGlobale.longitude

            /*
            val angleDirection = Math.toDegrees(Math.atan2(distanceLat, distanceLong))
            println("lat : " + distanceLat + " long : " + distanceLong)
            println("direction : " + angleDirection + " | angle : " + angle)

            */

            // Position initiale (latitude et longitude)
            val lat1 = 46.16025161517004
            val lon1 = -1.156863590985258

// Position d'arrivée (latitude et longitude)
            val lat2 = 46.15507352200837
            val lon2 = -1.154131833798576

// Rayon de la Terre en kilomètres
            val R = 6371.0

// Calcul de la différence de longitude
            val deltaLon = Math.toRadians(lon2 - lon1)

// Calcul de la distance entre les deux points en kilomètres
            val lat1Radians = Math.toRadians(lat1)
            val lat2Radians = Math.toRadians(lat2)
            val deltaLat = lat2Radians - lat1Radians
            val a = sin(deltaLat / 2).pow(2.0) + cos(lat1Radians) * cos(lat2Radians) * sin(deltaLon / 2).pow(2.0)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val distance = R * c

// Calcul de l'angle en radians
            val y = sin(deltaLon) * cos(lat2Radians)
            val x = cos(lat1Radians) * sin(lat2Radians) - sin(lat1Radians) * cos(lat2Radians) * cos(deltaLon)
            var angleDirection = atan2(y, x)

// Conversion en degrés
            angleDirection = Math.toDegrees(angleDirection)
            if (angleDirection < 0) {
                angleDirection += 360
            }

            angleDirection = (angleDirection + 180) % 360

            println("Angle: $angleDirection")
            println("direction : " + angleDirection + " | angle : " + angle)
            stopAccelerometre = true
            angle = angleDirection
        }

        buttonUrgence.setOnClickListener {
            println("Urgence !")
            vitesseGlobale = 0.0
        }


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
        drone1.position!!.latitude = home.latitude
        drone1.position!!.longitude = home.longitude
        drone1.orientation = 0.0
        drone1.vitesse = 0.0


        val markerbateau = mMap.addMarker(MarkerOptions()
            .position(LatLng(0.00, 0.00))
            .title(drone1.name)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon)).snippet(""))
        if (markerbateau != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerbateau.position, 8f))
        }

        val task = object : TimerTask() {
            override fun run() {
                drone1.orientation = angle
                drone1.vitesse = vitesseGlobale
                drone1.position!!.latitude = positionGlobale.latitude
                drone1.position!!.longitude = positionGlobale.longitude

                this@Vue2.runOnUiThread {
                    if (markerbateau != null) {
                        markerbateau.position = LatLng(drone1.position!!.latitude, drone1.position!!.longitude)
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
                val metresecondes:Double = drone1.vitesse!! / 1.94384
                val nvPos: LatLng = getDestinationLatLng(LatLng(drone1.position!!.latitude, drone1.position!!.longitude), metresecondes , drone1.orientation!!)
                positionGlobale = nvPos

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
            if (timerBool && !stopAccelerometre) { // Si une seconde est passée et que bouton Home pas appuyé
                if (!doFirst) {
                    if (event.values[0] < 0) isGauche = -1
                    println("isGauche set to " + isGauche)
                    doFirst = true
                } else {
                    val accel: Float = event.values[1]
                    var diviseurVitesse:Int = (16 - (vitesseGlobale / 2.33)).toInt()
                    if(diviseurVitesse < 1) diviseurVitesse = 1
                    if (vitesseGlobale == 0.0) diviseurVitesse = 9999999
                    angle += (accel * 9.17 * isGauche) / diviseurVitesse // tourne + ou - vite selon vitesse
                    if (angle > 360) angle -= 360 // Remettre à zero
                    if (angle < -360) angle += 360 // Remettre à zero
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