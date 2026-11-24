package es.uji.wise.maps.integration

import es.uji.wise.maps.model.InvalidCredentialsException
import es.uji.wise.maps.model.Session
import es.uji.wise.maps.model.User
import es.uji.wise.maps.repository.UserRepository
import es.uji.wise.maps.service.AuthService
import es.uji.wise.maps.service.SessionManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`

/**
 * Pruebas de integración para HU-02 (Inicio de sesión), usando un mock de
 * UserRepository inyectado por constructor para aislar la persistencia.
 */
class HU02LoginIntegrationTest {

    @Test
    fun `E1 Valido - Inicio de sesion consulta repositorio y activa la sesion`() {
        // GIVEN un usuario registrado en el repositorio mockeado y ninguna sesión activa
        val userRepository = mock(UserRepository::class.java)
        val user = User(nombre = "Juan Pérez", email = "juan@example.com", password = "Pass1234")
        `when`(userRepository.findByEmail("juan@example.com")).thenReturn(user)
        val sessionManager = SessionManager(null)
        val authService = AuthService(userRepository, sessionManager)

        // WHEN el usuario inicia sesión con sus credenciales correctas
        val session = authService.login("juan@example.com", "Pass1234")

        // THEN se devuelve la sesión autenticada y queda activa
        val expectedSession = Session(email = "juan@example.com", autenticado = true)
        assertEquals(expectedSession, session)
        assertEquals(expectedSession, sessionManager.sesionActiva)
        verify(userRepository).findByEmail("juan@example.com")
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `E2 Invalido - Contrasena incorrecta no activa la sesion`() {
        // GIVEN un usuario registrado en el repositorio mockeado y ninguna sesión activa
        val userRepository = mock(UserRepository::class.java)
        val user = User(nombre = "Juan Pérez", email = "juan@example.com", password = "Pass1234")
        `when`(userRepository.findByEmail("juan@example.com")).thenReturn(user)
        val sessionManager = SessionManager(null)
        val authService = AuthService(userRepository, sessionManager)

        // WHEN se intenta acceder con una contraseña incorrecta
        val exception = assertThrows(InvalidCredentialsException::class.java) {
            authService.login("juan@example.com", "WrongPass")
        }

        // THEN se rechazan las credenciales y la sesión continúa inactiva
        assertEquals("Credenciales inválidas", exception.message)
        assertNull(sessionManager.sesionActiva)
        verify(userRepository).findByEmail("juan@example.com")
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `E3 Invalido - Usuario inexistente devuelve error generico y no activa sesion`() {
        // GIVEN un repositorio mockeado sin el usuario solicitado
        val userRepository = mock(UserRepository::class.java)
        `when`(userRepository.findByEmail("desconocido@example.com")).thenReturn(null)
        val sessionManager = SessionManager(null)
        val authService = AuthService(userRepository, sessionManager)

        // WHEN se intenta acceder con un correo no registrado
        val exception = assertThrows(InvalidCredentialsException::class.java) {
            authService.login("desconocido@example.com", "Pass1234")
        }

        // THEN se devuelve el mismo error genérico que ante una contraseña incorrecta
        assertEquals("Credenciales inválidas", exception.message)
        assertNull(sessionManager.sesionActiva)
        verify(userRepository).findByEmail("desconocido@example.com")
        verifyNoMoreInteractions(userRepository)
    }
}
