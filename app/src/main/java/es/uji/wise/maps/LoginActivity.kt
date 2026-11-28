package es.uji.wise.maps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uji.wise.maps.model.User
import es.uji.wise.maps.repository.InMemoryUserRepository
import es.uji.wise.maps.service.AuthService
import es.uji.wise.maps.service.SessionManager
import es.uji.wise.maps.viewmodel.LoginFeedback
import es.uji.wise.maps.viewmodel.LoginViewModel

private val WisePrimary = Color(0xFF00685F)
private val WiseCanvas = Color(0xFFF8F9FF)
private val WiseInk = Color(0xFF0B1C30)
private val WiseMuted = Color(0xFF3D4947)

class LoginActivity : ComponentActivity() {
    private val authService by lazy {
        val demoUser = User("Carlos Mendoza Ríos", "carlos.rutas@poi.es", "RutaSegura2025!")
        AuthService(InMemoryUserRepository(listOf(demoUser)), SessionManager())
    }

    private val loginViewModelFactory by lazy {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(LoginViewModel::class.java))
                return LoginViewModel(authService) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = WisePrimary, background = WiseCanvas)) {
                val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)
                WiseMapsLoginScreen(loginViewModel)
            }
        }
    }
}

@Composable
private fun WiseMapsLoginScreen(viewModel: LoginViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize(), color = WiseCanvas) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier.size(64.dp).background(WisePrimary, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("W", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(20.dp))
            Text("Wise Maps", style = MaterialTheme.typography.headlineMedium, color = WiseInk, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Tus rutas y lugares, siempre a mano", color = WiseMuted, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(28.dp))

            state.feedback?.let { feedback ->
                FeedbackCard(feedback, onDismiss = viewModel::dismissFeedback)
                Spacer(Modifier.height(16.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Iniciar sesión", style = MaterialTheme.typography.titleLarge, color = WiseInk, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Correo electrónico", style = MaterialTheme.typography.labelLarge, color = WiseMuted)
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = viewModel::onEmailChanged,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("ejemplo@wise.com") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Contraseña", style = MaterialTheme.typography.labelLarge, color = WiseMuted)
                            TextButton(onClick = viewModel::requestPasswordRecovery) {
                                Text("¿Olvidaste tu contraseña?", fontSize = 12.sp)
                            }
                        }
                        OutlinedTextField(
                            value = state.password,
                            onValueChange = viewModel::onPasswordChanged,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("Introduce tu contraseña") },
                            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                TextButton(onClick = viewModel::togglePasswordVisibility) {
                                    Text(if (state.passwordVisible) "Ocultar" else "Mostrar", fontSize = 12.sp)
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = state.rememberSession, onCheckedChange = viewModel::onRememberSessionChanged)
                            Spacer(Modifier.width(4.dp))
                            Text("Recordar sesión", style = MaterialTheme.typography.bodySmall, color = WiseMuted)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).background(WisePrimary, CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text("GPS activo", style = MaterialTheme.typography.labelSmall, color = WisePrimary)
                        }
                    }
                    Button(
                        onClick = viewModel::login,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WisePrimary)
                    ) {
                        Text(if (state.isAuthenticated) "Sesión iniciada" else "Iniciar sesión", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(8.dp))
                        Text("→", fontSize = 20.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Acceso seguro a Wise Maps", style = MaterialTheme.typography.bodySmall, color = WiseMuted)
        }
    }
}

@Composable
private fun FeedbackCard(feedback: LoginFeedback, onDismiss: () -> Unit) {
    val container = if (feedback.isError) Color(0xFFFFDAD6) else Color(0xFFDAE2FD)
    val content = if (feedback.isError) Color(0xFF93000A) else Color(0xFF3F465C)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(feedback.title, color = content, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(3.dp))
                Text(feedback.message, color = content, style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onDismiss) { Text("×", color = content, fontSize = 20.sp) }
        }
    }
}
