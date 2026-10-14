# Spike: Kotlin + Jetpack Compose para rutas

Aplicación Android nativa mínima para comprobar el ciclo de desarrollo con Kotlin, Compose y pruebas automatizadas. No es todavía el producto ni se conecta a servicios externos: al pulsar **Buscar ruta** muestra datos simulados.

## Qué demuestra

- Pantalla declarativa en Compose y estado local (`remember` / `mutableStateOf`).
- Campos de origen y destino, validación básica y presentación de resultados.
- Separación inicial de la lógica mediante `RouteUseCase` y la abstracción `RouteService`.
- Prueba unitaria JUnit con Mockito que sustituye el servicio por un mock.
- Prueba de interfaz Compose que introduce datos, pulsa el botón y comprueba el resultado.

## Abrir y ejecutar

1. Abrir esta carpeta en Android Studio (Koala o posterior) y aceptar la sincronización de Gradle.
2. Instalar Android SDK Platform 36 y seleccionar un emulador Android o un dispositivo conectado.
3. Ejecutar la configuración `app` para abrir la aplicación.
4. Ejecutar `testDebugUnitTest` para las pruebas JUnit/Mockito y `connectedDebugAndroidTest` con emulador para la prueba de Compose.

Se requiere JDK 17. El proyecto incluye Gradle Wrapper.

## APIs y decisiones que se investigarán para el producto

| Necesidad | API candidata | Qué validar en la siguiente iteración |
|---|---|---|
| Geocodificar nombres y calcular distancia, duración y geometría | OpenRouteService Directions + Geocoding (REST/JSON; requiere clave gratuita) | petición HTTP, autorización, errores, parseo de GeoJSON y límites de uso |
| Mapa Android e interacción de selección de puntos | Mapbox Maps SDK para Android | mostrar mapa, obtener coordenadas al tocar y dibujar LineString; confirmar coste y condiciones de uso |
| Persistencia local | Room sobre SQLite | guardar y recuperar lugares/rutas mediante DAO y pruebas con base temporal |
| Estado de pantalla y llamadas asíncronas | ViewModel + Kotlin Coroutines/Flow | separar UI/lógica y representar carga, éxito y error |

La siguiente iteración sustituiría `RouteService` por una interfaz Retrofit con un cliente OkHttp y un adaptador que traduzca la respuesta GeoJSON a `RouteResult`. La clave de OpenRouteService no debe guardarse en el repositorio; durante el desarrollo se inyectaría localmente y en la aplicación final se acordaría cómo protegerla. El mapa no está incluido en este spike para mantener acotada la primera prueba.

## Conclusión del spike

La viabilidad básica de Kotlin + Compose queda demostrada en la pantalla y las pruebas. Mockito permite aislar el servicio externo y verificar llamadas sin depender de red o claves. Antes de iniciar las historias de usuario falta un spike corto de integración real para OpenRouteService y Mapbox, especialmente para validar el intercambio de coordenadas y GeoJSON.

## Uso de IA generativa

Se usó ChatGPT para revisar los documentos de la entrega, proponer el alcance mínimo del spike y generar una primera estructura de Compose y pruebas. Interacción: «hazme el spike del proyecto queremos hacer un proyecto asi Aplicación móvil nativa: Jetpack Compose + Kotlin (tests con JUnit + Mockito) el spike a de ser algo muy basico que sirva para saber como funciona la tecnologia de kotlin y jetpack compose y las apis que haya que usar». La respuesta se utilizó como borrador y se contrastó con la rúbrica y los requisitos adjuntos. Aporta velocidad para crear el esqueleto; puede proponer dependencias o APIs incompatibles con el entorno y no sustituye la validación del equipo. La respuesta de rutas del spike es simulada, así que no demuestra todavía conectividad real ni precisión de rutas.
