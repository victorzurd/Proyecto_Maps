# Spike: Kotlin + Jetpack Compose para rutas

Aplicación Android nativa mínima para comprobar Kotlin, Compose, pruebas automatizadas y la integración de OpenRouteService. Al buscar una ruta, geocodifica origen/destino y solicita una ruta real en coche.

## Qué demuestra

- Pantalla declarativa en Compose y estado local (`remember` / `mutableStateOf`).
- Campos de origen y destino, validación básica y presentación de resultados.
- Separación de lógica mediante `RouteUseCase` y la abstracción `RouteService`; `OpenRouteService` adapta la API real.
- Llamadas HTTP asíncronas con Retrofit y coroutines, parseo de GeoJSON y presentación de errores.
- Prueba unitaria JUnit con Mockito que sustituye el servicio por un mock.
- Prueba de interfaz Compose que introduce datos, pulsa el botón y comprueba el resultado.

## Abrir y ejecutar

1. Abrir esta carpeta en Android Studio (Koala o posterior) y aceptar la sincronización de Gradle.
2. Instalar Android SDK Platform 36 y seleccionar un emulador Android o un dispositivo conectado.
3. Crear una cuenta y una clave de API gratuita en [OpenRouteService](https://openrouteservice.org/dev/#/signup).
4. En el `local.properties` de la raíz, añade una línea `ORS_API_KEY=tu_clave` (sin comillas). No sustituyas la línea `sdk.dir` que Android Studio ya haya creado.
5. Sincroniza Gradle y ejecuta la configuración `app`.
6. Ejecuta `testDebugUnitTest` para JUnit/Mockito y `connectedDebugAndroidTest` con emulador para la prueba de Compose.

Se requiere JDK 17. El proyecto incluye Gradle Wrapper.

## APIs y decisiones que se investigarán para el producto

| Necesidad | API candidata | Qué validar en la siguiente iteración |
|---|---|---|
| Geocodificar nombres y calcular distancia, duración y geometría | OpenRouteService Geocoding + Directions (REST/JSON/GeoJSON; requiere clave) | La integración de aprendizaje ya está implementada en `OpenRouteService.kt` |
| Mapa Android e interacción de selección de puntos | Mapbox Maps SDK para Android | mostrar mapa, obtener coordenadas al tocar y dibujar LineString; confirmar coste y condiciones de uso |
| Persistencia local | Room sobre SQLite | guardar y recuperar lugares/rutas mediante DAO y pruebas con base temporal |
| Estado de pantalla y llamadas asíncronas | ViewModel + Kotlin Coroutines/Flow | separar UI/lógica y representar carga, éxito y error |

La clave se lee localmente desde `local.properties`, archivo excluido de Git. Al ser una app cliente, la clave incluida en un APK puede extraerse; esta técnica es solo para el spike, no para producción. La API devuelve coordenadas en orden [longitud, latitud] y la ruta en GeoJSON. El mapa Mapbox todavía no está integrado; es el siguiente experimento recomendado para dibujar la geometría de la ruta.

## Conclusión del spike

La viabilidad de Kotlin + Compose y de la integración real de OpenRouteService queda abordada. Mockito permite probar el caso de uso sin depender de red ni de claves. Mapbox y persistencia local con Room quedan pendientes de un spike posterior.

## Uso de IA generativa

Se usó ChatGPT para revisar los documentos de la entrega, proponer el alcance mínimo del spike y generar una primera estructura de Compose y pruebas. Interacción: «hazme el spike del proyecto queremos hacer un proyecto asi Aplicación móvil nativa: Jetpack Compose + Kotlin (tests con JUnit + Mockito) el spike a de ser algo muy basico que sirva para saber como funciona la tecnologia de kotlin y jetpack compose y las apis que haya que usar». La respuesta se utilizó como borrador y se contrastó con la rúbrica y los requisitos adjuntos. Aporta velocidad para crear el esqueleto; puede proponer dependencias o APIs incompatibles con el entorno y no sustituye la validación del equipo. La respuesta de rutas del spike es simulada, así que no demuestra todavía conectividad real ni precisión de rutas.
