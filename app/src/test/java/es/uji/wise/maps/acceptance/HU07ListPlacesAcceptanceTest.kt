package es.uji.wise.maps.acceptance

import es.uji.wise.maps.model.PlaceOfInterest
import es.uji.wise.maps.model.Session
import es.uji.wise.maps.model.UnauthorizedActionException
import es.uji.wise.maps.repository.InMemoryPlaceRepository
import es.uji.wise.maps.service.PlaceService
import es.uji.wise.maps.service.SessionManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class HU07ListPlacesAcceptanceTest {

    @Test
    fun `E1 Valido - Consulta con lugares registrados y ordenacion`() {
        // GIVEN un usuario autenticado con los siguientes lugares guardados
        val email = "juan@example.com"
        val sessionManager = SessionManager(Session(email = email, autenticado = true))
        val placeRepository = InMemoryPlaceRepository(
            mapOf(
                email to listOf(
                    PlaceOfInterest(
                        "Castellón de la Plana",
                        39.986,
                        -0.051,
                        favorito = false,
                        ordenAlta = 1
                    ),
                    PlaceOfInterest("Valencia", 39.469, -0.376, favorito = true, ordenAlta = 2),
                    PlaceOfInterest("Madrid", 40.416, -3.703, favorito = false, ordenAlta = 3)
                )
            )
        )
        val placeService = PlaceService(placeRepository, sessionManager)

        // WHEN el usuario solicita consultar la lista de lugares de interés
        val resultado = placeService.getPlaces()

        // THEN el sistema devuelve la lista ordenada (favoritos primero, y el resto por orden de alta)
        val esperado = listOf(
            PlaceOfInterest("Valencia", 39.469, -0.376, favorito = true, ordenAlta = 2),
            PlaceOfInterest(
                "Castellón de la Plana",
                39.986,
                -0.051,
                favorito = false,
                ordenAlta = 1
            ),
            PlaceOfInterest("Madrid", 40.416, -3.703, favorito = false, ordenAlta = 3)
        )
        assertEquals(esperado, resultado)
    }

    @Test
    fun `E3 Invalido - Consulta sin autenticacion`() {
        // GIVEN un entorno sin sesión activa
        val sessionManager = SessionManager(initialSession = null)
        val placeRepository = InMemoryPlaceRepository()
        val placeService = PlaceService(placeRepository, sessionManager)

        // WHEN se solicita consultar la lista de lugares de interés
        // THEN el sistema rechaza la consulta e informa "Acción no autorizada"
        val ex = assertThrows(UnauthorizedActionException::class.java) {
            placeService.getPlaces()
        }
        assertEquals("Acción no autorizada", ex.message)
    }
}