package es.uji.wise.maps.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.uji.wise.maps.ui.theme.WiseError
import es.uji.wise.maps.ui.theme.WiseErrorContainer
import es.uji.wise.maps.ui.theme.WiseIcons
import es.uji.wise.maps.ui.theme.WiseOnTealContainer
import es.uji.wise.maps.ui.theme.WiseSuccess
import es.uji.wise.maps.ui.theme.WiseSuccessContainer
import es.uji.wise.maps.ui.theme.WiseTeal
import es.uji.wise.maps.ui.theme.WiseTealContainer

enum class BannerKind {
    INFO,
    SUCCESS,
    ERROR
}

data class StatusBanner(
    val message: String,
    val kind: BannerKind
)

@Composable
fun StatusBannerCard(
    banner: StatusBanner,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (containerColor, contentColor, icon) = when (banner.kind) {
        BannerKind.INFO -> Triple(WiseTealContainer, WiseOnTealContainer, WiseIcons.Info)
        BannerKind.SUCCESS -> Triple(WiseSuccessContainer, WiseSuccess, WiseIcons.Check)
        BannerKind.ERROR -> Triple(WiseErrorContainer, WiseError, WiseIcons.Info)
    }

    val tint = if (banner.kind == BannerKind.INFO) WiseTeal else contentColor

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = banner.message,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = WiseIcons.Close,
                    contentDescription = "Cerrar aviso",
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
