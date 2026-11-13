package es.uji.wise.maps.acceptance

import es.uji.wise.maps.model.InvalidCoordinatesException
import es.uji.wise.maps.model.PlaceOfInterest
import es.uji.wise.maps.model.Session
import es.uji.wise.maps.repository.InMemoryPlaceRepository
import es.uji.wise.maps.service.PlaceService
import es.uji.wise.maps.service.SessionManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class HU05AddPlaceByCoordinatesAcceptanceTest {

    @Test
    fun `E1 Valido - Alta exitosa de lugar por coordenadas`() {
        // GIVEN un usuario autenticado con un lugar de interés guardado
        val email = "juan@example.com"
        val initialPlaces = listOf(
            PlaceOfInterest("Lugar 1", 39.986, -0.051, favorito = false, ordenAlta = 1)
        )
        val placeRepository = InMemoryPlaceRepository(mapOf(email to initialPlaces))
        val sessionManager = SessionManager(Session(email = email, autenticado = true))
        val placeService = PlaceService(placeRepository, sessionManager)

        // WHEN añade un lugar con coordenadas válidas
        placeService.addPlaceByCoordinates("Punto de Inicio", 39.981, -0.038)

        // THEN se conserva el lugar anterior y se añade el nuevo con los valores por defecto
        val expectedPlaces = listOf(
            PlaceOfInterest("Lugar 1", 39.986, -0.051, favorito = false, ordenAlta = 1),
            PlaceOfInterest("Punto de Inicio", 39.981, -0.038, favorito = false, ordenAlta = 2)
        )
        assertEquals(expectedPlaces, placeRepository.findAllByUser(email))
    }

    @Test
    fun `E2 Invalido - Alta con latitud fuera de rango`() {
        // GIVEN un usuario autenticado con un lugar de interés guardado
        val email = "juan@example.com"
        val initialPlaces = listOf(
            PlaceOfInterest("Lugar 1", 39.986, -0.051, favorito = false, ordenAlta = 1)
        )
        val placeRepository = InMemoryPlaceRepository(mapOf(email to initialPlaces))
        val sessionManager = SessionManager(Session(email = email, autenticado = true))
        val placeService = PlaceService(placeRepository, sessionManager)

        // WHEN intenta añadir un lugar con latitud superior a 90
        val exception = assertThrows(InvalidCoordinatesException::class.java) {
            placeService.addPlaceByCoordinates("Punto de Inicio", 90.0001, -0.038)
        }

        // THEN se rechaza la operación con el mensaje previsto y la lista permanece intacta
        assertEquals("Coordenadas fuera de rango válido", exception.message)
        assertEquals(initialPlaces, placeRepository.findAllByUser(email))
    }
}
