package es.uji.wise.maps.model

data class User(
    val nombre: String,
    val email: String,
    val password: String? = null
)

