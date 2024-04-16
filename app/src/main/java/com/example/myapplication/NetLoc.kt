package com.example.myapplication

import android.widget.TextView
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class NetLoc(mainActivity: MainActivity) : LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var tvNetLocation: TextView
    private lateinit var lang: Button
    private val locationPermissionCode = 2
    val mainthis : MainActivity = mainActivity

    private var ck = 1

    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)
        tvNetLocation = mainthis.net
        tvNetLocation.text = mainthis.getString(R.string.koorSET) + mainthis.getString(R.string.active)
    }

    override fun onProviderDisabled(provider: String) {
        super.onProviderDisabled(provider)
        tvNetLocation = mainthis.net
        tvNetLocation.text = mainthis.getString(R.string.koorSET) + mainthis.getString(R.string.noactive)
    }

    open fun getLocation(){
        locationManager = mainthis.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(mainthis,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(mainthis, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location){
        if(location.provider == LocationManager.NETWORK_PROVIDER)
        {
            tvNetLocation = mainthis.net
            tvNetLocation.text = ""+ location.latitude+ "," + location.longitude

        }
    }
}