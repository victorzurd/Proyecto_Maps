package es.uji.rutaspike

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

private interface OrsApi {
    @GET("geocode/search")
    suspend fun geocode(
        @Query("text") place: String,
        @Query("size") size: Int = 1,
        @Query("boundary.country") country: String = "ESP",
        @Query("api_key") apiKey: String
    ): GeocodeResponse

    @POST("v2/directions/{profile}/geojson")
    suspend fun directions(
        @Path("profile") profile: String,
        @Header("Authorization") apiKey: String,
        @Body request: DirectionsRequest
    ): DirectionsResponse
}

private data class DirectionsRequest(val coordinates: List<List<Double>>)
private data class GeocodeResponse(val features: List<GeocodeFeature> = emptyList())
private data class GeocodeFeature(val geometry: GeocodeGeometry)
private data class GeocodeGeometry(val coordinates: List<Double>)
private data class DirectionsResponse(val features: List<RouteFeature> = emptyList())
private data class RouteFeature(val properties: RouteProperties)
private data class RouteProperties(val summary: RouteSummary)
private data class RouteSummary(val distance: Double, val duration: Double)

/** Adapta las respuestas GeoJSON de OpenRouteService al modelo sencillo de la app. */
class OpenRouteService(private val apiKey: String) : RouteService {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.openrouteservice.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OrsApi::class.java)

    override suspend fun calculate(origin: String, destination: String): RouteResult = withContext(Dispatchers.IO) {
        check(apiKey.isNotBlank()) {
            "Falta la clave de OpenRouteService. Añade ORS_API_KEY a local.properties y sincroniza Gradle."
        }

        val start = api.geocode(origin, apiKey = apiKey).features.firstOrNull()?.geometry?.coordinates
            ?: error("No se encontró el origen: $origin")
        val end = api.geocode(destination, apiKey = apiKey).features.firstOrNull()?.geometry?.coordinates
            ?: error("No se encontró el destino: $destination")

        require(start.size >= 2 && end.size >= 2) { "La API devolvió coordenadas no válidas" }
        // Geocoding devuelve [longitud, latitud]; Directions espera [[longitud, latitud], ...].
        val route = api.directions(
            profile = "driving-car",
            apiKey = apiKey,
            request = DirectionsRequest(listOf(start.take(2), end.take(2)))
        ).features.firstOrNull()?.properties?.summary ?: error("No se encontró una ruta entre esos lugares")

        val distanceKm = route.distance / 1000.0
        val durationMinutes = (route.duration / 60.0).toInt()
        // Estimación demostrativa: 7 l/100 km y 1,70 €/l; sustituir por precios reales más adelante.
        val estimatedCostEuro = distanceKm * 7.0 / 100.0 * 1.70
        RouteResult(distanceKm, durationMinutes, estimatedCostEuro)
    }
}
