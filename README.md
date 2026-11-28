# Wise Maps

Aplicación Android de movilidad urbana desarrollada para el **Proyecto Conjunto de EI1039 (Diseño de Software) y EI1048 (Paradigmas de Software)** en la Universitat Jaume I (Curso 2026/2027) por el **Equipo Wise**.

- **Integrantes:** Iván Duarte, Víctor Calvo, Mario De La Horra y Daniel Villar.
- **Panel de seguimiento en vivo:** https://progreso-ramas.netlify.app/

## Objetivo

Permitir a usuarios registrados gestionar lugares de interés, vehículos y preferencias para calcular rutas (a pie, en bicicleta o en coche) sobre un mapa interactivo, consultando distancia, duración, trayecto GeoJSON, coste energético (€ en combustible o electricidad) y gasto calórico (kcal).

## Arquitectura y Stack Tecnológico

- **Lenguaje y UI:** Kotlin + Jetpack Compose + Material 3 + patrón **MVVM con Unidirectional Data Flow (UDF)**.
- **Mapas:** MapLibre GL Android SDK.
- **Red y APIs externas:** Retrofit 2 + Gson (*OpenRouteService* para rutas y geocoding, *datos.gob.es* para carburantes y *Red Eléctrica de España* para electricidad).
- **Pruebas automatizadas (ATDD):** JUnit 5 (Jupiter) + Mockito, separadas en:
  - `app/src/test/java/es/uji/wise/maps/acceptance/` — Pruebas de aceptación.
  - `app/src/test/java/es/uji/wise/maps/integration/` — Pruebas de integración con *mocks* mediante inyección de dependencias.
- **Patrones de diseño (GoF):** `Strategy`, `Facade`, `Adapter`, `Singleton` y `Observer`.
- **CI/CD:** GitHub Actions (`.github/workflows/android-ci.yml`).

### Diagramas UML

- [Diagrama de clases UML – Iteración 1](Diagrama_UML_Iteracion1_Wise.svg)
- [Diagramas de patrones de diseño (GoF)](docs/patrones/)

## Instalación y ejecución local

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/victorzurd/Proyecto_Maps.git
   cd Proyecto_Maps
   ```
2. Configurar la clave gratuita de OpenRouteService en `local.properties` (en la raíz del proyecto):
   ```properties
   ORS_API_KEY=tu_clave_de_openrouteservice
   ```
3. Compilar la aplicación y las pruebas:
   ```bash
   ./gradlew assembleDebug compileDebugUnitTestKotlin
   ```
4. Ejecutar la batería de pruebas de aceptación e integración:
   ```bash
   ./gradlew testDebugUnitTest
   ```
   Para ejecutar también las pruebas de aceptación contra APIs externas reales:
   ```bash
   ./gradlew testDebugUnitTest -PrunOrsIntegrationTests=true
   ```

## Metodología colaborativa (Git Flow + Conventional Commits)

- `main`: rama estable con iteraciones completadas.
- `it-nn`: rama de integración de cada iteración (`it-01` .. `it-05`).
- `h-xx`: rama de historia de usuario (`h-01` .. `h-24`), fusionada mediante PR `ITnn-Hxx`.
- Formato de commits validado por CI: `<type>(itnnhxx): <description in english>` (tipos: `test`, `feat`, `refactor`, `fix`, `chore`, `docs`).

## Licencia

Distribuido bajo licencia MIT. Ver [LICENSE](LICENSE).
