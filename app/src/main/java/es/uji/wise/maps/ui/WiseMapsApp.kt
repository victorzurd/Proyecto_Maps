package es.uji.wise.maps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uji.wise.maps.ui.auth.AuthScreen
import es.uji.wise.maps.ui.auth.AuthViewModel
import es.uji.wise.maps.ui.map.MapScreen
import es.uji.wise.maps.ui.places.PlacesScreen
import es.uji.wise.maps.ui.places.PlacesViewModel
import es.uji.wise.maps.ui.theme.WiseIcons
import es.uji.wise.maps.ui.theme.WiseOrange
import es.uji.wise.maps.ui.theme.WisePlum

enum class WiseDestination(
    val label: String,
    val icon: ImageVector
) {
    ACCOUNT("Cuenta", WiseIcons.Account),
    MAP("Mapa", WiseIcons.MapPin),
    PLACES("Lugares", WiseIcons.PlacesList)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiseMapsApp(
    appContainer: WiseAppContainer = WiseAppGraph.container
) {
    val authViewModel: AuthViewModel = viewModel(factory = appContainer.authViewModelFactory)
    val placesViewModel: PlacesViewModel = viewModel(factory = appContainer.placesViewModelFactory)

    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val placesState by placesViewModel.uiState.collectAsStateWithLifecycle()

    val isAuthenticated = authState.activeSession?.autenticado == true
    var currentDestination by remember(authState.activeSession?.email) {
        mutableStateOf(if (isAuthenticated) WiseDestination.MAP else WiseDestination.ACCOUNT)
    }

    LaunchedEffect(authState.activeSession?.email, isAuthenticated) {
        placesViewModel.onSessionChanged(isAuthenticated)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (isAuthenticated) TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WisePlum),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "W",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "Wise Maps",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = authState.activeSession?.email ?: "Invitado · IT-01",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier.padding(end = 12.dp),
                        shape = RoundedCornerShape(999.dp),
                        color = if (isAuthenticated) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        }
                    ) {
                        Text(
                            text = if (isAuthenticated) "Activa" else "Sin sesión",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAuthenticated) {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            } else {
                                WiseOrange
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (isAuthenticated) NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                WiseDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination == destination,
                        onClick = {
                            currentDestination = destination
                            if (destination == WiseDestination.PLACES) {
                                placesViewModel.refreshPlaces(silentIfNotLoggedIn = false)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        val visibleDestination = if (isAuthenticated) currentDestination else WiseDestination.ACCOUNT

        when (visibleDestination) {
            WiseDestination.ACCOUNT -> AuthScreen(
                uiState = authState,
                onSelectMode = authViewModel::selectMode,
                onNombreChange = authViewModel::onNombreChange,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onTogglePasswordVisibility = authViewModel::togglePasswordVisibility,
                onRememberSessionChange = authViewModel::onRememberSessionChange,
                onTermsAcceptanceChange = authViewModel::onTermsAcceptanceChange,
                onRequestPasswordRecovery = authViewModel::requestPasswordRecovery,
                onSubmit = authViewModel::submit,
                onGoToMap = { currentDestination = WiseDestination.MAP },
                onDismissBanner = authViewModel::dismissBanner,
                modifier = screenModifier
            )

            WiseDestination.MAP -> MapScreen(
                uiState = placesState,
                onMapClick = placesViewModel::onMapCoordinateSelected,
                onPlaceNameChange = placesViewModel::onPlaceNameChange,
                onLatitudeChange = placesViewModel::onLatitudeChange,
                onLongitudeChange = placesViewModel::onLongitudeChange,
                onApplyPreset = placesViewModel::applyPreset,
                onAddPlace = placesViewModel::addPlace,
                onDismissBanner = placesViewModel::dismissBanner,
                modifier = screenModifier
            )

            WiseDestination.PLACES -> PlacesScreen(
                uiState = placesState,
                isAuthenticated = isAuthenticated,
                onRefresh = { placesViewModel.refreshPlaces(silentIfNotLoggedIn = false) },
                onSelectPlaceOnMap = { place ->
                    placesViewModel.focusPlace(place)
                    currentDestination = WiseDestination.MAP
                },
                onGoToMap = { currentDestination = WiseDestination.MAP },
                onGoToAccount = { currentDestination = WiseDestination.ACCOUNT },
                onDismissBanner = placesViewModel::dismissBanner,
                modifier = screenModifier
            )
        }
    }
}
