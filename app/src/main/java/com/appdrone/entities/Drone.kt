package com.appdrone.entities

import com.appdrone.Utilitaire.Monthread
import com.appdrone.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class Drone(val name: String) {

    var icon : BitmapDescriptor? = null
    var position: Waypoint? = null
    var vitesse: Double? = null
    var orientation: Double? = null

    val threadInit: Monthread = Monthread()

    init {
        threadInit.name = "Drone init"
        threadInit.start()
        Thread.sleep(1000)
        icon = BitmapDescriptorFactory.fromResource(R.drawable.icon)
        position = Waypoint(0.0, 0.0)
        position!!.longitude = threadInit.longitude.toDouble()
        position!!.latitude = threadInit.latitude.toDouble()
        vitesse = threadInit.vitesse.toDouble()
        orientation = threadInit.direction.toDouble()
    }

    fun update() {
        position!!.longitude = threadInit.longitude.toDouble()
        position!!.latitude = threadInit.latitude.toDouble()
        vitesse = threadInit.vitesse.toDouble()
        orientation = threadInit.direction.toDouble()
    }
}