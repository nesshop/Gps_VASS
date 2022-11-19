package com.practicas.vass.gps_vass

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

object MapUtils {

    fun getDestination(): LatLng = LatLng(37.0251, -3.6230)

    fun getOrigin(): LatLng = LatLng(37.0238, -3.62336)

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


}