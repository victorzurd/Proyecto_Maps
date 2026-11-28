package es.uji.wise.maps.ui.places

import androidx.lifecycle.ViewModel
import es.uji.wise.maps.model.PlaceOfInterest
import es.uji.wise.maps.model.UnauthorizedActionException
import es.uji.wise.maps.service.PlaceService
import es.uji.wise.maps.service.SessionManager
import es.uji.wise.maps.ui.components.BannerKind
import es.uji.wise.maps.ui.components.StatusBanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

data class PlacesUiState(
    val places: List<PlaceOfInterest> = emptyList(),
    val placeName: String = "",
    val latitudeInput: String = "39.99450",
    val longitudeInput: String = "-0.06880",
    val selectedLat: Double? = 39.99450,
    val selectedLon: Double? = -0.06880,
    val focusedPlace: PlaceOfInterest? = null,
    val banner: StatusBanner? = null
)

/**
 * ViewModel MVVM para gestión de lugares de interés de Iteración 1:
 * HU-05 (Alta por coordenadas) y HU-07 (Consulta ordenada de lugares).
 */
class PlacesViewModel(
    private val placeService: PlaceService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlacesUiState())
    val uiState: StateFlow<PlacesUiState> = _uiState.asStateFlow()

    fun onPlaceNameChange(value: String) {
        _uiState.update { it.copy(placeName = value) }
    }

    fun onLatitudeChange(value: String) {
        val parsed = parseCoordinate(value)
        _uiState.update {
            it.copy(
                latitudeInput = value,
                selectedLat = parsed?.takeIf { lat -> lat in -90.0..90.0 }
            )
        }
    }

    fun onLongitudeChange(value: String) {
        val parsed = parseCoordinate(value)
        _uiState.update {
            it.copy(
                longitudeInput = value,
                selectedLon = parsed?.takeIf { lon -> lon in -180.0..180.0 }
            )
        }
    }

    fun onMapCoordinateSelected(lat: Double, lon: Double) {
        _uiState.update {
            it.copy(
                latitudeInput = formatCoordinate(lat),
                longitudeInput = formatCoordinate(lon),
                selectedLat = lat,
                selectedLon = lon,
                focusedPlace = null
            )
        }
    }

    fun applyPreset(nombre: String, lat: Double, lon: Double) {
        val previewPlace = PlaceOfInterest(nombre = nombre, latitud = lat, longitud = lon)
        _uiState.update {
            it.copy(
                placeName = nombre,
                latitudeInput = formatCoordinate(lat),
                longitudeInput = formatCoordinate(lon),
                selectedLat = lat,
                selectedLon = lon,
                focusedPlace = previewPlace,
                banner = null
            )
        }
    }

    fun addPlace() {
        val state = _uiState.value
        val nombre = state.placeName.trim()
        val lat = parseCoordinate(state.latitudeInput)
        val lon = parseCoordinate(state.longitudeInput)

        if (nombre.isEmpty()) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = "Introduce un nombre para el lugar de interés.",
                        kind = BannerKind.ERROR
                    )
                )
            }
            return
        }

        if (lat == null || lon == null) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = "Las coordenadas deben ser valores numéricos válidos.",
                        kind = BannerKind.ERROR
                    )
                )
            }
            return
        }

        try {
            val saved = placeService.addPlaceByCoordinates(
                nombre = nombre,
                latitud = lat,
                longitud = lon
            )
            val updatedList = runCatching { placeService.getPlaces() }
                .getOrElse { _uiState.value.places + saved }

            _uiState.update {
                it.copy(
                    places = updatedList,
                    placeName = "",
                    selectedLat = saved.latitud,
                    selectedLon = saved.longitud,
                    focusedPlace = saved,
                    banner = StatusBanner(
                        message = "Lugar \"${saved.nombre}\" guardado correctamente.",
                        kind = BannerKind.SUCCESS
                    )
                )
            }
        } catch (_: NotImplementedError) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = "HU-05 (Alta por coordenadas) pendiente de fusionar la rama h-05 en it-01.",
                        kind = BannerKind.INFO
                    )
                )
            }
        } catch (ex: RuntimeException) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = ex.message ?: "No se pudo guardar el lugar de interés.",
                        kind = BannerKind.ERROR
                    )
                )
            }
        }
    }

    fun refreshPlaces(silentIfNotLoggedIn: Boolean = false) {
        if (silentIfNotLoggedIn && sessionManager.sesionActiva?.autenticado != true) {
            _uiState.update { it.copy(places = emptyList()) }
            return
        }

        try {
            val loaded = placeService.getPlaces()
            _uiState.update {
                it.copy(
                    places = loaded,
                    banner = null
                )
            }
        } catch (_: NotImplementedError) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = "HU-07 (Consulta de lugares) pendiente de fusionar la rama h-07 en it-01.",
                        kind = BannerKind.INFO
                    )
                )
            }
        } catch (_: UnauthorizedActionException) {
            _uiState.update {
                it.copy(
                    places = emptyList(),
                    banner = StatusBanner(
                        message = "Inicia sesión en la pestaña Cuenta para consultar tus lugares guardados.",
                        kind = BannerKind.INFO
                    )
                )
            }
        } catch (ex: RuntimeException) {
            _uiState.update {
                it.copy(
                    banner = StatusBanner(
                        message = ex.message ?: "No se pudo obtener la lista de lugares.",
                        kind = BannerKind.ERROR
                    )
                )
            }
        }
    }

    fun focusPlace(place: PlaceOfInterest) {
        _uiState.update {
            it.copy(
                focusedPlace = place,
                selectedLat = place.latitud,
                selectedLon = place.longitud,
                latitudeInput = formatCoordinate(place.latitud),
                longitudeInput = formatCoordinate(place.longitud)
            )
        }
    }

    fun onSessionChanged(isAuthenticated: Boolean) {
        if (isAuthenticated) {
            refreshPlaces(silentIfNotLoggedIn = false)
        } else {
            _uiState.update { it.copy(places = emptyList(), banner = null) }
        }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(banner = null) }
    }

    private fun parseCoordinate(raw: String): Double? =
        raw.trim().replace(',', '.').toDoubleOrNull()

    private fun formatCoordinate(value: Double): String =
        String.format(Locale.US, "%.5f", value)
}
