package es.uji.wise.maps.acceptance

import es.uji.wise.maps.model.DuplicateUserException
import es.uji.wise.maps.model.User
import es.uji.wise.maps.repository.InMemoryUserRepository
import es.uji.wise.maps.service.AuthService
import es.uji.wise.maps.service.SessionManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class HU01RegisterUserAcceptanceTest {

    @Test
    fun `E1 Valido - Registro exitoso de usuario`() {
        // GIVEN la lista de usuarios registrados:
        val userRepository = InMemoryUserRepository(
            listOf(User(nombre = "Juan Pérez", email = "juan@example.com", password = "Pass1234"))
        )
        val authService = AuthService(userRepository, SessionManager(null))

        // WHEN un usuario no registrado solicita el registro con nombre "María Gómez", email "maria@example.com" y contraseña "Pass1234"
        authService.register("María Gómez", "maria@example.com", "Pass1234")

        // THEN la lista de usuarios registrados se actualiza incorporando el nuevo usuario
        val usuarios = userRepository.findAll().map { it.copy(password = null) }
        val esperado = listOf(
            User(nombre = "Juan Pérez", email = "juan@example.com"),
            User(nombre = "María Gómez", email = "maria@example.com")
        )
        assertEquals(esperado, usuarios)
    }

    @Test
    fun `E2 Invalido - Registro con correo ya existente`() {
        // GIVEN la lista de usuarios registrados:
        val inicial = listOf(User(nombre = "Juan Pérez", email = "juan@example.com", password = "Pass1234"))
        val userRepository = InMemoryUserRepository(inicial)
        val authService = AuthService(userRepository, SessionManager(null))

        // WHEN un usuario intenta registrarse con nombre "Juan Segundo", email "juan@example.com" y contraseña "Pass1234"
        // THEN el sistema rechaza el registro e informa "El usuario con este correo ya existe"
        val ex = assertThrows(DuplicateUserException::class.java) {
            authService.register("Juan Segundo", "juan@example.com", "Pass1234")
        }
        assertEquals("El usuario con este correo ya existe", ex.message)

        // AND la lista de usuarios permanece intacta
        assertEquals(1, userRepository.findAll().size)
        assertEquals("Juan Pérez", userRepository.findAll().first().nombre)
    }
}
