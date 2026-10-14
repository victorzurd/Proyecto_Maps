package es.uji.rutaspike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { RouteScreen(OpenRouteService(BuildConfig.ORS_API_KEY)) } }
    }
}

@Composable
fun RouteScreen(routeService: RouteService) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<RouteResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Calcula una ruta", style = MaterialTheme.typography.headlineMedium)
            Text("Spike técnico · respuesta simulada")
            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text("Origen") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destino") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        error = null
                        result = null
                        try {
                            result = RouteUseCase(routeService).calculate(origin, destination)
                        } catch (exception: Exception) {
                            error = exception.message ?: "No se pudo calcular la ruta"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = origin.isNotBlank() && destination.isNotBlank() && !loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (loading) "Consultando OpenRouteService…" else "Buscar ruta") }
            result?.let {
                Text("Distancia: ${it.distanceKm} km")
                Text("Duración: ${it.durationMinutes} min")
                Text("Coste estimado: ${"%.2f".format(it.estimatedCostEuro)} €")
            }
            error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }
            result?.let { RouteMap(it) }
        }
    }
}

