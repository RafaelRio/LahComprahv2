🛒 LahComprahV2

Aplicación Android para gestionar una lista de la compra de forma sencilla y en tiempo real.

Desarrollada en Kotlin con Jetpack Compose, utiliza Firebase Realtime Database para almacenar y sincronizar los productos, Firebase Analytics para registrar interacciones y Koin para la inyección de dependencias.

✨ Funcionalidades
➕ Añadir productos a la lista.
🔢 Seleccionar la cantidad de cada producto.
✏️ Editar productos existentes.
🗑️ Eliminar productos con diálogo de confirmación.
☁️ Almacenamiento mediante Firebase Realtime Database.
🔄 Actualización reactiva de la lista cuando cambian los datos.
🔤 Ordenación alfabética automática de los productos.
⏳ Indicadores de carga y guardado.
⚠️ Gestión de errores mediante Snackbar.
📊 Registro de altas, modificaciones y eliminaciones mediante Firebase Analytics.
🧪 Tests instrumentados de interfaz con Compose UI Testing.
📱 Funcionamiento

La pantalla principal muestra todos los productos almacenados en Firebase.

Cada elemento de la lista contiene:

Nombre del producto.
Cantidad.
Botón para editarlo.
Botón para eliminarlo.

Para añadir un nuevo producto se utiliza un Modal Bottom Sheet desde el que se puede introducir el nombre y seleccionar la cantidad.

┌──────────────────────────────┐
│         LahComprahV2      +  │
├──────────────────────────────┤
│                              │
│  Pan                         │
│  Cantidad: 2        ✏️  🗑️  │
│                              │
│  Leche                       │
│  Cantidad: 1        ✏️  🗑️  │
│                              │
│  Huevos                      │
│  Cantidad: 12       ✏️  🗑️  │
│                              │
└──────────────────────────────┘

Los cambios realizados en la lista se almacenan en Firebase y la interfaz se actualiza automáticamente.

🛠️ Stack tecnológico
Tecnología	Uso
Kotlin	Lenguaje principal
Jetpack Compose	Desarrollo de la interfaz
Material 3	Componentes visuales
MVVM	Organización de la lógica de presentación
StateFlow	Gestión reactiva del estado de UI
Coroutines	Operaciones asíncronas
Firebase Realtime Database	Persistencia y sincronización de productos
Firebase Analytics	Registro de eventos de uso
Koin	Inyección de dependencias
Compose UI Testing	Tests instrumentados de interfaz
Gradle Kotlin DSL	Configuración del proyecto
🧱 Arquitectura

El proyecto utiliza una estructura basada en MVVM, separando la interfaz, la gestión del estado y el acceso a datos.

┌─────────────────────────┐
│       Compose UI        │
│   ProductsListScreen    │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│        ViewModel        │
│  ProductListViewModel   │
│                         │
│      StateFlow          │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│       Repository        │
│    ProductRepository    │
│            │            │
│            ▼            │
│ FirebaseProductRepo...  │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│ Firebase Realtime DB    │
└─────────────────────────┘
UI

La interfaz está desarrollada completamente con Jetpack Compose.

ProductsListScreen observa el estado proporcionado por el ViewModel y reacciona a los cambios de:

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val completedOperationCount: Int = 0,
    val errorMessage: String? = null
)
ViewModel

ProductListViewModel contiene la lógica de presentación y expone el estado mediante un StateFlow.

Firebase
   │
   ▼
Repository
   │
   ▼
Flow<List<Product>>
   │
   ▼
ViewModel
   │
   ▼
StateFlow<ProductListUiState>
   │
   ▼
Jetpack Compose

El ViewModel permite:

Observar los productos.
Añadir productos.
Editar productos.
Eliminar productos.
Gestionar estados de carga.
Gestionar errores.
🔥 Firebase Realtime Database

La aplicación utiliza Firebase Realtime Database como fuente de datos.

Los productos se almacenan bajo:

products/
├── product-id-1/
│   ├── id
│   ├── nombre
│   └── cantidad
│
├── product-id-2/
│   ├── id
│   ├── nombre
│   └── cantidad
│
└── ...

El modelo utilizado es:

data class Product(
    var id: String = "",
    var nombre: String = "",
    var cantidad: Int = 0
)

La aplicación utiliza un ValueEventListener y lo adapta a un Flow mediante callbackFlow.

Esto permite que cualquier modificación en Firebase actualice automáticamente el estado observado por la interfaz.

📦 Repository Pattern

El acceso a datos está abstraído mediante la interfaz:

interface ProductRepository {

    fun observeProducts(): Flow<List<Product>>

    suspend fun addProduct(
        name: String,
        quantity: Int
    )

    suspend fun deleteProduct(product: Product)

    suspend fun updateProduct(product: Product)
}

La implementación utilizada en producción es:

FirebaseProductRepository

De esta forma, el ViewModel no depende directamente de Firebase y trabaja únicamente con la abstracción ProductRepository.

💉 Inyección de dependencias

