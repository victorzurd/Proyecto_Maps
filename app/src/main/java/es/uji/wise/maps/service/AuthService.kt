package es.uji.wise.maps.service

import es.uji.wise.maps.model.Session
import es.uji.wise.maps.model.User
import es.uji.wise.maps.repository.UserRepository

class AuthService(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) {
    /**
     * HU-01: Registro de usuario
     */
    fun register(nombre: String, email: String, password: String): User {
        throw NotImplementedError("Not implemented")
    }

    /**
     * HU-02: Inicio de sesión
     */
    fun login(email: String, password: String): Session {
        throw NotImplementedError("Not implemented")
    }

    /**
     * HU-03: Cierre de sesión
     */
    fun logout() {
        throw NotImplementedError("Not implemented")
    }
}

