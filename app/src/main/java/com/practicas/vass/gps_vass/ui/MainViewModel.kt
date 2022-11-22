package com.practicas.vass.gps_vass.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng


class MainViewModel : ViewModel() {

    private var _coordinates: MutableLiveData<List<LatLng>> = MutableLiveData()
    var coordinates: LiveData<List<LatLng>> = _coordinates

}