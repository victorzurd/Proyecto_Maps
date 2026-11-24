package es.uji.wise.maps.ui.auth

import androidx.lifecycle.ViewModel
import es.uji.wise.maps.model.Session
import es.uji.wise.maps.repository.UserRepository
import es.uji.wise.maps.service.AuthService
import es.uji.wise.maps.service.SessionManager
import es.uji.wise.maps.ui.components.BannerKind
import es.uji.wise.maps.ui.components.StatusBanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AuthMode {
    LOGIN,
    REGISTER
}

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val rememberSession: Boolean = true,
    val acceptsTerms: Boolean = false,
    val activeSession: Session? = null,
    val activeUserName: String? = null,
    val banner: StatusBanner? = null
)

/**
 * ViewModel MVVM (Unidirectional Data Flow) para HU-01 (Registro) y
 * HU-02 (Inicio de sesión).
 */
class AuthViewModel(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            activeSession = sessionManager.sesionActiva,
            activeUserName = sessionManager.sesionActiva?.let { session ->
                userRepository.findByEmail(session.email)?.nombre
            }
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun selectMode(mode: AuthMode) {
        _uiState.update { it.copy(mode = mode, banner = null) }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onRememberSessionChange(remember: Boolean) {
        _uiState.update { it.copy(rememberSession = remember) }
    }

    fun onTermsAcceptanceChange(accepted: Boolean) {
        _uiState.update { it.copy(acceptsTerms = accepted) }
    }

    fun requestPasswordRecovery() {
        _uiState.update {
            it.copy(
                banner = StatusBanner(
                    message = "Para recuperar tu contraseña, contacta con el administrador.",
                    kind = BannerKind.INFO
                )
            )
        }
    }

    fun fillDemoAccount() {
        _uiState.update { current ->
            current.copy(
                nombre = if (current.mode == AuthMode.REGISTER) "María García" else current.nombre,
                email = if (current.mode == AuthMode.REGISTER) "maria@example.com" else "juan@example.com",
                password = if (current.mode == AuthMode.REGISTER) "Segura123" else "Pass1234",
                banner = null
            )
        }
    }

    fun submit() {
        when (_uiState.value.mode) {
            AuthMode.REGISTER -> register()
            AuthMode.LOGIN -> login()
        }
    }

    private fun register() {
        val state = _uiState.value
        try {
            val user = authService.register(
                nombre = state.nombre,
                email = state.email,
                password = state.password
            )
            _uiState.update {
                it.copy(
                    mode = AuthMode.LOGIN,
                    email = user.email,
                    password = "",
                    banner = StatusBanner(
                        message = "Cuenta creada para ${user.nombre} (${user.email}). Ahora puedes iniciar sesión.",
                        kind = BannerKind.SUCCESS
                    )
                )
            }
        } catch (_: NotImplementedError) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = "HU-01 (Registro) aún no está fusionada en it-01. En cuanto se acepte la PR #31 funcionará de extremo a extremo.",
                        kind = BannerKind.INFO
                    )
                )
            }
        } catch (ex: RuntimeException) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = ex.message ?: "No se pudo completar el registro.",
                        kind = BannerKind.ERROR
                    )
                )
            }
        }
    }

    private fun login() {
        val state = _uiState.value
        try {
            val session = authService.login(
                email = state.email,
                password = state.password
            )
            val resolvedName = userRepository.findByEmail(session.email)?.nombre
            _uiState.update {
                it.copy(
                    activeSession = session,
                    activeUserName = resolvedName,
                    password = "",
                    banner = StatusBanner(
                        message = "Sesión iniciada como ${resolvedName ?: session.email}.",
                        kind = BannerKind.SUCCESS
                    )
                )
            }
        } catch (_: NotImplementedError) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = "HU-02 (Inicio de sesión) pendiente de fusionar la rama h-02 en it-01.",
                        kind = BannerKind.INFO
                    )
                )
            }
        } catch (ex: RuntimeException) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = ex.message ?: "No se pudo iniciar sesión.",
                        kind = BannerKind.ERROR
                    )
                )
            }
        }
    }

    fun logout() {
        try {
            authService.logout()
            _uiState.update {
                it.copy(
                    activeSession = null,
                    activeUserName = null,
                    banner = StatusBanner(
                        message = "Sesión cerrada correctamente.",
                        kind = BannerKind.SUCCESS
                    )
                )
            }
        } catch (ex: RuntimeException) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = ex.message ?: "No se pudo cerrar la sesión.",
                        kind = BannerKind.ERROR
                    )
                )
            }
        }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(banner = null) }
    }
}
