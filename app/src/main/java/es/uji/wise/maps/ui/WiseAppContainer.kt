package es.uji.wise.maps.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.uji.wise.maps.model.PlaceOfInterest
import es.uji.wise.maps.model.User
import es.uji.wise.maps.repository.InMemoryPlaceRepository
import es.uji.wise.maps.repository.InMemoryUserRepository
import es.uji.wise.maps.repository.PlaceRepository
import es.uji.wise.maps.repository.UserRepository
import es.uji.wise.maps.service.AuthService
import es.uji.wise.maps.service.PlaceService
import es.uji.wise.maps.service.SessionManager
import es.uji.wise.maps.ui.auth.AuthViewModel
import es.uji.wise.maps.ui.places.PlacesViewModel

/**
 * Contenedor de inyección de dependencias manual de Wise Maps.
 * Centraliza el cableado entre repositorios (actualmente en memoria y preparados
 * para sustituirse por Firebase Auth / Cloud Firestore), servicios de dominio y ViewModels.
 */
class WiseAppContainer(
    val userRepository: UserRepository = InMemoryUserRepository(
        initialUsers = listOf(
            User("Juan Pérez", "juan@example.com", "Pass1234")
        )
    ),
    val placeRepository: PlaceRepository = InMemoryPlaceRepository(
        initialPlaces = mapOf(
            "juan@example.com" to listOf(
                PlaceOfInterest(
                    nombre = "Universitat Jaume I",
                    latitud = 39.9945,
                    longitud = -0.0688,
                    favorito = true,
                    ordenAlta = 1
                ),
                PlaceOfInterest(
                    nombre = "Castellón de la Plana",
                    latitud = 39.9864,
                    longitud = -0.0513,
                    favorito = false,
                    ordenAlta = 2
                ),
                PlaceOfInterest(
                    nombre = "Valencia",
                    latitud = 39.4699,
                    longitud = -0.3763,
                    favorito = true,
                    ordenAlta = 3
                )
            )
        )
    ),
    val sessionManager: SessionManager = SessionManager()
) {
    val authService: AuthService = AuthService(userRepository, sessionManager)
    val placeService: PlaceService = PlaceService(placeRepository, sessionManager)

    val authViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(
                authService = authService,
                userRepository = userRepository,
                sessionManager = sessionManager
            ) as T
        }
    }

    val placesViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlacesViewModel(
                placeService = placeService,
                sessionManager = sessionManager
            ) as T
        }
    }
}

/**
 * Grafo de aplicación retenido en memoria durante la vida del proceso para
 * preservar usuarios, lugares y sesión activa ante rotaciones de pantalla.
 */
object WiseAppGraph {
    val container: WiseAppContainer by lazy { WiseAppContainer() }
}
