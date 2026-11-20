package es.uji.wise.maps.ui.places

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.uji.wise.maps.model.PlaceOfInterest
import es.uji.wise.maps.ui.components.StatusBannerCard
import es.uji.wise.maps.ui.theme.WiseIcons
import es.uji.wise.maps.ui.theme.WiseOnOrangeContainer
import es.uji.wise.maps.ui.theme.WiseOrange
import es.uji.wise.maps.ui.theme.WiseOrangeContainer
import es.uji.wise.maps.ui.theme.WiseTeal
import es.uji.wise.maps.ui.theme.WiseTealContainer
import java.util.Locale

@Composable
fun PlacesScreen(
    uiState: PlacesUiState,
    isAuthenticated: Boolean,
    onRefresh: () -> Unit,
    onSelectPlaceOnMap: (PlaceOfInterest) -> Unit,
    onGoToMap: () -> Unit,
    onGoToAccount: () -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mis lugares (HU-07)",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "${uiState.places.size} lugares · favoritos primero",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FilledTonalButton(onClick = onRefresh) {
                Icon(
                    imageVector = WiseIcons.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Actualizar")
            }
        }

        uiState.banner?.let { banner ->
            StatusBannerCard(banner = banner, onDismiss = onDismissBanner)
        }

        if (uiState.places.isEmpty()) {
            EmptyPlacesCard(
                isAuthenticated = isAuthenticated,
                onGoToMap = onGoToMap,
                onGoToAccount = onGoToAccount
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = uiState.places,
                    key = { "${it.ordenAlta}-${it.nombre}" }
                ) { place ->
                    PlaceCard(
                        place = place,
                        onShowOnMap = { onSelectPlaceOnMap(place) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceCard(
    place: PlaceOfInterest,
    onShowOnMap: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (place.favorito) WiseOrangeContainer else WiseTealContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (place.favorito) WiseIcons.StarFilled else WiseIcons.MapPin,
                    contentDescription = null,
                    tint = if (place.favorito) WiseOrange else WiseTeal,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = place.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (place.favorito) {
                        Surface(
                            color = WiseOrangeContainer,
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = "Favorito",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = WiseOnOrangeContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = String.format(
                        Locale.US,
                        "Lat %.4f · Lon %.4f · Alta #%d",
                        place.latitud,
                        place.longitud,
                        place.ordenAlta
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledTonalButton(
                onClick = onShowOnMap,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Mapa")
            }
        }
    }
}

@Composable
private fun EmptyPlacesCard(
    isAuthenticated: Boolean,
    onGoToMap: () -> Unit,
    onGoToAccount: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = WiseIcons.MapPin,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = if (isAuthenticated) {
                    "Todavía no hay lugares cargados"
                } else {
                    "Inicia sesión para ver tus lugares"
                },
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = if (isAuthenticated) {
                    "Pulsa en cualquier punto del mapa interactivo para dar de alta tu primer lugar de interés."
                } else {
                    "Tus lugares de interés están vinculados a tu cuenta de Wise Maps."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = if (isAuthenticated) onGoToMap else onGoToAccount,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (isAuthenticated) "Ir al mapa" else "Ir a Cuenta")
            }
        }
    }
}
