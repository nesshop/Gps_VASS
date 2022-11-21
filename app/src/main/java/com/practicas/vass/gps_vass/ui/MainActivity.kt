package com.practicas.vass.gps_vass.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.practicas.vass.gps_vass.R
import com.practicas.vass.gps_vass.databinding.ActivityMainBinding
import com.practicas.vass.gps_vass.utils.Locations
import com.practicas.vass.gps_vass.utils.MapUtils

class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    OnMyLocationClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private var locations = mutableListOf<LatLng>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //MVVM
    private lateinit var mainActivityViewModel: MainViewModel

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

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
        setupViewModel()
        setupObservers()
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

        binding.btnPhoto.setOnClickListener{
            startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            val imageView = binding.imgPhoto
            imageView.setImageBitmap(imageBitmap)

            map.addMarker(MarkerOptions().position(Locations.padul).icon(BitmapDescriptorFactory.fromBitmap(imageBitmap)))
        }
    }

    private fun setupViewModel() {
        mainActivityViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private fun setupObservers() {
        mainActivityViewModel.mainActivity.observe(this){

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
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION
            )
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