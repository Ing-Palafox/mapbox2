package com.example.mapbox

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.dsl.cameraOptions
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest


class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var placeInput: EditText
    private lateinit var coordinatesInput: EditText
    private lateinit var searchLocationButton: Button
    private lateinit var locationText: TextView
    private lateinit var getLocationButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        placeInput = findViewById(R.id.placeInput)
        coordinatesInput = findViewById(R.id.coordinatesInput)
        searchLocationButton = findViewById(R.id.searchLocationButton)
        locationText = findViewById(R.id.locationText)
        getLocationButton = findViewById(R.id.getLocationButton)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)

        getLocationButton.setOnClickListener {
            requestLocationPermission()
        }

        searchLocationButton.setOnClickListener {
            val coordinates = coordinatesInput.text.toString().split(",")
            if (coordinates.size == 2) {
                val latitude = coordinates[0].trim().toDoubleOrNull()
                val longitude = coordinates[1].trim().toDoubleOrNull()

                if (latitude != null && longitude != null) {
                    setLocationOnMap(latitude, longitude)
                } else {
                    locationText.text = "Coordenadas inválidas. Por favor, inténtalo de nuevo."
                }
            } else {
                val placeName = placeInput.text.toString()
                if (placeName.isNotEmpty()) {
                    searchForPlace(placeName)
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            obtenerUbicacion()
        } else {
            locationText.text = "Permiso denegado. No se puede obtener la ubicación."
        }
    }

    private fun obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    locationText.text = "Latitud: $latitude, Longitud: $longitude"
                    setLocationOnMap(latitude, longitude)
                } else {
                    locationText.text = "No se pudo obtener la ubicación."
                }
            }
        }
    }

    private fun setLocationOnMap(latitude: Double, longitude: Double) {
        mapView.getMapboxMap().setCamera(
            cameraOptions {
                center(Point.fromLngLat(longitude, latitude))
                zoom(15.0)
            }
        )
        addRedMarker(latitude, longitude)
    }

    private fun searchForPlace(placeName: String) {
        // Aquí usarías una API geocoding para obtener las coordenadas del lugar
        // Por ejemplo, podrías usar la API de Mapbox Geocoding
        // Este es un ejemplo ficticio y debes reemplazarlo con la llamada real a la API
        val fakeLatitude = 21.50951
        val fakeLongitude = -104.89569

        setLocationOnMap(fakeLatitude, fakeLongitude)
    }

    private fun addRedMarker(latitude: Double, longitude: Double) {
        bitmapFromDrawableRes(this@MainActivity, R.drawable.red_marker)?.let {
            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()

            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withIconImage(it)

            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, resourceId: Int): Bitmap? {
        return convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))
    }

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}

