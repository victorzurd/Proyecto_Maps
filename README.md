# Spike: Kotlin + Jetpack Compose para rutas

Aplicación Android nativa mínima para comprobar Kotlin, Compose, pruebas automatizadas y la integración de OpenRouteService. Al buscar una ruta, geocodifica origen/destino y solicita una ruta real en coche.

## Qué demuestra

- Pantalla declarativa en Compose y estado local (`remember` / `mutableStateOf`).
- Campos de origen y destino, validación básica y presentación de resultados.
- Separación de lógica mediante `RouteUseCase` y la abstracción `RouteService`; `OpenRouteService` adapta la API real.
- Llamadas HTTP asíncronas con Retrofit y coroutines, parseo de GeoJSON y presentación de errores.
- Prueba unitaria JUnit con Mockito que sustituye el servicio por un mock.
- Prueba de integración opcional contra la API real con márgenes plausibles para Castellón–Valencia.
- Prueba de interfaz Compose que introduce datos, pulsa el botón y comprueba el resultado.

## Abrir y ejecutar

1. Abrir esta carpeta en Android Studio (Koala o posterior) y aceptar la sincronización de Gradle.
2. Instalar Android SDK Platform 36 y seleccionar un emulador Android o un dispositivo conectado.
3. Crear una cuenta y una clave de API gratuita en [OpenRouteService](https://openrouteservice.org/dev/#/signup).
4. En el `local.properties` de la raíz añade `ORS_API_KEY=...` (sin comillas). No sustituyas la línea `sdk.dir` que Android Studio ya haya creado.
5. Sincroniza Gradle y ejecuta la configuración `app`.
6. Ejecuta `testDebugUnitTest` para los tests unitarios con Mockito y `connectedDebugAndroidTest` con emulador para la prueba de Compose.
7. Para consultar también la API real, activa la prueba de integración con `gradlew.bat :app:testDebugUnitTest --tests es.uji.rutaspike.OpenRouteServiceIntegrationTest -PrunOrsIntegrationTests=true`. Necesita Internet y `ORS_API_KEY`; comprueba que la distancia entre Castelló de la Plana y València esté entre 55–110 km y la duración entre 20–150 min. Si no se activa la propiedad, esta prueba queda omitida.

Se requiere JDK 17. El proyecto incluye Gradle Wrapper.

## APIs y decisiones que se investigarán para el producto

| Necesidad | API candidata | Qué validar en la siguiente iteración |
|---|---|---|
| Geocodificar nombres y calcular distancia, duración y geometría | OpenRouteService Geocoding + Directions (REST/JSON/GeoJSON; requiere clave) | La integración de aprendizaje ya está implementada en `OpenRouteService.kt` |
| Mapa Android e interacción de selección de puntos | MapLibre Native Android (integrado con `AndroidView` en Compose) | Ya muestra un mapa y dibuja la LineString de OpenRouteService; queda añadir selección de coordenadas al tocar |
| Persistencia local | Room sobre SQLite | guardar y recuperar lugares/rutas mediante DAO y pruebas con base temporal |
| Estado de pantalla y llamadas asíncronas | ViewModel + Kotlin Coroutines/Flow | separar UI/lógica y representar carga, éxito y error |

MapLibre no necesita token de Mapbox. El ejemplo carga el estilo de demostración de MapLibre (`demotiles.maplibre.org`) y por eso requiere Internet; para producción habrá que elegir un proveedor de teselas y respetar sus condiciones y atribución. OpenRouteService se configura localmente en `local.properties`, que está excluido de Git. La API entrega coordenadas GeoJSON en orden [longitud, latitud].

## Conclusión del spike

La viabilidad básica de Kotlin + Compose, OpenRouteService y MapLibre queda abordada: la ruta calculada se dibuja sobre el mapa. Mockito permite probar el caso de uso sin depender de red ni claves. Persistencia local con Room queda pendiente de un spike posterior.

## Uso de IA generativa

Se usó ChatGPT para revisar los documentos de la entrega, proponer el alcance mínimo del spike y generar una primera estructura de Compose, APIs y pruebas. Interacción inicial: «hazme el spike del proyecto queremos hacer un proyecto asi Aplicación móvil nativa: Jetpack Compose + Kotlin (tests con JUnit + Mockito) el spike a de ser algo muy basico que sirva para saber como funciona la tecnologia de kotlin y jetpack compose y las apis que haya que usar». También se usó para sustituir Mapbox por MapLibre tras identificar que Mapbox requiere añadir un método de pago para crear tokens adicionales. Las sugerencias se contrastaron con la rúbrica, los requisitos y la documentación oficial. La IA aceleró el esqueleto, pero propuso una integración que necesitaba credenciales de pago y sus cambios de Gradle requirieron correcciones. La ruta usa ahora la API real de OpenRouteService; la precisión y robustez deben validarse con pruebas del equipo.
