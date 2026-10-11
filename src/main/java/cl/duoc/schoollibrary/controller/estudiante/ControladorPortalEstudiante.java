package cl.duoc.schoollibrary.controller.estudiante;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.controller.CoordinadorNavegacion;
import cl.duoc.schoollibrary.model.dao.CategoriaDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.dao.PrestamoDAO;
import cl.duoc.schoollibrary.model.entity.Categoria;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.estudiante.VentanaPortalEstudiante;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador centralizado y unificado para el portal de autoservicio del estudiante.
 * Gestiona los estados de sesión del alumno, búsquedas filtradas en memoria reactiva
 * y la canalización asíncrona de solicitudes de retiro y devoluciones físicas.
 */
public class ControladorPortalEstudiante {

    private final VentanaPortalEstudiante ventanaPortalEstudiante;
    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;
    private final PrestamoDAO prestamoDAO;
    private final Estudiante alumnoLogueado;
    private List<Libro> listaLibrosEnMemoria = new ArrayList<>();
    private List<Prestamo> listaPrestamosEnMemoria = new ArrayList<>();

    public ControladorPortalEstudiante(VentanaPortalEstudiante ventanaPortalEstudiante, Estudiante alumnoLogueado) {
        this.ventanaPortalEstudiante = ventanaPortalEstudiante;
        this.alumnoLogueado = alumnoLogueado;
        this.libroDAO = new LibroDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.prestamoDAO = new PrestamoDAO();

        inicializarListeners();
        cargarCategoriasDesdeBD();
        cargarLibrosDesdeBD();
        cargarPrestamosActivos();
        this.ventanaPortalEstudiante.establecerNombreUsuario(alumnoLogueado.getNombre());
    }

    private void inicializarListeners() {
        ventanaPortalEstudiante.addBusquedaDocumentListener(this::aplicarFiltrosCatalogo);
        ventanaPortalEstudiante.addCategoriaComboListener(e -> aplicarFiltrosCatalogo());
        ventanaPortalEstudiante.addSolicitarPrestamoListener(e -> procesarSolicitudPrestamo());
        ventanaPortalEstudiante.addDevolverLibroListener(e -> procesarDevolucionLibro());
        ventanaPortalEstudiante.addCerrarSesionListener(e -> procesarCerrarSesion());
    }

    private void cargarCategoriasDesdeBD() {
        AppThreadPool.ejecutarCarga(
                categoriaDAO::read,
                categorias -> {
                    List<String> nombres = categorias.stream().map(Categoria::getNombre).toList();
                    ventanaPortalEstudiante.poblarCategorias(nombres);
                },
                ex -> ventanaPortalEstudiante.mostrarMensajeError(
                        "Error al iniciar filtros de categorías: " + ex.getMessage())
        );
    }

    private void cargarLibrosDesdeBD() {
        AppThreadPool.ejecutarCarga(
                libroDAO::read,
                libros -> {
                    listaLibrosEnMemoria = libros;
                    ventanaPortalEstudiante.actualizarTablaLibros(libros);
                },
                ex -> ventanaPortalEstudiante.mostrarMensajeError(
                        "Error al cargar catálogo: " + ex.getMessage())
        );
    }

    private void cargarPrestamosActivos() {
        AppThreadPool.ejecutarCarga(
                () -> {
                    List<Prestamo> todosLosPrestamos = prestamoDAO.read();
                    return todosLosPrestamos.stream()
                            .filter(p -> p.getEstudiante().getId() == alumnoLogueado.getId() && !p.isDevuelto())
                            .toList();
                },
                prestamos -> {
                    this.listaPrestamosEnMemoria = prestamos;
                    ventanaPortalEstudiante.actualizarTablaPrestamos(prestamos);
                },
                ex -> ventanaPortalEstudiante.mostrarMensajeError(
                        "Error al cargar los préstamos activos: " + ex.getMessage())
        );
    }

