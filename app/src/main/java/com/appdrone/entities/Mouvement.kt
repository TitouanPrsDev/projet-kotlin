package com.appdrone.entities

open abstract class Mouvement() {
    abstract fun avancer()
    abstract fun freiner()
    abstract fun droite()
    abstract fun gauche()
    abstract fun plonger()
    abstract fun remonter()
    abstract fun suivreTracjectoire()
}