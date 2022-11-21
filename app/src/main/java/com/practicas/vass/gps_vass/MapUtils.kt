package com.practicas.vass.gps_vass

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

object MapUtils {

    private fun getDestination(): LatLng = LatLng(37.0251, -3.6230)

    fun setupMap (context: Context, map:GoogleMap){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(getDestination(),15f))
        map.uiSettings.apply {
            isMyLocationButtonEnabled = false
            isZoomGesturesEnabled = false
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
            isMapToolbarEnabled = false
        }

    }

    fun addPolyline(map: GoogleMap, locations: MutableList<LatLng>){
        map.addPolyline(PolylineOptions()
            .width(16f)
            .color(Color.GREEN)
            .jointType(JointType.ROUND)
            .startCap(RoundCap())
            .addAll(locations)
        )

    }

    fun createPolylines(map: GoogleMap) {
        val polylineOptions = PolylineOptions()
            .add(LatLng(37.0243684776579, -3.6232383048608767))
            .add(LatLng(37.0241457695855, -3.62379620433569))
            .add(LatLng(37.02384596922572, -3.6235708987784108))
            .add(LatLng(37.024085809608195, -3.6230344569755175))
            .width(15f)
            .color(Color.RED)

        val polyline = map.addPolyline(polylineOptions)

        polyline.isClickable = true
        map.setOnPolylineClickListener { polyline -> changeColorLine(polyline) }
    }

   private fun changeColorLine(polyline: Polyline) {
        val color = (0..3).random()
        when (color) {
            0 -> polyline.color = Color.BLUE
            1 -> polyline.color = Color.GREEN
            2 -> polyline.color = Color.CYAN
            3 -> polyline.color = Color.MAGENTA
        }
    }

    private fun createMarker(map: GoogleMap) {
        val coordinates = LatLng(40.416640, -3.703902)
        val marker = MarkerOptions().position(coordinates).title("Mi humilde morada")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 20f),
            4000,
            null
        )
    }


}