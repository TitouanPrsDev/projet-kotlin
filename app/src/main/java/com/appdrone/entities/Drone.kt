package com.appdrone.entities

class Drone (
    private var name: String,
    private var position: WayPoint,
    private var trajectoire: List<WayPoint>
        ): Mouvement() {
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