El proyecto utiliza Koin para gestionar las dependencias.

El módulo principal proporciona:

FirebaseAnalytics
       │
       ▼
ProductAnalytics

FirebaseProductRepository
       │
       ▼
ProductRepository
       │
       ▼
ProductListViewModel

La inicialización de Koin se realiza desde LahComprahApplication.

startKoin {
    androidContext(this@LahComprahApplication)
    modules(appModule)
}
📊 Firebase Analytics

La aplicación registra las principales operaciones realizadas sobre los productos.

Eventos
product_created
product_updated
product_deleted

Cada evento puede contener:

product_id
product_name
quantity

Esto permite analizar cómo se utiliza la gestión de productos dentro de la aplicación.

🎨 Interfaz

La aplicación utiliza componentes de Material 3 y Jetpack Compose como:

Scaffold
TopAppBar
LazyColumn
Card
ModalBottomSheet
AlertDialog
OutlinedTextField
Snackbar
CircularProgressIndicator
LinearProgressIndicator

El formulario para crear y editar productos comparte el mismo componente y adapta automáticamente su comportamiento dependiendo de si existe un producto seleccionado.

⚡ Gestión de estado

La UI contempla diferentes estados durante las operaciones con Firebase.

Carga inicial
Firebase → Repository → ViewModel
                         │
                         ▼
                    isLoading

Mientras se recuperan los productos se muestra un LinearProgressIndicator.

Guardado

Durante una creación o actualización:

isSaving = true

El botón muestra un indicador de progreso y se deshabilitan las acciones necesarias hasta completar la operación.

Errores

Los errores generados durante operaciones contra Firebase se incorporan al UiState y se muestran mediante un Snackbar.

🧪 Testing

El proyecto incluye tests instrumentados de Jetpack Compose.

Los tests utilizan un FakeUiProductRepository, evitando depender de Firebase durante las pruebas de interfaz.

Entre los comportamientos comprobados se encuentran:

Eliminación segura

Se verifica que un producto no sea eliminado inmediatamente al pulsar el botón y que sea necesario confirmar primero la acción.

Eliminar
   │
   ▼
AlertDialog
   ├── Cancelar ──► mantiene producto
   │
   └── Confirmar ─► elimina producto
Estado de guardado

También se comprueba que el formulario permanezca visible mientras el repositorio está procesando una operación y que desaparezca únicamente cuando el guardado ha terminado correctamente.

Para ejecutar los tests instrumentados:

./gradlew connectedAndroidTest
📁 Estructura del proyecto
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
│
└── MainActivity.kt
🔄 Flujo de datos
            ┌───────────────────┐
            │ Firebase Database │
            └─────────┬─────────┘
                      │
                      ▼
        ┌─────────────────────────┐
        │ FirebaseProductRepository│
        └────────────┬────────────┘
                     │ Flow
                     ▼
        ┌─────────────────────────┐
        │  ProductListViewModel   │
        │                         │
        │       StateFlow         │
        └────────────┬────────────┘
                     │
                     ▼
        ┌─────────────────────────┐
        │     Jetpack Compose     │
        │   ProductsListScreen    │
        └─────────────────────────┘
⚙️ Requisitos
Android Studio
Java 11
minSdk 31
targetSdk 34
compileSdk 35
Proyecto de Firebase configurado
Firebase Realtime Database habilitado
🚀 Instalación

Clona el repositorio:

git clone https://github.com/RafaelRio/LahComprahv2.git

Accede al proyecto:

cd LahComprahv2

Abre el proyecto en Android Studio y sincroniza Gradle.

Para compilar el APK de debug:

./gradlew assembleDebug

El APK generado se encontrará en:

app/build/outputs/apk/debug/
🔥 Configuración de Firebase

La aplicación requiere un proyecto configurado en Firebase con:

Firebase Realtime Database
Firebase Analytics

Para utilizar otro proyecto de Firebase:

Crea un proyecto desde Firebase Console.
Registra una aplicación Android con el package:
com.example.lahcomprahv2
Descarga el archivo:
google-services.json
Colócalo dentro de:
app/google-services.json
Configura las reglas de Realtime Database según las necesidades del entorno.
📌 Características técnicas destacadas
Arquitectura basada en MVVM.
Repository Pattern para desacoplar Firebase de la capa de presentación.
Inyección de dependencias con Koin.
UI completamente desarrollada con Jetpack Compose.
Gestión de estado mediante StateFlow.
Conversión de callbacks de Firebase a Kotlin Flow mediante callbackFlow.
Operaciones asíncronas con Coroutines.
Sincronización en tiempo real mediante Firebase Realtime Database.
Instrumentación de eventos mediante Firebase Analytics.
Estados explícitos de carga, guardado y error.
Tests de interfaz con repositorio fake.
Confirmación antes de operaciones destructivas.
👨‍💻 Autor

Desarrollado por Rafael Río.

📄 Licencia

Proyecto desarrollado con fines personales y educativos.
