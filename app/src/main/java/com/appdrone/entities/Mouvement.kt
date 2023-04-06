package com.appdrone.entities

open abstract class Mouvement() {
    abstract fun avancer()
    abstract fun freiner()

    abstract fun urgence()

    abstract fun home()

    abstract fun demarrer()

}