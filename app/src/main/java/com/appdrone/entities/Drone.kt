package com.appdrone.entities

import com.appdrone.Utilitaire.Monthread
import com.appdrone.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class Drone (val name: String) : Mouvement() {

    var icon = BitmapDescriptorFactory.fromResource(R.drawable.icon)
    var position: Waypoint? = null
    var vitesse: Double? = null
    var direction: Double? = null

    init {
        val monThread : Monthread = Monthread()
        monThread.start()
        runBlocking {
            delay(1000)
            position = Waypoint(0.0, 0.0)
            position!!.x = monThread.longitude.toDouble()
            position!!.y = monThread.latitude.toDouble()
            vitesse = monThread.vitesse.toDouble()
            direction = monThread.direction.toDouble()
        }
    }
    override fun avancer() {
        TODO("Not yet implemented")
    }

    override fun freiner() {
        TODO("Not yet implemented")
    }

    override fun urgence() {
        TODO("Not yet implemented")
    }

    override fun home() {
        TODO("Not yet implemented")
    }

    override fun demarrer() {
        TODO("Not yet implemented")
    }


}