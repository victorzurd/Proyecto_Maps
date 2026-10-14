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
import retrofit2.http.Url

private interface OrsApi {
    @GET
    suspend fun geocode(
        @Url url: String,
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
private data class RouteFeature(val properties: RouteProperties, val geometry: RouteGeometry)
private data class RouteGeometry(val coordinates: List<List<Double>> = emptyList())
private data class RouteProperties(val summary: RouteSummary)
private data class RouteSummary(val distance: Double, val duration: Double)

/** Adapta las respuestas GeoJSON de OpenRouteService al modelo sencillo de la app. */
class OpenRouteService(private val apiKey: String) : RouteService {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.heigit.org/openrouteservice/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OrsApi::class.java)

    override suspend fun calculate(origin: String, destination: String): RouteResult = withContext(Dispatchers.IO) {
        check(apiKey.isNotBlank()) {
            "Falta la clave de OpenRouteService. Añade ORS_API_KEY a local.properties y sincroniza Gradle."
        }

        val start = api.geocode(
            url = "https://api.heigit.org/pelias/v1/search",
            place = origin,
            apiKey = apiKey
        ).features.firstOrNull()?.geometry?.coordinates
            ?: error("No se encontró el origen: $origin")
        val end = api.geocode(
            url = "https://api.heigit.org/pelias/v1/search",
            place = destination,
            apiKey = apiKey
        ).features.firstOrNull()?.geometry?.coordinates
            ?: error("No se encontró el destino: $destination")

        require(start.size >= 2 && end.size >= 2) { "La API devolvió coordenadas no válidas" }
        // Geocoding devuelve [longitud, latitud]; Directions espera [[longitud, latitud], ...].
        val routeFeature = api.directions(
            profile = "driving-car",
            apiKey = apiKey,
            request = DirectionsRequest(listOf(start.take(2), end.take(2)))
        ).features.firstOrNull() ?: error("No se encontró una ruta entre esos lugares")
        val route = routeFeature.properties.summary

        val distanceKm = route.distance / 1000.0
        val durationMinutes = (route.duration / 60.0).toInt()
        // Estimación demostrativa: 7 l/100 km y 1,70 €/l; sustituir por precios reales más adelante.
        val estimatedCostEuro = distanceKm * 7.0 / 100.0 * 1.70
        RouteResult(distanceKm, durationMinutes, estimatedCostEuro, routeFeature.geometry.coordinates)
    }
}
