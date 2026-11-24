package es.uji.wise.maps.service

import es.uji.wise.maps.model.DuplicateUserException
import es.uji.wise.maps.model.InvalidUserDataException
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
        val cleanName = nombre.trim()
        val normalizedEmail = email.trim().lowercase()

        if (cleanName.isBlank()) {
            throw InvalidUserDataException("El nombre no puede estar vacío")
        }
        if (!EMAIL_REGEX.matches(normalizedEmail)) {
            throw InvalidUserDataException("Formato de correo inválido")
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            throw InvalidUserDataException("La contraseña debe tener al menos 8 caracteres")
        }
        if (userRepository.findByEmail(normalizedEmail) != null) {
            throw DuplicateUserException("El usuario con este correo ya existe")
        }

        val newUser = User(nombre = cleanName, email = normalizedEmail, password = password)
        userRepository.save(newUser)
        return newUser
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

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}

