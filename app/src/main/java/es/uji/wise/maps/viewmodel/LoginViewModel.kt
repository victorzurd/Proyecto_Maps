package es.uji.wise.maps.viewmodel

import androidx.lifecycle.ViewModel
import es.uji.wise.maps.model.InvalidCredentialsException
import es.uji.wise.maps.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberSession: Boolean = true,
    val passwordVisible: Boolean = false,
    val isAuthenticated: Boolean = false,
    val feedback: LoginFeedback? = null
)

data class LoginFeedback(val title: String, val message: String, val isError: Boolean)

class LoginViewModel(private val authService: AuthService) : ViewModel() {
    private val mutableUiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = mutableUiState.asStateFlow()

    fun onEmailChanged(email: String) = update { copy(email = email, feedback = null) }

    fun onPasswordChanged(password: String) = update { copy(password = password, feedback = null) }

    fun onRememberSessionChanged(remember: Boolean) = update { copy(rememberSession = remember) }

    fun togglePasswordVisibility() = update { copy(passwordVisible = !passwordVisible) }

    fun dismissFeedback() = update { copy(feedback = null) }

    fun requestPasswordRecovery() = update {
        copy(feedback = LoginFeedback("Recuperación de contraseña", "Contacta con el administrador para restablecer tu contraseña.", false))
    }

    fun login() {
        val current = mutableUiState.value
        if (current.email.isBlank() || current.password.isBlank()) {
            update {
                copy(feedback = LoginFeedback("Campos incompletos", "Introduce tu correo electrónico y contraseña.", true))
            }
            return
        }

        try {
            authService.login(current.email, current.password)
            update {
                copy(
                    isAuthenticated = true,
                    feedback = LoginFeedback("¡Bienvenido a Wise Maps!", "Has iniciado sesión correctamente.", false)
                )
            }
        } catch (_: InvalidCredentialsException) {
            update {
                copy(feedback = LoginFeedback("No se pudo iniciar sesión", "El correo o la contraseña no son correctos.", true))
            }
        }
    }

    private inline fun update(transform: LoginUiState.() -> LoginUiState) {
        mutableUiState.value = mutableUiState.value.transform()
    }
}
