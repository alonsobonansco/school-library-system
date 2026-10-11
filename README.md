# 📚 SchoolLibrary System

Sistema de Gestión de Biblioteca Escolar

---

## 📖 Descripción

SchoolLibrary es una aplicación de escritorio desarrollada en **Java y Maven** diseñada para automatizar el flujo de préstamos, devoluciones e inteligencia analítica de una institución educativa.

El sistema rompe con los esquemas tradicionales de escritorio al implementar una **Arquitectura Multihilo Asíncrona (Asynchronous Thread Pool Engine)** desacoplada del hilo gráfico y un **Motor Transaccional Centralizado con Bloqueo por Monitor (Mutex)**, garantizando una interfaz 100% fluida (libre de congelamientos) y una consistencia absoluta de datos en entornos de alta concurrencia relacional sobre MySQL.

---

## 🏗️ Arquitectura del Sistema (MVC Decoupled)

El proyecto organiza sus componentes bajo un esquema de **Alta Cohesión y Bajo Acoplamiento**:

| Capa / Paquete | Responsabilidad Estructural | Mecanismo Clave |
| :--- | :--- | :--- |
| **`cl.duoc.schoollibrary.view.*`** | Renderizado gráfico pasivo. Hereda de `VentanaBase` para unificar tokens estéticos (fuentes, cursores) y modales informativos. | `Java Swing`, `GridBagLayout` |
| **`cl.duoc.schoollibrary.controller.*`** | Orquestadores de negocio. Capturan eventos de la UI, congelan controles mecánicamente y delegan tareas al pool asíncrono. | `AppThreadPool`, `CoordinadorNavegacion` |
| **`cl.duoc.schoollibrary.model.entity`** | Modelo de dominio rico. Encapsula esquemas (POJOs) auto-defensivos que invocan reglas invariantes en sus constructores y setters. | `JavaBeans contratos` |
| **`cl.duoc.schoollibrary.util`** | Clases utilitarias inmutables puras. Actúan como la aduana de seguridad del sistema para sanitizar textos, emails y algoritmos de RUT. | `Validator`, `RutValidator` |
| **`cl.duoc.schoollibrary.model.dao`** | Capa de persistencia aislada. Abstrae sentencias SQL puras y delega su ejecución física a través de contratos funcionales. | `JDBC`, `PreparedStatement` |
| **`cl.duoc.schoollibrary.database`** | Motor de infraestructura. Provee el socket único relacional y el cortafuegos de sincronización atómica. | `Singleton`, `Functional Interfaces` |
| **`cl.duoc.schoollibrary.exception`** | Cortafuegos de excepciones de negocio. Evita la fuga de dependencias relacionales hacia los controladores superiores. | `RestriccionIntegridadException` |

---

## 🚀 Pilares Tecnológicos y Gestión de Concurrencia

La ingeniería del proyecto resuelve los desafíos de la programación concurrente y la persistencia de datos bajo tres ejes estrictos:

### 1. Desacoplamiento Asíncrono de Interfaz (`AppThreadPool` + EDT)
Para cumplir con la pauta de **evitar bloqueos de interfaz**, el hilo pintor gráfico nativo de Swing (**EDT**) tiene prohibido comunicarse con la base de datos.
* El sistema delega todas las consultas de carga y acciones de escritura a **`AppThreadPool`**, un entorno administrado de tamaño fijo limitado a **2 hilos obreros permanentes en RAM** (`pool-1-thread-1` y `pool-1-thread-2`).
* Las respuestas, popups y actualizaciones de tablas regresan al hilo gráfico empaquetadas exclusivamente en **`SwingUtilities.invokeLater()`**, garantizando la estabilidad visual (*Thread-Safety*) y una experiencia de usuario (UX) fluida y reactiva.

### 2. Consistencia Transaccional y Bloqueo de Monitor (`DatabaseConnection`)
Colocar el modificador `synchronized` en métodos de los DAO genera una falsa sensación de seguridad, ya que al instanciarse dinámicamente (`new LibroDAO()`) los hilos bloquean objetos locales distintos en la RAM, chocando al tocar la red.
* Se centraliza el firewall en el Singleton **`DatabaseConnection`** mediante un bloque **`synchronized(lock)`** que actúa como un **Monitor de Exclusión Mutua (Mutex)** de capacidad estricta de 1 hilo a la vez.
* Los métodos avanzados como `PrestamoDAO.create` ejecutan **Transacciones Atómicas ACID multi-tabla** (verifican y descuentan stock en `libros` e insertan la fila en `prestamos`). Si el stock se agota en paralelo, la transacción aborta y ejecuta un `rollback()` inmediato, impidiendo la corrupción o fuga de inventario físico.

### 3. Mecanismo de Control Fail-Fast e Invariantes de Dominio
* **Validación Invariante:** Las entidades de negocio rechazan estados corruptos en tiempo de ejecución. Los constructores interceptan y sanitizan RUTs (vía algoritmo Módulo 11 inmutable en `RutValidator`), correos y nulidades antes de alojar el objeto en la RAM.
* **Frenos de Mano Mecánicos (Throttling):** Al disparar una ruta de navegación o una mutación destructiva, el controlador congela inmediatamente los botones de la vista (`setAccionesHabilitadas(false)`). Al acoplar un `WindowListener` al ciclo de vida de los formularios flotantes, la vista se libera de forma simétrica únicamente cuando la tarea en segundo plano finaliza con éxito o error, destruyendo el riesgo de spams de clics e inflación de ventanas duplicadas.


---

## 🛠️ Instrucciones para clonar y ejecutar

### Requisitos del sistema:

* **JDK:** Java 25 (LTS) o superior
* **IDE Recomendado:** IntelliJ IDEA

### Pasos:

1. Clonar el repositorio desde la terminal de la computadora o IDE:  
   git clone https://github.com/alonsobonansco/school-library-system.git
2. Ir a File →️ Open y seleccionar la carpeta raíz del proyecto (la carpeta que contiene el archivo pom.xml).
3. Ejecutar el `Main` desde su clase en el paquete raíz `cl.duoc.schoollibrary` *(La aplicación se abrirá, pero arrojará un
   aviso de que faltan las credenciales en el sistema, lo cual es normal).*
4. En la barra superior de IntelliJ, justo al lado del botón verde de "Play",
   hacer clic en la lista desplegable que ahora dice `Main` y seleccionar **Edit Configuration...**
5. Hacer clic en el ícono de la pequeña carpeta o en el símbolo de más (`+`) al
   final de ese campo e ingresar las dos variables de entorno con tus datos locales de MySQL:
    * **Name:** `DB_USER` | **Value:** `tu_usuario_root` *(Por ejemplo: root)*
    * **Name:** `DB_PASSWORD` | **Value:** `tu_contraseña_de_mysql`
6. Hacer clic en **Apply** y luego en **OK** para cerrar el panel de configuración de IntelliJ.
7. Volver a presionar el botón verde de **Play** en la barra superior. La aplicación **School Library System** se conectará de forma
   segura a tu base de datos y levantará el panel Login al instante.

---