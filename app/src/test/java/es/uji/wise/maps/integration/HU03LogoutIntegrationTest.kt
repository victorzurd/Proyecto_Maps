package es.uji.wise.maps.integration

import es.uji.wise.maps.model.NoActiveSessionException
import es.uji.wise.maps.model.Session
import es.uji.wise.maps.repository.InMemoryUserRepository
import es.uji.wise.maps.service.AuthService
import es.uji.wise.maps.service.SessionManager
import es.uji.wise.maps.service.SessionStore
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

/** HU-03 integration tests: service, session manager and mocked session persistence. */
class HU03LogoutIntegrationTest {

    @Test
    fun `E1 Valido - cierra sesion y elimina la sesion persistida`() {
        // GIVEN una sesión activa y un almacén de sesión inyectado como mock
        val activeSession = Session(email = "juan@example.com", autenticado = true)
        val sessionStore = mock(SessionStore::class.java)
        val sessionManager = SessionManager(activeSession, sessionStore)
        val authService = AuthService(InMemoryUserRepository(), sessionManager)

        // WHEN se solicita cerrar sesión
        authService.logout()

        // THEN se elimina la sesión en memoria y también en el almacenamiento
        assertNull(sessionManager.sesionActiva)
        verify(sessionStore).save(null)
    }

    @Test
    fun `E2 Invalido - sin sesion activa rechaza cierre y no modifica persistencia`() {
        // GIVEN que no existe una sesión activa
        val sessionStore = mock(SessionStore::class.java)
        val sessionManager = SessionManager(null, sessionStore)
        val authService = AuthService(InMemoryUserRepository(), sessionManager)

        // WHEN se solicita cerrar sesión
        assertThrows(NoActiveSessionException::class.java) { authService.logout() }

        // THEN solo se lee la sesión al inicializar; el intento no escribe cambios
        assertNull(sessionManager.sesionActiva)
        verify(sessionStore).load()
        verifyNoMoreInteractions(sessionStore)
    }
}
