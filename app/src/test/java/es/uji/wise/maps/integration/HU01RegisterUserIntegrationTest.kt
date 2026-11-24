package es.uji.wise.maps.integration

import es.uji.wise.maps.model.DuplicateUserException
import es.uji.wise.maps.model.InvalidUserDataException
import es.uji.wise.maps.model.User
import es.uji.wise.maps.repository.UserRepository
import es.uji.wise.maps.service.AuthService
import es.uji.wise.maps.service.SessionManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`

/**
 * Pruebas de integración para HU-01 (Registro de usuario) sustituyendo
 * la dependencia de persistencia (UserRepository) por un mock de Mockito
 * inyectado por constructor en AuthService.
 */
class HU01RegisterUserIntegrationTest {

    @Test
    fun `E1 Valido - Registro exitoso delega consulta y guardado en UserRepository mockeado`() {
        // GIVEN un repositorio mockeado donde el correo aún no existe
        val userRepository = mock(UserRepository::class.java)
        `when`(userRepository.findByEmail("maria@example.com")).thenReturn(null)
        val authService = AuthService(userRepository, SessionManager(null))

        // WHEN se solicita el registro con datos válidos
        val creado = authService.register("María Gómez", "maria@example.com", "Pass1234")

        // THEN se devuelve el usuario creado y se invoca la persistencia en el mock
        val esperado = User(nombre = "María Gómez", email = "maria@example.com", password = "Pass1234")
        assertEquals(esperado, creado)
        verify(userRepository).findByEmail("maria@example.com")
        verify(userRepository).save(esperado)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `E2 Invalido - Registro con correo existente rechaza sin invocar save en el mock`() {
        // GIVEN un repositorio mockeado que devuelve un usuario ya registrado con ese correo
        val userRepository = mock(UserRepository::class.java)
        val existente = User(nombre = "Juan Pérez", email = "juan@example.com", password = "Pass1234")
        `when`(userRepository.findByEmail("juan@example.com")).thenReturn(existente)
        val authService = AuthService(userRepository, SessionManager(null))

        // WHEN se intenta registrar otro usuario con el mismo correo
        // THEN se lanza DuplicateUserException y nunca se llama a save()
        val ex = assertThrows(DuplicateUserException::class.java) {
            authService.register("Juan Segundo", "juan@example.com", "Pass1234")
        }
        assertEquals("El usuario con este correo ya existe", ex.message)
        verify(userRepository).findByEmail("juan@example.com")
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `E3 Invalido - Registro con formato de correo invalido rechaza antes de consultar repositorio`() {
        // GIVEN un repositorio mockeado
        val userRepository = mock(UserRepository::class.java)
        val authService = AuthService(userRepository, SessionManager(null))

        // WHEN se intenta registrar con un correo sin formato válido
        // THEN se lanza InvalidUserDataException sin tocar el repositorio
        val ex = assertThrows(InvalidUserDataException::class.java) {
            authService.register("María Gómez", "correo-sin-arroba", "Pass1234")
        }
        assertEquals("Formato de correo inválido", ex.message)
        verifyNoInteractions(userRepository)
    }

    @Test
    fun `E4 Invalido - Registro con contrasena menor de 8 caracteres rechaza antes de consultar repositorio`() {
        // GIVEN un repositorio mockeado
        val userRepository = mock(UserRepository::class.java)
        val authService = AuthService(userRepository, SessionManager(null))

        // WHEN se intenta registrar con contraseña de 7 caracteres (límite inferior inválido)
        // THEN se lanza InvalidUserDataException sin tocar el repositorio
        val ex = assertThrows(InvalidUserDataException::class.java) {
            authService.register("María Gómez", "maria@example.com", "Pass123")
        }
        assertEquals("La contraseña debe tener al menos 8 caracteres", ex.message)
        verifyNoInteractions(userRepository)
    }
}
