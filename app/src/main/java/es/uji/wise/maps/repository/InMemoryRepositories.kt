package es.uji.wise.maps.repository

import es.uji.wise.maps.model.PlaceOfInterest
import es.uji.wise.maps.model.User

class InMemoryUserRepository(initialUsers: List<User> = emptyList()) : UserRepository {
    private val users = initialUsers.toMutableList()
    override fun findAll(): List<User> = users.toList()
    override fun findByEmail(email: String): User? =
        users.find { it.email.equals(email, ignoreCase = true) }
    override fun save(user: User) {
        users.add(user)
    }
}

class InMemoryPlaceRepository(
    initialPlaces: Map<String, List<PlaceOfInterest>> = emptyMap()
) : PlaceRepository {
    private val placesByUser = initialPlaces.mapValues { it.value.toMutableList() }.toMutableMap()
    override fun findAllByUser(userEmail: String): List<PlaceOfInterest> =
        placesByUser[userEmail]?.toList() ?: emptyList()
    override fun save(userEmail: String, place: PlaceOfInterest) {
        val list = placesByUser.getOrPut(userEmail) { mutableListOf() }
        list.add(place)
    }
}

