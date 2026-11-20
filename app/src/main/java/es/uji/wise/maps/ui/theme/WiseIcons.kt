package es.uji.wise.maps.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Iconografía vectorial propia de Wise Maps sin dependencias extra.
 */
object WiseIcons {

    val Account: ImageVector by lazy {
        icon("WiseAccount") {
            path(fill = SolidColor(Color.Black)) {
                // Head
                moveTo(12f, 4f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = true, isPositiveArc = false, 12f, 12f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = true, isPositiveArc = false, 12f, 4f)
                close()
                // Shoulders
                moveTo(4f, 19f)
                curveTo(4f, 15.8f, 7.6f, 14f, 12f, 14f)
                curveTo(16.4f, 14f, 20f, 15.8f, 20f, 19f)
                verticalLineTo(20f)
                horizontalLineTo(4f)
                close()
            }
        }
    }

    val MapPin: ImageVector by lazy {
        icon("WiseMapPin") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 2f)
                curveTo(8.13f, 2f, 5f, 5.13f, 5f, 9f)
                curveTo(5f, 14.25f, 12f, 22f, 12f, 22f)
                curveTo(12f, 22f, 19f, 14.25f, 19f, 9f)
                curveTo(19f, 5.13f, 15.87f, 2f, 12f, 2f)
                close()
                moveTo(12f, 11.5f)
                curveTo(10.62f, 11.5f, 9.5f, 10.38f, 9.5f, 9f)
                curveTo(9.5f, 7.62f, 10.62f, 6.5f, 12f, 6.5f)
                curveTo(13.38f, 6.5f, 14.5f, 7.62f, 14.5f, 9f)
                curveTo(14.5f, 10.38f, 13.38f, 11.5f, 12f, 11.5f)
                close()
            }
        }
    }

    val PlacesList: ImageVector by lazy {
        icon("WisePlacesList") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8f, 6f); lineTo(20f, 6f)
                moveTo(8f, 12f); lineTo(20f, 12f)
                moveTo(8f, 18f); lineTo(20f, 18f)
                moveTo(4f, 6f); lineTo(4.01f, 6f)
                moveTo(4f, 12f); lineTo(4.01f, 12f)
                moveTo(4f, 18f); lineTo(4.01f, 18f)
            }
        }
    }

    val StarFilled: ImageVector by lazy {
        icon("WiseStarFilled") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 2.5f)
                lineTo(14.9f, 8.38f)
                lineTo(21.4f, 9.32f)
                lineTo(16.7f, 13.9f)
                lineTo(17.8f, 20.38f)
                lineTo(12f, 17.32f)
                lineTo(6.2f, 20.38f)
                lineTo(7.3f, 13.9f)
                lineTo(2.6f, 9.32f)
                lineTo(9.1f, 8.38f)
                close()
            }
        }
    }

    val Refresh: ImageVector by lazy {
        icon("WiseRefresh") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.1f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 11f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = true, isPositiveArc = false, 18.2f, 17f)
                moveTo(20f, 4f)
                verticalLineTo(11f)
                horizontalLineTo(13f)
            }
        }
    }

    val Logout: ImageVector by lazy {
        icon("WiseLogout") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.1f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9f, 21f); horizontalLineTo(5f)
                curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f)
                verticalLineTo(5f)
                curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f)
                horizontalLineTo(9f)
                moveTo(16f, 17f); lineTo(21f, 12f); lineTo(16f, 7f)
                moveTo(21f, 12f); horizontalLineTo(9f)
            }
        }
    }

    val Add: ImageVector by lazy {
        icon("WiseAdd") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 5f); verticalLineTo(19f)
                moveTo(5f, 12f); horizontalLineTo(19f)
            }
        }
    }

    val Info: ImageVector by lazy {
        icon("WiseInfo") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 3f)
                arcTo(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = false, 12f, 21f)
                arcTo(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = false, 12f, 3f)
                close()
                moveTo(12f, 11f); verticalLineTo(16f)
                moveTo(12f, 8f); lineTo(12.01f, 8f)
            }
        }
    }

    val Check: ImageVector by lazy {
        icon("WiseCheck") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(5f, 12.5f); lineTo(9.5f, 17f); lineTo(19f, 7.5f)
            }
        }
    }

    val Close: ImageVector by lazy {
        icon("WiseClose") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(6f, 6f); lineTo(18f, 18f)
                moveTo(18f, 6f); lineTo(6f, 18f)
            }
        }
    }

    private fun icon(name: String, block: ImageVector.Builder.() -> Unit): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply(block).build()
    }
}
