package es.uji.wise.maps.ui.map

import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import es.uji.wise.maps.model.PlaceOfInterest
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

private const val MAP_STYLE_URL = "https://demotiles.maplibre.org/style.json"
private const val SAVED_PLACES_SOURCE_ID = "wise-saved-places-source"
private const val SAVED_PLACES_LAYER_ID = "wise-saved-places-layer"
private const val SELECTED_POINT_SOURCE_ID = "wise-selected-point-source"
private const val SELECTED_POINT_LAYER_ID = "wise-selected-point-layer"

private val DEFAULT_CENTER_UJI = LatLng(39.9945, -0.0688)
private const val DEFAULT_ZOOM = 10.5

/**
 * Componente interactivo de mapa basado en MapLibre GL Native (SDK 13.5.3).
 * Permite pulsar sobre el mapa para capturar coordenadas (HU-05) y renderiza
 * los marcadores de los lugares guardados y del punto seleccionado.
 */
@Composable
fun WiseMapView(
    places: List<PlaceOfInterest>,
    selectedLat: Double?,
    selectedLon: Double?,
    focusedPlace: PlaceOfInterest?,
    onMapClick: (lat: Double, lon: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val latestOnMapClick by rememberUpdatedState(onMapClick)

    val mapView = remember(context) {
        MapLibre.getInstance(context.applicationContext)
        MapView(context)
    }

    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleLoaded by remember { mutableStateOf(false) }

    DisposableEffect(lifecycle, mapView) {
        var created = false
        var destroyed = false

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    if (!created) {
                        mapView.onCreate(null)
                        created = true
                    }
                }
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> {
                    if (!destroyed) {
                        mapView.onDestroy()
                        destroyed = true
                    }
                }
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            if (!destroyed) {
                mapView.onPause()
                mapView.onStop()
                mapView.onDestroy()
                destroyed = true
            }
        }
    }

    LaunchedEffect(mapView) {
        mapView.getMapAsync { readyMap ->
            map = readyMap
            readyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_CENTER_UJI, DEFAULT_ZOOM))
            readyMap.addOnMapClickListener { latLng ->
                latestOnMapClick(latLng.latitude, latLng.longitude)
                true
            }
            readyMap.setStyle(MAP_STYLE_URL) {
                styleLoaded = true
            }
        }
    }

    LaunchedEffect(map, styleLoaded, places, selectedLat, selectedLon) {
        val readyMap = map ?: return@LaunchedEffect
        if (!styleLoaded) return@LaunchedEffect
        val style = readyMap.style ?: return@LaunchedEffect

        val savedFeatures = places.map { place ->
            Feature.fromGeometry(Point.fromLngLat(place.longitud, place.latitud))
        }
        val savedCollection = FeatureCollection.fromFeatures(savedFeatures)
        val savedSource = style.getSourceAs<GeoJsonSource>(SAVED_PLACES_SOURCE_ID)
        if (savedSource == null) {
            style.addSource(GeoJsonSource(SAVED_PLACES_SOURCE_ID, savedCollection))
            style.addLayer(
                CircleLayer(SAVED_PLACES_LAYER_ID, SAVED_PLACES_SOURCE_ID).withProperties(
                    circleColor(AndroidColor.parseColor("#2B6A6C")),
                    circleRadius(8f),
                    circleStrokeColor(AndroidColor.WHITE),
                    circleStrokeWidth(2.5f)
                )
            )
        } else {
            savedSource.setGeoJson(savedCollection)
        }

        val selectedFeatures = if (selectedLat != null && selectedLon != null) {
            listOf(Feature.fromGeometry(Point.fromLngLat(selectedLon, selectedLat)))
        } else {
            emptyList()
        }
        val selectedCollection = FeatureCollection.fromFeatures(selectedFeatures)
        val selectedSource = style.getSourceAs<GeoJsonSource>(SELECTED_POINT_SOURCE_ID)
        if (selectedSource == null) {
            style.addSource(GeoJsonSource(SELECTED_POINT_SOURCE_ID, selectedCollection))
            style.addLayer(
                CircleLayer(SELECTED_POINT_LAYER_ID, SELECTED_POINT_SOURCE_ID).withProperties(
                    circleColor(AndroidColor.parseColor("#B80D48")),
                    circleRadius(9.5f),
                    circleStrokeColor(AndroidColor.parseColor("#F29724")),
                    circleStrokeWidth(3f)
                )
            )
        } else {
            selectedSource.setGeoJson(selectedCollection)
        }
    }

    LaunchedEffect(map, styleLoaded, focusedPlace) {
        val readyMap = map ?: return@LaunchedEffect
        val target = focusedPlace ?: return@LaunchedEffect
        if (!styleLoaded) return@LaunchedEffect
        readyMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(target.latitud, target.longitud),
                12.5
            )
        )
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
