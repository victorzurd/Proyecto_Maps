package es.uji.wise.maps.model

data class PlaceOfInterest(
    val nombre: String,
    val latitud: Double,
    val longitud: Double,
    val favorito: Boolean = false,
    val ordenAlta: Int = 1
)

