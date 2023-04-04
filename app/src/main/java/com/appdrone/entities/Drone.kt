package com.appdrone.entities

import com.appdrone.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class Drone (var name: String): Mouvement() {

    var icon = BitmapDescriptorFactory.fromResource(R.drawable.icon)
    var position: Waypoint = Waypoint(0.0, 0.0, 0.0)
    var trajectoire: ArrayList<Waypoint> = ArrayList()
    fun calculTrajectoire() {

    }

    override fun avancer() {
        TODO("Not yet implemented")
    }

    override fun freiner() {
        TODO("Not yet implemented")
    }

    override fun droite() {
        TODO("Not yet implemented")
    }

    override fun gauche() {
        TODO("Not yet implemented")
    }

    override fun plonger() {
        TODO("Not yet implemented")
    }

    override fun remonter() {
        TODO("Not yet implemented")
    }

    override fun suivreTracjectoire() {
        TODO("Not yet implemented")
    }
}