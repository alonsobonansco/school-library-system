package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.dao.PrestamoDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.VentanaPortalEstudiante;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador centralizado y unificado para el portal de autoservicio del estudiante.
 * Gestiona de forma síncrona el estado de la sesión, consultas en caché y transacciones.
 */
public class ControladorPortalEstudiante {

    private final VentanaPortalEstudiante vista;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;
    private final Estudiante alumnoLogueado;
    private List<Libro> listaLibrosCache = new ArrayList<>();
    private List<Prestamo> listaPrestamosCache = new ArrayList<>();

    public ControladorPortalEstudiante(VentanaPortalEstudiante vista, Estudiante alumnoLogueado) {
        this.vista = vista;
        this.alumnoLogueado = alumnoLogueado;
        this.libroDAO = new LibroDAO();
        this.prestamoDAO = new PrestamoDAO();

        inicializarListeners();
        cargarCatalogoLibros();
        cargarPrestamosActivos();
    }

    private void inicializarListeners() {
        vista.addBusquedaDocumentListener(this::aplicarFiltrosCatalogo);

        vista.addCategoriaComboListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aplicarFiltrosCatalogo();
            }
        });

        vista.addSolicitarButtonListener(e -> procesarSolicitudPrestamo());
        vista.addDevolverButtonListener(e -> procesarDevolucionLibro());
        vista.addCerrarSesionButtonListener(e -> procesarCerrarSesion());
    }

    public void cargarCatalogoLibros() {
        new Thread(() -> {
            try {
                listaLibrosCache = libroDAO.read();
                vista.actualizarTablaLibros(listaLibrosCache);
            } catch (DataAccessException ex) {
                vista.mostrarMensajeError("Error al cargar catálogo: " + ex.getMessage());
            }
        }).start();
    }

    public void cargarPrestamosActivos() {
        new Thread(() -> {
            try {
                List<Prestamo> todosLosPrestamos = prestamoDAO.read();

                listaPrestamosCache = todosLosPrestamos.stream()
                        .filter(p -> p.getEstudiante().getId() == alumnoLogueado.getId() && !p.isDevuelto())
                        .toList();

                vista.actualizarTablaPrestamos(listaPrestamosCache);
            } catch (DataAccessException ex) {
                vista.mostrarMensajeError("Error al cargar tus préstamos: " + ex.getMessage());
            }
        }).start();
    }

    private void aplicarFiltrosCatalogo() {
        String textoBuscado = vista.getTextoBusqueda().toLowerCase().trim();
        String categoriaSeleccionada = vista.getCategoriaSeleccionada();

        if (categoriaSeleccionada == null) return;

        List<Libro> librosFiltrados = listaLibrosCache.stream()
                .filter(l -> textoBuscado.isBlank() ||
                        l.getTitulo().toLowerCase().contains(textoBuscado) ||
                        l.getIsbn().contains(textoBuscado))
                .filter(l -> categoriaSeleccionada.equals("Todas") ||
                        l.getCategoria().getNombre().equalsIgnoreCase(categoriaSeleccionada))
                .toList();

        vista.actualizarTablaLibros(librosFiltrados);
    }

    private void procesarSolicitudPrestamo() {
        int filaSeleccionada = vista.getFilaSeleccionadaLibro();
        if (filaSeleccionada == -1) {
            vista.mostrarMensajeError("Por favor, seleccione un libro del catálogo para solicitarlo.");
            return;
        }

        Libro libroSeleccionado = listaLibrosCache.get(filaSeleccionada);

        if (libroSeleccionado.getStock() <= 0) {
            vista.mostrarMensajeError("Lo sentimos, el libro '" + libroSeleccionado.getTitulo() + "' se encuentra agotado.");
            return;
        }

        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalDate vencimiento = hoy.plusDays(7);

        Prestamo nuevoPrestamo = new Prestamo(alumnoLogueado, libroSeleccionado, hoy, vencimiento, false);

        new Thread(() -> {
            try {
                prestamoDAO.create(nuevoPrestamo);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    vista.mostrarMensajeConfirmacion("¡Préstamo registrado!\nTienes hasta el " + vencimiento + " para devolverlo.");

                    cargarCatalogoLibros();
                    cargarPrestamosActivos();
                });

            } catch (DataAccessException ex) {
                vista.mostrarMensajeError(ex.getMessage());
            }
        }).start();
    }

    private void procesarDevolucionLibro() {
        int filaSeleccionada = vista.getFilaSeleccionadaPrestamo();
        if (filaSeleccionada == -1) {
            vista.mostrarMensajeError("Debe seleccionar un préstamo activo de la tabla para marcar su devolución.");
            return;
        }

        if (!vista.confirmarEliminacion("préstamo y retornar el libro al inventario físico")) {
            return;
        }

        Prestamo prestamoAEditar = listaPrestamosCache.get(filaSeleccionada);

        int idPrestamo = prestamoAEditar.getId();
        int idLibro = prestamoAEditar.getLibro().getId();

        new Thread(() -> {
            try {
                prestamoDAO.registrarDevolucion(idPrestamo, idLibro);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    vista.mostrarMensajeConfirmacion("El libro ha sido devuelto al inventario de la biblioteca con éxito.");
                    cargarCatalogoLibros();
                    cargarPrestamosActivos();
                });

            } catch (DataAccessException ex) {
                vista.mostrarMensajeError("Fallo al procesar devolución: " + ex.getMessage());
            }
        }).start();
    }

    private void procesarCerrarSesion() {
        CoordinadorNavegacion.getInstancia().mostrarLogin();
    }
}
