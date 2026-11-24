package es.uji.wise.maps.service

import es.uji.wise.maps.model.Session
import es.uji.wise.maps.model.User
import es.uji.wise.maps.model.InvalidCredentialsException
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
        val user = userRepository.findByEmail(email.trim())
        if (user == null || user.password == null || user.password != password) {
            throw InvalidCredentialsException()
        }

        return Session(email = user.email, autenticado = true).also {
            sessionManager.sesionActiva = it
        }
    }

    /**
     * HU-03: Cierre de sesión
     */
    fun logout() {
        throw NotImplementedError("Not implemented")
    }
}

