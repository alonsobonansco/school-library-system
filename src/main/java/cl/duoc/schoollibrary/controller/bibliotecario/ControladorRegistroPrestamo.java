package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.dao.PrestamoDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroPrestamo;

import java.time.LocalDate;
import java.util.List;

public class ControladorRegistroPrestamo {

    private final VentanaRegistroPrestamo ventanaRegistroPrestamo;
    private final ControladorControlPrestamos controladorControlPrestamos;
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    public ControladorRegistroPrestamo(VentanaRegistroPrestamo ventanaRegistroPrestamo,
                                       ControladorControlPrestamos controladorControlPrestamos) {
        this.ventanaRegistroPrestamo = ventanaRegistroPrestamo;
        this.controladorControlPrestamos = controladorControlPrestamos;

        inicializarListeners();
        cargarCombosDesdeBD();
    }

    private void inicializarListeners() {
        ventanaRegistroPrestamo.addGuardarListener(e -> procesarAsignacionPrestamo());
        ventanaRegistroPrestamo.addVolverAtrasListener(e -> ventanaRegistroPrestamo.cerrarVentana());
        ventanaRegistroPrestamo.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (controladorControlPrestamos != null) {
                    controladorControlPrestamos.cargarPrestamosDesdeBD();
                }
            }
        });
    }

    private void cargarCombosDesdeBD() {
        AppThreadPool.ejecutarCarga(
                estudianteDAO::read,
                estudiantes -> {
                    ventanaRegistroPrestamo.poblarEstudiantes(estudiantes);

                    AppThreadPool.ejecutarCarga(
                            libroDAO::read,
                            libros -> {
                                List<Libro> librosConStock = libros.stream()
                                        .filter(l -> l.getStock() > 0)
                                        .toList();
                                ventanaRegistroPrestamo.poblarLibros(librosConStock);
                            },
                            ex -> ventanaRegistroPrestamo.mostrarMensajeError(
                                    "Fallo al cargar libros: " + ex.getMessage()
                            )
                    );
                },
                ex -> ventanaRegistroPrestamo.mostrarMensajeError(
                        "Fallo al cargar estudiantes: " + ex.getMessage()
                )
        );
    }

    private void procesarAsignacionPrestamo() {
        Estudiante estudianteSeleccionado = (Estudiante) ventanaRegistroPrestamo.getEstudiantesCombo().getSelectedItem();
        Libro libroSeleccionado = (Libro) ventanaRegistroPrestamo.getLibrosCombo().getSelectedItem();

        if (estudianteSeleccionado == null || libroSeleccionado == null) {
            ventanaRegistroPrestamo.mostrarMensajeError("Debe seleccionar un estudiante y un título obligatoriamente.");
            return;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = hoy.plusDays(7);

        Prestamo nuevoPrestamo
                = new Prestamo(0, estudianteSeleccionado, libroSeleccionado, hoy, vencimiento, false);

        AppThreadPool.ejecutarAccion(
                () -> prestamoDAO.create(nuevoPrestamo),
                () -> {
                    ventanaRegistroPrestamo.mostrarMensajeConfirmacion("Préstamo asignado con éxito en la Base de Datos");
                    ventanaRegistroPrestamo.getLibrosCombo().removeItem(libroSeleccionado);
                    ventanaRegistroPrestamo.cerrarVentana();
                },
                ex -> ventanaRegistroPrestamo.mostrarMensajeError(ex.getMessage())
        );
    }
}
