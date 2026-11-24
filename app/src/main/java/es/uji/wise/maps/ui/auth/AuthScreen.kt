package es.uji.wise.maps.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import es.uji.wise.maps.ui.components.StatusBannerCard
import es.uji.wise.maps.ui.theme.WiseIcons

private val AuthTeal = Color(0xFF00685F)
private val AuthInk = Color(0xFF0B1C30)
private val AuthMuted = Color(0xFF566275)
private val AuthCanvas = Color(0xFFF8F9FF)
private val AuthTint = Color(0xFFEFF4FF)
private val AuthTab = Color(0xFFE5EEFF)

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onSelectMode: (AuthMode) -> Unit,
    onNombreChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onRememberSessionChange: (Boolean) -> Unit,
    onTermsAcceptanceChange: (Boolean) -> Unit,
    onRequestPasswordRecovery: () -> Unit,
    onSubmit: () -> Unit,
    onGoToMap: () -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuthCanvas)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD0D0D0)),
                contentAlignment = Alignment.Center
            ) {
                Text("img", color = Color(0xFF555555), style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Wise", style = MaterialTheme.typography.headlineSmall, color = AuthInk, fontWeight = FontWeight.Bold)
                Surface(color = Color(0xFFDAE2FD), shape = CircleShape) {
                    Text("v2.4", color = AuthMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                }
            }
            Text(
                "Tu gestor inteligente de rutas, vehículos y lugares de interés.",
                style = MaterialTheme.typography.bodyMedium,
                color = AuthMuted,
                modifier = Modifier.padding(horizontal = 18.dp)
            )
        }

        val session = uiState.activeSession
        if (session != null && session.autenticado) {
            uiState.banner?.let { StatusBannerCard(banner = it, onDismiss = onDismissBanner) }
            ActiveSessionCard(
                userName = uiState.activeUserName,
                email = session.email,
                onGoToMap = onGoToMap
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().clip(CircleShape).background(AuthTab).padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuthModeTab("Iniciar Sesión", WiseIcons.Login, uiState.mode == AuthMode.LOGIN, Modifier.weight(1f)) { onSelectMode(AuthMode.LOGIN) }
                AuthModeTab("Crear Cuenta", WiseIcons.PersonAdd, uiState.mode == AuthMode.REGISTER, Modifier.weight(1f)) { onSelectMode(AuthMode.REGISTER) }
            }

            uiState.banner?.let { StatusBannerCard(banner = it, onDismiss = onDismissBanner) }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.mode == AuthMode.REGISTER) {
                        AuthField(
                            value = uiState.nombre,
                            onValueChange = onNombreChange,
                            label = "Nombre Completo",
                            placeholder = "Carlos Mendoza Ríos",
                            leading = WiseIcons.Badge,
                            keyboardType = KeyboardType.Text
                        )
                    }

                    val emailValid = uiState.email.matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
                    val emailStatusColor = when {
                        emailValid -> AuthTeal
                        uiState.email.isBlank() -> AuthMuted
                        else -> MaterialTheme.colorScheme.error
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Correo Electrónico", style = MaterialTheme.typography.bodySmall, color = AuthMuted, fontWeight = FontWeight.SemiBold)
                            if (uiState.mode == AuthMode.REGISTER) {
                                val emailLabel = if (uiState.email.isBlank()) "Formato requerido" else if (emailValid) "Formato válido" else "Correo incompleto"
                                Text(emailLabel, style = MaterialTheme.typography.labelSmall, color = emailStatusColor)
                            }
                        }
                        AuthInput(
                            value = uiState.email,
                            onValueChange = onEmailChange,
                            placeholder = if (uiState.mode == AuthMode.REGISTER) "conductor@wise.com" else "ejemplo@wise.com",
                            leading = WiseIcons.Email,
                            trailing = if (uiState.mode == AuthMode.REGISTER) {
                                if (uiState.email.isBlank()) WiseIcons.Email else if (uiState.email.matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))) WiseIcons.Check else WiseIcons.Close
                            } else null,
                            trailingTint = emailStatusColor,
                            keyboardType = KeyboardType.Email
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Contraseña", style = MaterialTheme.typography.bodySmall, color = AuthMuted, fontWeight = FontWeight.SemiBold)
                            if (uiState.mode == AuthMode.LOGIN) {
                                Text(
                                    "¿Olvidaste tu contraseña?",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AuthTeal,
                                    modifier = Modifier.clickable(onClick = onRequestPasswordRecovery)
                                )
                            }
                        }
                        AuthInput(
                            value = uiState.password,
                            onValueChange = onPasswordChange,
                            placeholder = if (uiState.mode == AuthMode.REGISTER) "Mínimo 8 caracteres" else "••••••••••••",
                            leading = WiseIcons.Lock,
                            trailing = WiseIcons.Visibility,
                            onTrailingClick = onTogglePasswordVisibility,
                            keyboardType = KeyboardType.Password,
                            visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                        )
                    }

                    if (uiState.mode == AuthMode.REGISTER) {
                        PasswordStrength(password = uiState.password)
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AuthTint).padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AuthCheckbox(checked = uiState.acceptsTerms, onCheckedChange = onTermsAcceptanceChange)
                            Text(
                                buildAnnotatedString {
                                    append("Acepto las ")
                                    withStyle(SpanStyle(color = AuthTeal, fontWeight = FontWeight.SemiBold, textDecoration = TextDecoration.Underline)) { append("Condiciones de Servicio") }
                                    append(" y la política de privacidad y gestión de flota.")
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = AuthMuted
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AuthCheckbox(checked = uiState.rememberSession, onCheckedChange = onRememberSessionChange)
                                Text("Recordar sesión", style = MaterialTheme.typography.bodySmall, color = AuthMuted)
                            }
                            Surface(color = Color(0xFFDDF8F3), shape = CircleShape) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Box(Modifier.size(7.dp).clip(CircleShape).background(AuthTeal))
                                    Text("GPS activo", style = MaterialTheme.typography.labelSmall, color = AuthTeal)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AuthTeal, contentColor = Color.White)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(if (uiState.mode == AuthMode.LOGIN) "Iniciar sesión" else "Crear Cuenta en Wise", fontWeight = FontWeight.SemiBold)
                            if (uiState.mode == AuthMode.LOGIN) {
                                Text("→", fontWeight = FontWeight.SemiBold)
                            } else {
                                Icon(WiseIcons.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthModeTab(label: String, icon: ImageVector, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (selected) Color.White else Color.Transparent,
            contentColor = if (selected) AuthTeal else AuthMuted
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium)
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leading: ImageVector,
    keyboardType: KeyboardType
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = AuthMuted, fontWeight = FontWeight.SemiBold)
        AuthInput(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            leading = leading,
            keyboardType = keyboardType
        )
    }
}

