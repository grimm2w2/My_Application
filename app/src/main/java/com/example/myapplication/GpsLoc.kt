package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.speech.tts.TextToSpeech
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import okhttp3.OkHttpClient
import okio.IOException
import kotlin.math.abs


class GpsLoc(mainActivity: MainActivity): LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2
    private var x = 0.0
    private var y = 0.0
    val mainthis : MainActivity = mainActivity
    private lateinit var mapView: MapView
    lateinit var placemark : PlacemarkMapObject
    var ck = 0
    private val placemarkTapListener = MapObjectTapListener { _, point ->
        Toast.makeText(
            mainthis,
            "вы по GPS",
            Toast.LENGTH_SHORT
        ).show()
        mapView.map.move(
            CameraPosition(
                /* target */ Point(x, y),
                /* zoom */ 15f,
                /* azimuth */ 0f,
                /* tilt */ 0f,
            ),
            Animation(Animation.Type.LINEAR, 1f),
            null
        )

        true
    }


    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)
        tvGpsLocation = mainthis.gps
        tvGpsLocation.text = mainthis.getString(R.string.koorGPS) + mainthis.getString(R.string.active)
    }
    override fun onProviderDisabled(provider: String) {
        super.onProviderDisabled(provider)
        tvGpsLocation = mainthis.gps
        tvGpsLocation.text = mainthis.getString(R.string.koorGPS) + mainthis.getString(R.string.noactive)
    }

    open fun getLocation(){
        locationManager = mainthis.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(mainthis,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(mainthis, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5f,this)
    }

    override fun onLocationChanged(location: Location){
        if (location.provider == LocationManager.GPS_PROVIDER) {
            tvGpsLocation = mainthis.gps
            tvGpsLocation.text = "" + location.latitude + "," + location.longitude
            x = location.latitude
            y = location.longitude
            mapView = mainthis.mapView
            val imageProvider = ImageProvider.fromResource(mainthis, R.drawable.ic_pin2)
            if(ck == 0){
                placemark = mapView.map.mapObjects.addPlacemark().apply {
                    geometry = com.yandex.mapkit.geometry.Point(location.latitude, location.longitude)
                    setIcon(imageProvider)
                    setText("ВыGPS")
                }
                mapView.map.move(
                    CameraPosition(
                        /* target */ Point(location.latitude, location.longitude),
                        /* zoom */ 15f,
                        /* azimuth */ 0f,
                        /* tilt */ 0f,
                    ),
                    Animation(Animation.Type.LINEAR, 1f),
                    null
                )
                ck = 1
            }
            else {
                placemark.geometry = Point(location.latitude, location.longitude)
            }



            placemark.addTapListener(placemarkTapListener)
            val client = OkHttpClient()

            val request = okhttp3.Request.Builder()
                .url("http://пвип.рф/php/location.php?name=igor&lat="+location.latitude+"&lon="+location.longitude+"&uuid="+mainthis.phone)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Запрос к серверу не был успешен:" +
                                " ${response.code} ${response.message}")
                    }
                    // пример получения конкретного заголовка ответа
                    println("Server: ${response.header("Server")}")
                    // вывод тела ответа
                    println(response.body!!.string())
                }
            } catch (e: IOException) {
                println("Ошибка подключения: $e");
            }
            var cn = 0
            for(lat in mainthis.coordLat){
                val x = lat - location.latitude
                if (abs(x) < 0.001){
                    val y = mainthis.coordLon[cn]-location.longitude
                    if(abs(y) < 0.001){

                        mainthis.tts!!.speak(mainthis.data[cn], TextToSpeech.QUEUE_FLUSH, null,"")
                    }
                }
                cn+=1
            }
        }
    }

    open fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                mainthis,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mainthis,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                mainthis,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationPermissionCode
            )
            false
        } else {
            true
        }
    }
}