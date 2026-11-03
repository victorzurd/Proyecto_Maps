package es.uji.wise.maps.acceptance

import es.uji.wise.maps.model.NoActiveSessionException
import es.uji.wise.maps.model.Session
import es.uji.wise.maps.repository.InMemoryUserRepository
import es.uji.wise.maps.service.AuthService
import es.uji.wise.maps.service.SessionManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class HU03LogoutAcceptanceTest {

    @Test
    fun `E1 Valido - Cierre de sesion exitoso`() {
        // GIVEN un usuario con sesión activa
        val activeSession = Session(email = "juan@example.com", autenticado = true)
        val sessionManager = SessionManager(activeSession)
        val authService = AuthService(InMemoryUserRepository(), sessionManager)

        // WHEN el usuario solicita cerrar la sesión
        authService.logout()

        // THEN la sesión queda inactiva
        assertNull(sessionManager.sesionActiva)
    }

    @Test
    fun `E2 Invalido - Cierre de sesion sin sesion activa`() {
        // GIVEN que no hay ninguna sesión activa
        val sessionManager = SessionManager(null)
        val authService = AuthService(InMemoryUserRepository(), sessionManager)

        // WHEN se solicita cerrar sesión
        val exception = assertThrows(NoActiveSessionException::class.java) {
            authService.logout()
        }

        // THEN se rechaza la operación y el estado sigue inactivo
        assertEquals("No hay sesión activa para cerrar", exception.message)
        assertNull(sessionManager.sesionActiva)
    }
}