@Composable
private fun AuthInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leading: ImageVector,
    keyboardType: KeyboardType,
    trailing: ImageVector? = null,
    trailingTint: Color = AuthMuted,
    onTrailingClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = AuthInk),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        cursorBrush = SolidColor(AuthTeal),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuthTint)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(leading, contentDescription = null, tint = AuthMuted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = AuthMuted)
                    }
                    innerTextField()
                }
                if (trailing != null) {
                    Spacer(Modifier.width(8.dp))
                    val trailingModifier = if (onTrailingClick != null) {
                        Modifier.size(28.dp).clip(CircleShape).clickable(onClick = onTrailingClick)
                    } else {
                        Modifier
                    }
                    Box(modifier = trailingModifier, contentAlignment = Alignment.Center) {
                        Icon(trailing, contentDescription = null, tint = trailingTint, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    )
}

@Composable
private fun AuthCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(18.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (checked) AuthTeal else Color.White)
            .border(1.dp, if (checked) AuthTeal else AuthMuted, RoundedCornerShape(3.dp))
            .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Checkbox),
        contentAlignment = Alignment.Center
    ) {
        if (checked) Icon(WiseIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
    }
}

@Composable
private fun PasswordStrength(password: String) {
    val score = listOf(
        password.length >= 6,
        password.length >= 8 && password.any(Char::isDigit),
        password.any(Char::isUpperCase) && password.any(Char::isLowerCase),
        password.any { !it.isLetterOrDigit() }
    ).count { it }
    val labels = listOf("Sin ingresar", "Débil", "Media", "Buena", "Excelente")
    val color = when (score) {
        0, 1 -> Color(0xFFBA1A1A)
        2 -> Color(0xFF825100)
        else -> AuthTeal
    }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Robustez de clave", style = MaterialTheme.typography.labelSmall, color = AuthMuted)
            Text(labels[score], style = MaterialTheme.typography.labelSmall, color = if (score == 0) AuthMuted else color)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(4) { index ->
                Box(
                    Modifier.weight(1f).height(5.dp).clip(CircleShape)
                        .background(if (score > index) color else Color(0xFFDCE9FF))
                )
            }
        }
    }
}

@Composable
private fun ActiveSessionCard(userName: String?, email: String, onGoToMap: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(Modifier.size(52.dp).clip(CircleShape).background(Color(0xFFDDF8F3)), contentAlignment = Alignment.Center) {
                    Icon(WiseIcons.Account, contentDescription = null, tint = AuthTeal)
                }
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(userName ?: "Usuario Wise Maps", style = MaterialTheme.typography.titleLarge, color = AuthInk)
                    Text(email, style = MaterialTheme.typography.bodyMedium, color = AuthMuted)
                }
            }
            Button(onClick = onGoToMap, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = AuthTeal)) {
                Text("Explorar mapa y guardar lugares")
            }
        }
    }
}
