package es.uji.rutaspike

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class RouteScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun `buscar está deshabilitado hasta completar origen y destino`() {
        val fakeRouteService = object : RouteService {
            override suspend fun calculate(origin: String, destination: String) =
                RouteResult(12.5, 24, 1.50)
        }
        compose.setContent { RouteScreen(fakeRouteService) }
        compose.onNodeWithText("Buscar ruta").assertIsNotEnabled()
        compose.onNodeWithText("Origen").performTextInput("Castellón")
        compose.onNodeWithText("Destino").performTextInput("Valencia")
        compose.onNodeWithText("Buscar ruta").assertIsEnabled().performClick()
        compose.onNodeWithText("Distancia: 12.5 km").assertExists()
    }
}
