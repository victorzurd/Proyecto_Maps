package es.uji.wise.maps.service

import es.uji.wise.maps.model.DuplicatePlaceException
import es.uji.wise.maps.model.InvalidCoordinatesException
import es.uji.wise.maps.model.PlaceOfInterest
import es.uji.wise.maps.model.Session
import es.uji.wise.maps.model.UnauthorizedActionException
import es.uji.wise.maps.repository.InMemoryPlaceRepository
import es.uji.wise.maps.repository.PlaceRepository
import java.io.IOException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class HU05PlaceServiceTest {
    private val email = "juan@example.com"
    private val existing = PlaceOfInterest("  Ágora ", 0.0, 0.0, false, 1)
    private val repository = InMemoryPlaceRepository(mapOf(email to listOf(existing)))
    private val sessions = SessionManager(Session(email, true))
    private val service = PlaceService(repository, sessions)

    @Test
    fun `E3 rechaza longitudes fuera de rango sin alterar el catálogo`() {
        for (longitude in listOf(-180.0001, 180.0001, Double.NaN)) {
            assertThrows(InvalidCoordinatesException::class.java) {
                service.addPlaceByCoordinates("Biblioteca", 0.0, longitude)
            }
        }
        assertEquals(listOf(existing), repository.findAllByUser(email))
    }

    @Test
    fun `E4 rechaza nombres equivalentes para el mismo usuario`() {
        assertThrows(DuplicatePlaceException::class.java) {
            service.addPlaceByCoordinates("áGORA", 1.0, 1.0)
        }
        assertEquals(listOf(existing), repository.findAllByUser(email))
    }

    @Test
    fun `E5 rechaza tanto sesión ausente como no autenticada`() {
        for (session in listOf(null, Session(email, false))) {
            sessions.sesionActiva = session
            assertThrows(UnauthorizedActionException::class.java) {
                service.addPlaceByCoordinates("Biblioteca", 0.0, 0.0)
            }
        }
        assertEquals(listOf(existing), repository.findAllByUser(email))
    }

    @Test
    fun `acepta las fronteras y conserva la grafía y el orden por usuario`() {
        val other = "ana@example.com"
        sessions.sesionActiva = Session(other, true)
        service.addPlaceByCoordinates(" ÁGORA ", -90.0, -180.0)
        service.addPlaceByCoordinates("Biblioteca", 90.0, 180.0)
        assertEquals(listOf(
            PlaceOfInterest(" ÁGORA ", -90.0, -180.0, false, 1),
            PlaceOfInterest("Biblioteca", 90.0, 180.0, false, 2)
        ), repository.findAllByUser(other))
        assertEquals(listOf(existing), repository.findAllByUser(email))
    }

    @Test
    fun `un fallo de conexión al guardar impide confirmar el alta`() {
        val disconnected = object : PlaceRepository by repository {
            override fun save(userEmail: String, place: PlaceOfInterest) {
                throw IOException("Sin conexión al servidor")
            }
        }
        assertThrows(IOException::class.java) {
            PlaceService(disconnected, sessions).addPlaceByCoordinates("Biblioteca", 0.0, 0.0)
        }
        assertEquals(listOf(existing), repository.findAllByUser(email))
    }
}
