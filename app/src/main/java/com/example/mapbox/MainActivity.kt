package com.example.mapbox

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.maps.dsl.cameraOptions

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var locationText: TextView
    private lateinit var getLocationButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a las vistas
        mapView = findViewById(R.id.mapView)
        locationText = findViewById(R.id.locationText)
        getLocationButton = findViewById(R.id.getLocationButton)

        // Inicializar cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configurar MapView
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)

        // Pedir permiso al pulsar el botón
        getLocationButton.setOnClickListener {
            requestLocationPermission()
        }
    }

    // Método para pedir permisos de ubicación
    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion()
        } else {
            // Solicitar permiso al usuario
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Registro del manejador de resultados del permiso
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            obtenerUbicacion()
        } else {
            locationText.text = "Permiso denegado. No se puede obtener la ubicación."
        }
    }

    // Obtener ubicación del dispositivo
    private fun obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    locationText.text = "Latitud: $latitude, Longitud: $longitude"

                    // Centrar el mapa en la ubicación actual
                    mapView.getMapboxMap().setCamera(
                        cameraOptions {
                            center(Point.fromLngLat(longitude, latitude))
                            zoom(15.0)
                        }
                    )

                    // Agregar el marcador en la ubicación actual
                    addRedMarker(latitude, longitude)
                } else {
                    locationText.text = "No se pudo obtener la ubicación."
                }
            }
        }
    }

    // Agregar el marcador rojo en el mapa
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

    // Convertir el drawable en bitmap
    private fun bitmapFromDrawableRes(context: Context, resourceId: Int): Bitmap? {
        return convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))
    }

    // Convertir un drawable a bitmap
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

    // Ciclo de vida del MapView
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
