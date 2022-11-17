package com.practicas.vass.gps_vass

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.practicas.vass.gps_vass.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener, OnMyLocationClickListener {


    private lateinit var map: GoogleMap
    private lateinit var binding:ActivityMainBinding
    private val coordinates = LatLng(37.0251, -3.6230)

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.startRoute.setOnClickListener {
            if (::map.isInitialized){


            }
        }

        createFragment()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //createMarker()
        createPolylines()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableLocation()

    }

    private fun createMarker() {
        val coordinates = LatLng(40.416640, -3.703902)
        val marker = MarkerOptions().position(coordinates).title("Mi humilde morada")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 20f),
            4000,
            null
        )
    }

    private fun createPolylines(){
        val polylineOptions = PolylineOptions()
            .add(LatLng( 37.0243684776579,-3.6232383048608767))
            .add(LatLng(37.0241457695855,-3.62379620433569 ))
            .add(LatLng(37.02384596922572,-3.6235708987784108 ))
            .add(LatLng(37.024085809608195,-3.6230344569755175 ))
            .width(15f)
            .color(ContextCompat.getColor(this,R.color.purple_200))

        val polyline = map.addPolyline(polylineOptions)

        polyline.isClickable = true
        map.setOnPolylineClickListener { polyline-> changeColorLine(polyline) }
    }
    fun changeColorLine(polyline: Polyline){
        val color = (0..3).random()
        when(color){
            0 -> polyline.color = ContextCompat.getColor(this,R.color.teal_200)
            1 -> polyline.color = ContextCompat.getColor(this,R.color.purple_700)
            2 ->polyline.color = ContextCompat.getColor(this, R.color.teal_700)
            3 ->polyline.color = ContextCompat.getColor(this, R.color.purple_500)
        }
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

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
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::map.isInitialized) return
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this,"Botón pulsado", Toast.LENGTH_SHORT).show()
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 20f),
            4000,
            null
        )
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this,"Estás en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }
}