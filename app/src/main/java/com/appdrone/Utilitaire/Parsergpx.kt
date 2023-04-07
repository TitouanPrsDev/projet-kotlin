package com.appdrone.Utilitaire

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.appdrone.entities.Drone
import com.appdrone.entities.Waypoint
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter


class Parsergpx {

    fun writeToFile(fos: FileOutputStream, name: String, desc: String, trajectoire : ArrayList<Waypoint>, drone : Drone) {

        try {

            fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".toByteArray())
            fos.write("<gpx version=\"1.1\" creator=\"My Application\">\n".toByteArray())
            fos.write("\t<metadata>\n".toByteArray())
            fos.write("\t\t<name>$name</name>\n".toByteArray())
            fos.write("\t\t<desc>$desc</desc>\n".toByteArray())
            fos.write("\t</metadata>\n".toByteArray())
            fos.write("\t<trk>\n".toByteArray())
            fos.write("\t\t<name>$name</name>\n".toByteArray())
            fos.write("\t\t<trkseg>\n".toByteArray())

            for (point in trajectoire) {
                calculTrajet(LatLng(point.latitude,point.longitude),5.0,drone)
                fos.write("\t\t\t<trkpt lon=\"${point.longitude}\" lat=\"${point.latitude}\">\n".toByteArray())
                fos.write("\t\t\t\t<time></time>\n".toByteArray())
                fos.write("\t\t\t</trkpt>\n".toByteArray())
            }

            fos.write("\t\t</trkseg>\n".toByteArray())
            fos.write("\t</trk>\n".toByteArray())
            fos.write("</gpx>\n".toByteArray())

        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
    fun calculTrajet(lnglat : LatLng, vitesse : Double, drone : Drone) : Double{

        val metreSeconds = vitesse * 0.514444
        val distance : Double = SphericalUtil.computeDistanceBetween(LatLng(drone.position!!.latitude,drone.position!!.longitude), lnglat)

        val temps : Double = distance / metreSeconds // temps en seconds
        val tempsHeure  = temps/3600 // temps en heure
        println("distance : $distance")
        println("temps : $temps")
        println("tempsHeure : $tempsHeure")
        return tempsHeure

    }
}
