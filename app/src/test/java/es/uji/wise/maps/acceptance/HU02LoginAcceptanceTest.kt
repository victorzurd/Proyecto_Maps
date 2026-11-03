package es.uji.wise.maps.acceptance

import es.uji.wise.maps.model.InvalidCredentialsException
import es.uji.wise.maps.model.Session
import es.uji.wise.maps.model.User
import es.uji.wise.maps.repository.InMemoryUserRepository
import es.uji.wise.maps.service.AuthService
import es.uji.wise.maps.service.SessionManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class HU02LoginAcceptanceTest {

    @Test
    fun `E1 Valido - Inicio de sesion exitoso`() {
        // GIVEN un usuario registrado y ninguna sesión activa
        val sessionManager = SessionManager(null)
        val authService = AuthService(
            InMemoryUserRepository(
                listOf(User("Juan Pérez", "juan@example.com", "Pass1234"))
            ),
            sessionManager
        )

        // WHEN el usuario proporciona sus credenciales correctas
        val session = authService.login("juan@example.com", "Pass1234")

        // THEN se devuelve la sesión autenticada y queda activa
        val expectedSession = Session(email = "juan@example.com", autenticado = true)
        assertEquals(expectedSession, session)
        assertEquals(expectedSession, sessionManager.sesionActiva)
    }

    @Test
    fun `E2 Invalido - Contrasena incorrecta`() {
        // GIVEN un usuario registrado y ninguna sesión activa
        val sessionManager = SessionManager(null)
        val authService = AuthService(
            InMemoryUserRepository(
                listOf(User("Juan Pérez", "juan@example.com", "Pass1234"))
            ),
            sessionManager
        )

        // WHEN se intenta iniciar sesión con una contraseña incorrecta
        val exception = assertThrows(InvalidCredentialsException::class.java) {
            authService.login("juan@example.com", "WrongPass")
        }

        // THEN se rechaza el acceso con el mensaje acordado y no se activa sesión
        assertEquals("Credenciales inválidas", exception.message)
        assertNull(sessionManager.sesionActiva)
    }

    @Test
    fun `E3 Invalido - Usuario inexistente sin revelar su existencia`() {
        // GIVEN una lista de usuarios registrados y ninguna sesión activa
        val sessionManager = SessionManager(null)
        val authService = AuthService(
            InMemoryUserRepository(
                listOf(User("Juan Pérez", "juan@example.com", "Pass1234"))
            ),
            sessionManager
        )

        // WHEN alguien intenta iniciar sesión con un correo no registrado
        val exception = assertThrows(InvalidCredentialsException::class.java) {
            authService.login("desconocido@example.com", "Pass1234")
        }

        // THEN se informa el mismo error que para una contraseña incorrecta
        assertEquals("Credenciales inválidas", exception.message)
        assertNull(sessionManager.sesionActiva)
    }
}
