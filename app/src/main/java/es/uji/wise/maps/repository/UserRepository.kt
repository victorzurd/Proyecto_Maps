package es.uji.wise.maps.repository

import es.uji.wise.maps.model.User

interface UserRepository {
    fun findAll(): List<User>
    fun findByEmail(email: String): User?
    fun save(user: User)
}

