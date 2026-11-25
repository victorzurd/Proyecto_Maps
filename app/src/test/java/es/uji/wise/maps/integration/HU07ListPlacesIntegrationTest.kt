package es.uji.wise.maps.integration

import es.uji.wise.maps.model.PlaceOfInterest
import es.uji.wise.maps.model.Session
import es.uji.wise.maps.model.UnauthorizedActionException
import es.uji.wise.maps.repository.PlaceRepository
import es.uji.wise.maps.service.PlaceService
import es.uji.wise.maps.service.SessionManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions

/** HU-07 integration tests: PlaceService with SessionManager and mocked PlaceRepository. */
class HU07ListPlacesIntegrationTest {

    @Test
    fun `E1 Valido - usuario autenticado consulta lugares y ordena favoritos primero`() {
        // GIVEN un usuario autenticado y un repositorio mockeado con lugares
        val userEmail = "juan@example.com"
        val session = Session(email = userEmail, autenticado = true)
        val sessionManager = SessionManager(initialSession = session)
        val placeRepository = mock(PlaceRepository::class.java)

        val place1 = PlaceOfInterest("Castellón", 39.986, -0.051, favorito = false, ordenAlta = 1)
        val place2 = PlaceOfInterest("Valencia", 39.469, -0.376, favorito = true, ordenAlta = 2)
        val place3 = PlaceOfInterest("Madrid", 40.416, -3.703, favorito = false, ordenAlta = 3)

        `when`(placeRepository.findAllByUser(userEmail)).thenReturn(listOf(place1, place2, place3))

        val placeService = PlaceService(placeRepository, sessionManager)

        // WHEN consulta los lugares
        val result = placeService.getPlaces()

        // THEN interactúa con el repositorio y devuelve los lugares ordenados
        verify(placeRepository).findAllByUser(userEmail)
        val esperado = listOf(place2, place1, place3)
        assertEquals(esperado, result)
    }

    @Test
    fun `E2 Invalido - sin sesion activa lanza excepcion y no consulta el repositorio`() {
        // GIVEN que no hay sesión activa
        val sessionManager = SessionManager(initialSession = null)
        val placeRepository = mock(PlaceRepository::class.java)
        val placeService = PlaceService(placeRepository, sessionManager)

        // WHEN intenta consultar los lugares
        val ex = assertThrows(UnauthorizedActionException::class.java) {
            placeService.getPlaces()
        }

        // THEN se rechaza con el mensaje esperado y no se consulta el repositorio
        assertEquals("Acción no autorizada", ex.message)
        verifyNoInteractions(placeRepository)
    }
}
