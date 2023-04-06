package com.appdrone.Utilitaire

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.appdrone.entities.Waypoint
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter


class Parsergpx {

    fun writeToFile(fos: FileOutputStream, name: String, desc: String, trajectoire : ArrayList<Waypoint>) {

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
                fos.write("\t\t\t<trkpt lat=\"${point.x}\" lon=\"${point.y}\">\n".toByteArray())
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
}
