package cl.duoc.schoollibrary.controller.estudiante;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.controller.CoordinadorNavegacion;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.dao.PrestamoDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.estudiante.VentanaPortalEstudiante;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador centralizado y unificado para el portal de autoservicio del estudiante.
 * Gestiona de forma síncrona el estado de la sesión, consultas en caché y transacciones.
 */
public class ControladorPortalEstudiante {

    private final VentanaPortalEstudiante ventanaPortalEstudiante;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;
    private final Estudiante alumnoLogueado;
    private List<Libro> listaLibrosCache = new ArrayList<>();
    private List<Prestamo> listaPrestamosCache = new ArrayList<>();

    public ControladorPortalEstudiante(VentanaPortalEstudiante ventanaPortalEstudiante, Estudiante alumnoLogueado) {
        this.ventanaPortalEstudiante = ventanaPortalEstudiante;
        this.alumnoLogueado = alumnoLogueado;
        this.libroDAO = new LibroDAO();
        this.prestamoDAO = new PrestamoDAO();

        inicializarListeners();
        cargarCatalogoLibros();
        cargarPrestamosActivos();
    }

    private void inicializarListeners() {
        ventanaPortalEstudiante.addBusquedaDocumentListener(this::aplicarFiltrosCatalogo);
        ventanaPortalEstudiante.addCategoriaComboListener(e -> aplicarFiltrosCatalogo());
        ventanaPortalEstudiante.addSolicitarButtonListener(e -> procesarSolicitudPrestamo());
        ventanaPortalEstudiante.addDevolverButtonListener(e -> procesarDevolucionLibro());
        ventanaPortalEstudiante.addCerrarSesionButtonListener(e -> procesarCerrarSesion());
    }

    public void cargarCatalogoLibros() {
        AppThreadPool.ejecutarCarga(
                libroDAO::read,
                libros -> {
                    listaLibrosCache = libros;
                    ventanaPortalEstudiante.actualizarTablaLibros(libros);
                },
                ex -> ventanaPortalEstudiante.mostrarMensajeError(
                        "Error al cargar catálogo: " + ex.getMessage())
        );
    }

    public void cargarPrestamosActivos() {
        AppThreadPool.ejecutarCarga(
                () -> {
                    List<Prestamo> todosLosPrestamos = prestamoDAO.read();
                    return todosLosPrestamos.stream()
                            .filter(p -> p.getEstudiante().getId() == alumnoLogueado.getId() && !p.isDevuelto())
                            .toList();
                },
                prestamos -> {
                    this.listaPrestamosCache = prestamos;
                    ventanaPortalEstudiante.actualizarTablaPrestamos(prestamos);
                },
                ex -> ventanaPortalEstudiante.mostrarMensajeError(
                        "Error al cargar tus préstamos: " + ex.getMessage())
        );
    }

    private void aplicarFiltrosCatalogo() {
        String textoBuscado = ventanaPortalEstudiante.getTextoBusqueda().toLowerCase().trim();
        String categoriaSeleccionada = ventanaPortalEstudiante.getCategoriaSeleccionada();

        if (categoriaSeleccionada == null) return;

        List<Libro> librosFiltrados = listaLibrosCache.stream()
                .filter(l -> textoBuscado.isBlank() ||
                        l.getTitulo().toLowerCase().contains(textoBuscado) ||
                        l.getIsbn().contains(textoBuscado))
                .filter(l -> categoriaSeleccionada.equals("Todas") ||
                        l.getCategoria().getNombre().equalsIgnoreCase(categoriaSeleccionada))
                .toList();

        ventanaPortalEstudiante.actualizarTablaLibros(librosFiltrados);
    }

    private void procesarSolicitudPrestamo() {
        int filaSeleccionada = ventanaPortalEstudiante.getFilaSeleccionadaLibro();
        if (filaSeleccionada == -1) {
            ventanaPortalEstudiante.mostrarMensajeError("Por favor, seleccione un libro del catálogo para solicitarlo.");
            return;
        }

        Libro libroSeleccionado = listaLibrosCache.get(filaSeleccionada);

        if (libroSeleccionado.getStock() <= 0) {
            ventanaPortalEstudiante.mostrarMensajeError("Lo sentimos, el libro '" + libroSeleccionado.getTitulo() + "' se encuentra agotado.");
            return;
        }

        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalDate vencimiento = hoy.plusDays(7);

        Prestamo nuevoPrestamo = new Prestamo(alumnoLogueado, libroSeleccionado, hoy, vencimiento, false);

        AppThreadPool.ejecutarAccion(
                () -> prestamoDAO.create(nuevoPrestamo),
                () -> {
                    ventanaPortalEstudiante.mostrarMensajeConfirmacion(
                            "¡Préstamo registrado!\nTienes hasta el " + vencimiento + " para devolverlo.");
                    cargarCatalogoLibros();
                    cargarPrestamosActivos();
                },
                (Exception ex) -> ventanaPortalEstudiante.mostrarMensajeError(ex.getMessage())
        );
    }

    private void procesarDevolucionLibro() {
        int filaSeleccionada = ventanaPortalEstudiante.getFilaSeleccionadaPrestamo();
        if (filaSeleccionada == -1) {
            ventanaPortalEstudiante.mostrarMensajeError(
                    "Debe seleccionar un préstamo activo de la tabla para marcar su devolución.");
            return;
        }

        if (!ventanaPortalEstudiante.confirmarEliminacion("préstamo y retornar el libro al inventario físico")) {
            return;
        }

        Prestamo prestamoAEditar = listaPrestamosCache.get(filaSeleccionada);

        int idPrestamo = prestamoAEditar.getId();
        int idLibro = prestamoAEditar.getLibro().getId();

        AppThreadPool.ejecutarAccion(
                () -> prestamoDAO.registrarDevolucion(idPrestamo, idLibro),
                () -> {
                    ventanaPortalEstudiante.mostrarMensajeConfirmacion(
                            "El libro ha sido devuelto al inventario de la biblioteca con éxito.");
                    cargarCatalogoLibros();
                    cargarPrestamosActivos();
                },
                ex -> ventanaPortalEstudiante.mostrarMensajeError("Fallo al procesar devolución: " + ex.getMessage())
        );
    }

    private void procesarCerrarSesion() {
        CoordinadorNavegacion.getInstancia().mostrarLogin();
    }
}
