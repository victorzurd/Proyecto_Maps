package es.uji.wise.maps.repository

import es.uji.wise.maps.model.PlaceOfInterest

interface PlaceRepository {
    fun findAllByUser(userEmail: String): List<PlaceOfInterest>
    fun save(userEmail: String, place: PlaceOfInterest)
}

