package es.uji.wise.maps.service

import es.uji.wise.maps.model.DuplicatePlaceException
import es.uji.wise.maps.model.InvalidCoordinatesException
import es.uji.wise.maps.model.PlaceOfInterest
import es.uji.wise.maps.model.UnauthorizedActionException
import es.uji.wise.maps.repository.PlaceRepository
import java.util.Locale

class PlaceService(
    private val placeRepository: PlaceRepository,
    private val sessionManager: SessionManager
) {
    /**
     * HU-05: Alta de lugar de intes por coordenadas
     */
    fun addPlaceByCoordinates(nombre: String, latitud: Double, longitud: Double): PlaceOfInterest {
        val session = sessionManager.sesionActiva
        if (session == null || !session.autenticado) {
            throw UnauthorizedActionException()
        }
        if (latitud !in -90.0..90.0 || longitud !in -180.0..180.0) {
            throw InvalidCoordinatesException()
        }

        val places = placeRepository.findAllByUser(session.email)
        val normalizedName = nombre.trim().lowercase(Locale.ROOT)
        if (places.any { it.nombre.trim().lowercase(Locale.ROOT) == normalizedName }) {
            throw DuplicatePlaceException()
        }

        val place = PlaceOfInterest(
            nombre = nombre,
            latitud = latitud,
            longitud = longitud,
            favorito = false,
            ordenAlta = (places.maxOfOrNull { it.ordenAlta } ?: 0) + 1
        )
        placeRepository.save(session.email, place)
        return place
    }

    /**
     * HU-07: Consulta de la lista de lugares de inter├®s
     */
    fun getPlaces(): List<PlaceOfInterest> {
        val session = sessionManager.sesionActiva
        if (session == null || !session.autenticado) {
            throw UnauthorizedActionException("Acción no autorizada")
        }

        return placeRepository.findAllByUser(session.email)
            .sortedWith(
                compareByDescending<PlaceOfInterest> { it.favorito }
                    .thenBy { it.ordenAlta }
            )
    }
}
