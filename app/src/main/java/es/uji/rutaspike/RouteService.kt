package es.uji.rutaspike

data class RouteResult(
    val distanceKm: Double,
    val durationMinutes: Int,
    val estimatedCostEuro: Double
)

/** Abstracción que permitirá sustituir la API real por un mock en pruebas. */
interface RouteService {
    fun calculate(origin: String, destination: String): RouteResult
}

/** Ejemplo deliberadamente simple para demostrar JUnit + Mockito sin red ni API key. */
class RouteUseCase(private val routeService: RouteService) {
    fun calculate(origin: String, destination: String): RouteResult {
        require(origin.isNotBlank()) { "El origen es obligatorio" }
        require(destination.isNotBlank()) { "El destino es obligatorio" }
        return routeService.calculate(origin.trim(), destination.trim())
    }
}