    private void aplicarFiltrosCatalogo() {
        String textoBuscado = ventanaPortalEstudiante.getTextoBusqueda().toLowerCase().trim();
        String categoriaSeleccionada = ventanaPortalEstudiante.getCategoriaSeleccionada();

        if (categoriaSeleccionada == null) return;

        List<Libro> librosFiltrados = listaLibrosEnMemoria.stream()
                .filter(l -> textoBuscado.isBlank() ||
                        l.getTitulo().toLowerCase().contains(textoBuscado) ||
                        l.getIsbn().contains(textoBuscado))
                .filter(l -> categoriaSeleccionada.equals("Todas") ||
                        l.getCategoria().getNombre().equalsIgnoreCase(categoriaSeleccionada))
                .toList();

        ventanaPortalEstudiante.actualizarTablaLibros(librosFiltrados);
    }

    /**
     * Procesa de forma asíncrona la reserva de un libro del catálogo.
     * Evalúa límites de stock en memoria RAM y delega al hilo obrero la ejecución del préstamo.
     * Calcula los plazos de vencimiento basándose de forma inmutable en el reloj del sistema (`LocalDate`).
     */
    private void procesarSolicitudPrestamo() {
        int filaSeleccionada = ventanaPortalEstudiante.getFilaSeleccionadaLibro();
        if (filaSeleccionada == -1) {
            ventanaPortalEstudiante.mostrarMensajeError("Debe seleccionar un libro del catálogo para solicitarlo.");
            return;
        }

        Libro libroSeleccionado = listaLibrosEnMemoria.get(filaSeleccionada);

        if (libroSeleccionado.getStock() <= 0) {
            ventanaPortalEstudiante.mostrarMensajeError(
                    "Lo sentimos, el libro '" + libroSeleccionado.getTitulo() + "' se encuentra agotado.");
            return;
        }

        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalDate vencimiento = hoy.plusDays(7);

        Prestamo nuevoPrestamo = new Prestamo(alumnoLogueado, libroSeleccionado, hoy, vencimiento, false);

        AppThreadPool.ejecutarAccion(
                () -> prestamoDAO.create(nuevoPrestamo),
                () -> {
                    ventanaPortalEstudiante.mostrarMensajeConfirmacion(
                            "Préstamo registrado\nTienes hasta el " + vencimiento + " para devolverlo.");
                    cargarLibrosDesdeBD();
                    cargarPrestamosActivos();
                },
                (Exception ex) -> ventanaPortalEstudiante.mostrarMensajeError(ex.getMessage())
        );
    }

    /**
     * Procesa el retorno físico e inventariado de un libro seleccionado.
     * Aplica el freno de mano mecánico en la vista, despacha la consulta mutadora al pool secundario
     * y utiliza SwingUtilities de forma indirecta para refrescar las tablas sin congelar la pantalla .
     */
    private void procesarDevolucionLibro() {
        int filaSeleccionada = ventanaPortalEstudiante.getFilaSeleccionadaPrestamo();
        if (filaSeleccionada == -1) {
            ventanaPortalEstudiante.mostrarMensajeError(
                    "Debe seleccionar un préstamo activo de la tabla para marcar su devolución.");
            return;
        }

        if (!ventanaPortalEstudiante.confirmarDevolucion()) {
            return;
        }

        Prestamo prestamoAEditar = listaPrestamosEnMemoria.get(filaSeleccionada);

        int idPrestamo = prestamoAEditar.getId();
        int idLibro = prestamoAEditar.getLibro().getId();

        ventanaPortalEstudiante.setFormularioHabilitado(false);

        AppThreadPool.ejecutarAccion(
                () -> prestamoDAO.registrarDevolucion(idPrestamo, idLibro),
                () -> {
                    ventanaPortalEstudiante.mostrarMensajeConfirmacion(
                            "El libro ha sido devuelto al inventario de la biblioteca con éxito.");
                    cargarLibrosDesdeBD();
                    cargarPrestamosActivos();
                    ventanaPortalEstudiante.setFormularioHabilitado(true);
                },
                ex -> {
                    ventanaPortalEstudiante.mostrarMensajeError(
                            "Fallo al procesar devolución: " + ex.getMessage());
                    ventanaPortalEstudiante.setFormularioHabilitado(true);
                }
        );
    }

    private void procesarCerrarSesion() {
        CoordinadorNavegacion.getInstancia().mostrarLogin();
    }
}
