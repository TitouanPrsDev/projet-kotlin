package com.appdrone.entities

import com.appdrone.Utilitaire.Monthread
import com.appdrone.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class Drone (val name: String) {

    var icon = BitmapDescriptorFactory.fromResource(R.drawable.icon)
    var position: Waypoint? = null
    var vitesse: Double? = null
    var direction: Double? = null

    init {
        val monThread : Monthread = Monthread()
        monThread.start()
        Thread.sleep(1000)
        position = Waypoint(0.0, 0.0)
        position!!.x = monThread.longitude.toDouble()
        position!!.y = monThread.latitude.toDouble()
        vitesse = monThread.vitesse.toDouble()
        direction = monThread.direction.toDouble()
        monThread.interrupt()
    }
}