package com.practicas.vass.gps_vass

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.practicas.vass.gps_vass.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    OnMyLocationClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    private var locations = mutableListOf<LatLng>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    private var locationCallback = object :LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.run {
                this.forEach{locations.add(LatLng(it.latitude, it.longitude))}

               MapUtils.addPolyline(map,locations)
            }
        }

    }
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createFragment()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        binding.btnStart.setOnClickListener {

            if (::map.isInitialized) {

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                val locationRequest = LocationRequest.create()
                    .setInterval(5_000)
                    .setFastestInterval(2_000)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
            map.clear()
            locations.clear()
        }
        binding.btnErase.setOnClickListener {
            map.clear()
            locations.clear()
        }

        binding.btnStop.setOnClickListener {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)

        }

        binding.btnPhoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)

        }
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableLocation()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)

    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    
    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }

    }

    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Botón pulsado", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Estás en ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
    }
}