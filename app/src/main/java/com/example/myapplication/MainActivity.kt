package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.speech.tts.TextToSpeech
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.beust.klaxon.Klaxon
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.Locale


class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener{
    lateinit var gpsloc: GpsLoc
    lateinit var netloc: NetLoc
    lateinit var net: TextView
    lateinit var gps: TextView
    lateinit var mapView: MapView
    private lateinit var discription: TextView
    public var tts: TextToSpeech? = null
    val coordLat = ArrayList<Double>()
    val coordLon = ArrayList<Double>()
    val data = ArrayList<String>()
    var phone ="";
    val placemarkTapListener = MapObjectTapListener { _, point ->
        check_loc("Памятник Ленину")
        mapView.map.move(
            CameraPosition(
                /* target */ Point(53.3468392, 83.7771433),
                /* zoom */ 15f,
                /* azimuth */ 0f,
                /* tilt */ 0f,
            ),
            Animation(Animation.Type.LINEAR, 1f),
            null
        )
        this.tts!!.speak(this.discription.text, TextToSpeech.QUEUE_FLUSH, null,"")
        true
    }
    val placemarkTapListener2 = MapObjectTapListener { _, point ->
        check_loc("Красная площадь")
        mapView.map.move(
            CameraPosition(
                /* target */ Point(55.7539303, 37.620795),
                /* zoom */ 15f,
                /* azimuth */ 0f,
                /* tilt */ 0f,
            ),
            Animation(Animation.Type.LINEAR, 1f),
            null
        )
        this.tts!!.speak(this.discription.text, TextToSpeech.QUEUE_FLUSH, null,"")
        true
    }
    val placemarkTapListener3 = MapObjectTapListener { _, point ->
        check_loc("Бутлегер")
        mapView.map.move(
            CameraPosition(
                /* target */ Point(53.3477077, 83.5922348),
                /* zoom */ 15f,
                /* azimuth */ 0f,
                /* tilt */ 0f,
            ),
            Animation(Animation.Type.LINEAR, 1f),
            null
        )
        this.tts!!.speak(this.discription.text, TextToSpeech.QUEUE_FLUSH, null,"")
        true
    }
    val placemarkTapListener4 = MapObjectTapListener { _, point ->
        check_loc("Дворец независимости")
        mapView.map.move(
            CameraPosition(
                /* target */ Point(53.9271814, 27.5223484),
                /* zoom */ 15f,
                /* azimuth */ 0f,
                /* tilt */ 0f,
            ),
            Animation(Animation.Type.LINEAR, 1f),
            null
        )
        this.tts!!.speak(this.discription.text, TextToSpeech.QUEUE_FLUSH, null,"")
        true
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val db = DBHelper(this, null)
        db.deleteAll()
        MapKitFactory.setApiKey("efde69ac-9b73-4823-977e-42029959b1da")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapview)
        db.addName("Памятник ползунова","53.344814,83.781438")
        db.addName("АЛТГТУ","53.345205,83.78221")
        db.addName("Иди работай, сука","53.3327414,83.75344366")
        db.addName("Дом","53.34174729,83.7585973")

        val cursor = db.getName()
        var text: String

        cursor!!.moveToFirst()
        text = (cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.GPS_COL)))
        val coord1 = text.split(",")
        coordLat.add(coord1[0].toDouble())
        coordLon.add(coord1[1].toDouble())
        text = (cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.Network_COL)))
        data.add(text)
        while(cursor.moveToNext()){
            text = (cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.GPS_COL)))
            val coord2 = text.split(",")
            coordLat.add(coord2[0].toDouble())
            coordLon.add(coord2[1].toDouble())
            text = (cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.Network_COL)))
            data.add(text)
        }

        cursor.close()
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE
                ),
                2
            )
        }
        phone = telephonyManager.line1Number

        net = this.findViewById(R.id.textView1)
        gps = this.findViewById(R.id.textView3)
        discription = this.findViewById(R.id.textView2)
        gpsloc = GpsLoc(this)
        netloc = NetLoc(this)
        gpsloc.isLocationPermissionGranted()
        gpsloc.getLocation()
        netloc.getLocation()
        tts = TextToSpeech(this, this)

        val imageProvider = ImageProvider.fromResource(this, R.drawable.ic_pin2)
        var placemark = mapView.map.mapObjects.addPlacemark().apply {
            geometry = com.yandex.mapkit.geometry.Point(53.3468392, 83.7771433)
            setIcon(imageProvider)
            setText("Памятник Ленину")
        }
        placemark.addTapListener(placemarkTapListener)

        var placemark2 = mapView.map.mapObjects.addPlacemark().apply {
            geometry = com.yandex.mapkit.geometry.Point(55.7539303, 37.620795)
            setIcon(imageProvider)
            setText("Красная площадь")
        }
        placemark2.addTapListener(placemarkTapListener2)

        var placemark3 = mapView.map.mapObjects.addPlacemark().apply {
            geometry = com.yandex.mapkit.geometry.Point(53.3477077, 83.5922348)
            setIcon(imageProvider)
            setText("Бутлегер")
        }
        placemark3.addTapListener(placemarkTapListener3)

        var placemark4 = mapView.map.mapObjects.addPlacemark().apply {
            geometry = com.yandex.mapkit.geometry.Point(53.9271814, 27.5223484)
            setIcon(imageProvider)
            setText("Дворец независимости")
        }
        placemark4.addTapListener(placemarkTapListener4)



    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale("ru")

            val result = tts!!.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            }
        }
    }

    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
    fun check_loc(input: String){
        val client = OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("http://пвип.рф/php/seven_places.php?name="+input)
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
                val result = Klaxon()
                    .parseArray<Loc>(response.body!!.string())
                discription.text = result!![0].dis

            }
        } catch (e: IOException) {
            println("Ошибка подключения: $e");
        }
    }
    data class Loc(val id: String,val name: String, val lat: String,val lon: String,val dis: String)


}