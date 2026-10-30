package es.uji.wise.maps.service

import es.uji.wise.maps.model.PlaceOfInterest
import es.uji.wise.maps.repository.PlaceRepository

class PlaceService(
    private val placeRepository: PlaceRepository,
    private val sessionManager: SessionManager
) {
    /**
     * HU-05: Alta de lugar de interés por coordenadas
     */
    fun addPlaceByCoordinates(nombre: String, latitud: Double, longitud: Double): PlaceOfInterest {
        throw NotImplementedError("Not implemented")
    }

    /**
     * HU-07: Consulta de la lista de lugares de interés
     */
    fun getPlaces(): List<PlaceOfInterest> {
        throw NotImplementedError("Not implemented")
    }
}

