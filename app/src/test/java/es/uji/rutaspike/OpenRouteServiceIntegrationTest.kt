package es.uji.rutaspike

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

/** Prueba opcional que consulta la API real; no reemplaza los unit tests con Mockito. */
class OpenRouteServiceIntegrationTest {
    @Test
    fun `API real devuelve una ruta razonable entre Castellon de la Plana y Valencia`() {
        assumeTrue(
            "Activa la prueba con -PrunOrsIntegrationTests=true",
            System.getProperty("runOrsIntegrationTests") == "true"
        )
        assumeTrue(
            "Configura ORS_API_KEY en local.properties y sincroniza Gradle",
            BuildConfig.ORS_API_KEY.isNotBlank()
        )

        val result = runBlocking {
            OpenRouteService(BuildConfig.ORS_API_KEY)
                .calculate("Castelló de la Plana, Castellón, España", "València, España")
        }

        assertTrue(
            "Distancia inesperada: ${result.distanceKm} km",
            result.distanceKm in 55.0..110.0
        )
        assertTrue(
            "Duración inesperada: ${result.durationMinutes} min",
            result.durationMinutes in 20..150
        )
        assertTrue("El coste estimado debe ser positivo", result.estimatedCostEuro > 0.0)
        assertTrue("La geometría de la ruta debe tener varios puntos", result.geometry.size > 1)
        assertTrue(
            "Las coordenadas deben ser pares [longitud, latitud] válidos",
            result.geometry.all { point ->
                point.size >= 2 && point[0] in -180.0..180.0 && point[1] in -90.0..90.0
            }
        )
    }
}
