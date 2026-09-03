# LahComprahV2

Aplicación Android para gestionar una lista de la compra de forma sencilla y sincronizada en tiempo real.

Desarrollada con **Kotlin** y **Jetpack Compose**, utiliza **Firebase Realtime Database** para almacenar los productos, **Firebase Analytics** para registrar eventos y **Koin** para la inyección de dependencias.

---

## Funcionalidades

* Añadir productos a la lista.
* Indicar la cantidad de cada producto.
* Editar productos existentes.
* Eliminar productos con confirmación previa.
* Sincronizar la lista en tiempo real mediante Firebase.
* Ordenar automáticamente los productos por nombre.
* Mostrar estados de carga y guardado.
* Gestionar errores mediante `Snackbar`.
* Registrar eventos de creación, edición y eliminación mediante Firebase Analytics.
* Tests instrumentados de interfaz con Jetpack Compose.

---

## Tecnologías

| Tecnología                     | Uso                           |
| ------------------------------ | ----------------------------- |
| **Kotlin**                     | Lenguaje principal            |
| **Jetpack Compose**            | Interfaz de usuario           |
| **Material 3**                 | Componentes visuales          |
| **MVVM**                       | Arquitectura de presentación  |
| **StateFlow**                  | Gestión reactiva del estado   |
| **Coroutines**                 | Operaciones asíncronas        |
| **Firebase Realtime Database** | Persistencia y sincronización |
| **Firebase Analytics**         | Registro de eventos           |
| **Koin**                       | Inyección de dependencias     |
| **Compose UI Testing**         | Tests de interfaz             |
| **Gradle Kotlin DSL**          | Configuración del proyecto    |

---

## Arquitectura

El proyecto sigue una estructura basada en **MVVM** junto con el patrón **Repository**.

```text
Jetpack Compose UI
        │
        ▼
ProductListViewModel
        │
        ▼
ProductRepository
        │
        ▼
FirebaseProductRepository
        │
        ▼
Firebase Realtime Database
```

El `ViewModel` no accede directamente a Firebase. En su lugar, trabaja con la interfaz `ProductRepository`, permitiendo desacoplar la capa de presentación de la fuente de datos.

---

## Estructura del proyecto

```text
com.example.lahcomprahv2
│
├── analytics/
│   └── ProductAnalytics.kt
│
├── data/
│   ├── ProductRepository.kt
│   └── FirebaseProductRepository.kt
│
├── di/
│   └── AppModule.kt
│
├── models/
│   └── Product.kt
│
├── ui/
│   ├── screens/
│   │   └── list/
│   │       ├── ProductList.kt
│   │       └── ProductListViewModel.kt
│   │
│   └── theme/
│
├── LahComprahApplication.kt
└── MainActivity.kt
```

---

## Modelo de datos

Cada elemento de la lista se representa mediante el modelo `Product`:

```kotlin
data class Product(
    var id: String = "",
    var nombre: String = "",
    var cantidad: Int = 0
)
```

Cada producto contiene:

* Un identificador generado por Firebase.
* El nombre del producto.
* La cantidad necesaria.

---

## Firebase Realtime Database

Los productos se almacenan en **Firebase Realtime Database** bajo el nodo:

```text
products/
```

La aplicación escucha los cambios mediante un `ValueEventListener` que se transforma en un `Flow` utilizando `callbackFlow`.

Esto permite que la interfaz se actualice automáticamente cuando se añade, modifica o elimina un producto.

```text
Firebase
   │
   ▼
callbackFlow
   │
   ▼
Flow<List<Product>>
   │
   ▼
ViewModel
   │
   ▼
StateFlow
   │
   ▼
Jetpack Compose
```

---

## Gestión del estado

El estado de la pantalla se centraliza en `ProductListUiState`:

```kotlin
data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val completedOperationCount: Int = 0,
    val errorMessage: String? = null
)
```

La interfaz puede reaccionar así a diferentes situaciones:

* Carga inicial de productos.
* Guardado de cambios.
* Operaciones completadas.
* Errores de Firebase.

---

## Inyección de dependencias

El proyecto utiliza **Koin** para proporcionar las principales dependencias de la aplicación.

```kotlin
val appModule = module {
    single { FirebaseAnalytics.getInstance(get()) }
    single { ProductAnalytics(get()) }
    single<ProductRepository> { FirebaseProductRepository(get()) }
    viewModel { ProductListViewModel(get()) }
}
```

Koin se inicializa desde `LahComprahApplication`.

---

## Firebase Analytics

La aplicación registra eventos relacionados con las operaciones realizadas sobre los productos.

Eventos implementados:

```text
product_created
product_updated
product_deleted
```

Los eventos incluyen información como:

```text
product_id
product_name
quantity
```

---

## Interfaz

La interfaz está desarrollada completamente con **Jetpack Compose**.

Entre los componentes utilizados se encuentran:

* `Scaffold`
* `TopAppBar`
* `LazyColumn`
* `Card`
* `ModalBottomSheet`
* `AlertDialog`
* `OutlinedTextField`
* `Snackbar`
* `LinearProgressIndicator`
* `CircularProgressIndicator`

Para añadir o editar un producto se utiliza un `ModalBottomSheet`.

La misma interfaz permite:

* Introducir el nombre.
* Aumentar la cantidad.
* Reducir la cantidad.
* Crear el producto.
* Actualizar un producto existente.

---

## Testing

El proyecto incluye tests instrumentados con **Compose UI Testing**.

Se utiliza un `FakeUiProductRepository` para probar el comportamiento de la interfaz sin depender de Firebase.

Entre los escenarios comprobados se encuentran:

* Confirmación antes de eliminar un producto.
* Cancelación de una eliminación.
* Estado de carga mientras se guarda un producto.
* Cierre del formulario únicamente cuando la operación ha terminado correctamente.

Para ejecutar los tests:

```bash
./gradlew connectedAndroidTest
```

---

## Requisitos

* Android Studio
* Java 11
* Android SDK
* `minSdk 31`
* `targetSdk 34`
* `compileSdk 35`
* Proyecto de Firebase configurado
* Firebase Realtime Database habilitado

---

## Instalación

Clona el repositorio:

```bash
git clone https://github.com/RafaelRio/LahComprahv2.git
```

Accede al proyecto:

```bash
cd LahComprahv2
```

Ábrelo con Android Studio y sincroniza las dependencias de Gradle.

Para compilar el APK de debug:

```bash
./gradlew assembleDebug
```

El APK generado estará disponible en:

```text
app/build/outputs/apk/debug/
```

---

## Configuración de Firebase

Para utilizar el proyecto con tu propia configuración de Firebase:

1. Crea un proyecto en Firebase.
2. Añade una aplicación Android con el package:

```text
com.example.lahcomprahv2
```

3. Activa **Firebase Realtime Database**.
4. Descarga el archivo:

```text
google-services.json
```

5. Colócalo en:

```text
app/google-services.json
```

---

## Características técnicas

* **MVVM**
* **Repository Pattern**
* **Jetpack Compose**
* **Kotlin Coroutines**
* **Flow / StateFlow**
* **Firebase Realtime Database**
* **Firebase Analytics**
* **Koin**
* **Material 3**
* **Compose UI Testing**

---

## Autor

Desarrollado por [Rafael Río](https://github.com/RafaelRio).
