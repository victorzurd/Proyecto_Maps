package es.uji.rutaspike

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlinx.coroutines.runBlocking

class RouteUseCaseTest {
    private val routeService = mock(RouteService::class.java)

    @Test
    fun `calcula ruta usando la dependencia y normaliza espacios`() = runBlocking {
        val expected = RouteResult(12.5, 24, 1.50)
        `when`(routeService.calculate("Castellón", "Valencia")).thenReturn(expected)

        val result = RouteUseCase(routeService).calculate(" Castellón ", "Valencia ")

        assertEquals(expected, result)
        verify(routeService).calculate("Castellón", "Valencia")
    }

    @Test
    fun `rechaza origen vacío sin llamar a la dependencia`() = runBlocking {
        assertThrows(IllegalArgumentException::class.java) {
            RouteUseCase(routeService).calculate(" ", "Valencia")
        }
    }
}
