package es.uji.rutaspike

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.MapLibre
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

private const val MAP_STYLE_URL = "https://demotiles.maplibre.org/style.json"
private const val ROUTE_SOURCE_ID = "calculated-route-source"
private const val ROUTE_LAYER_ID = "calculated-route-layer"

/** Prueba de MapLibre Native integrada en Compose mediante AndroidView. */
@Composable
fun RouteMap(route: RouteResult) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val points = remember(route.geometry) {
        route.geometry.filter { it.size >= 2 }.map { Point.fromLngLat(it[0], it[1]) }
    }

    val mapView = remember(context) {
        MapLibre.getInstance(context.applicationContext)
        MapView(context)
    }
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleLoaded by remember { mutableStateOf(false) }

    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            if (lifecycle.currentState != Lifecycle.State.DESTROYED) {
                mapView.onPause()
                mapView.onStop()
                mapView.onDestroy()
            }
        }
    }

    LaunchedEffect(mapView) {
        mapView.getMapAsync { readyMap ->
            map = readyMap
            readyMap.setStyle(MAP_STYLE_URL) { styleLoaded = true }
        }
    }

    LaunchedEffect(map, styleLoaded, points) {
        val readyMap = map ?: return@LaunchedEffect
        if (!styleLoaded || points.size < 2) return@LaunchedEffect
        val style = readyMap.style ?: return@LaunchedEffect
        val featureCollection = FeatureCollection.fromFeature(
            Feature.fromGeometry(LineString.fromLngLats(points))
        )
        val source = style.getSourceAs<GeoJsonSource>(ROUTE_SOURCE_ID)
        if (source == null) {
            style.addSource(GeoJsonSource(ROUTE_SOURCE_ID, featureCollection))
            style.addLayer(
                LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID).withProperties(
                    lineColor(android.graphics.Color.BLUE),
                    lineWidth(5f)
                )
            )
        } else {
            source.setGeoJson(featureCollection)
        }

        val start = points.first()
        val end = points.last()
        val center = LatLng((start.latitude() + end.latitude()) / 2, (start.longitude() + end.longitude()) / 2)
        readyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 8.0))
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}
