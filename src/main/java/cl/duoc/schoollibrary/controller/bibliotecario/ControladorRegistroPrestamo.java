package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.exception.DataAccessException;
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

    private final VentanaRegistroPrestamo vista;
    private final ControladorControlPrestamos controladorListaMaestra;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    public ControladorRegistroPrestamo(VentanaRegistroPrestamo vista,
                                       ControladorControlPrestamos controladorListaMaestra) {
        this.vista = vista;
        this.controladorListaMaestra = controladorListaMaestra;

        inicializarListeners();
        cargarCombosDesdeBD();
    }

    private void cargarCombosDesdeBD() {
        new Thread(() -> {
            try {
                List<Estudiante> estudiantes = estudianteDAO.read();

                List<Libro> librosConStock = libroDAO.read().stream()
                        .filter(l -> l.getStock() > 0)
                        .toList();

                javax.swing.SwingUtilities.invokeLater(() -> {
                    vista.poblarComboEstudiantes(estudiantes);
                    vista.poblarComboLibros(librosConStock);
                });

            } catch (DataAccessException ex) {
                javax.swing.SwingUtilities.invokeLater(() ->
                        vista.mostrarMensajeError("Fallo al inicializar asignación: " + ex.getMessage())
                );
            }
        }).start();
    }

    private void inicializarListeners() {
        vista.addGuardarListener(e -> procesarAsignacionPrestamo());
        vista.addVolverAtrasListener(e -> vista.cerrarVentana());

        vista.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (controladorListaMaestra != null) {
                    controladorListaMaestra.cargarPrestamosDesdeBD();
                }
            }
        });
    }

    private void procesarAsignacionPrestamo() {
        Estudiante estudianteSeleccionado = (Estudiante) vista.getComboEstudiantes().getSelectedItem();
        Libro libroSeleccionado = (Libro) vista.getComboLibros().getSelectedItem();

        if (estudianteSeleccionado == null || libroSeleccionado == null) {
            vista.mostrarMensajeError("Debe seleccionar un estudiante y un título obligatoriamente.");
            return;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = hoy.plusDays(7);

        Prestamo nuevoPrestamo = new Prestamo(0, estudianteSeleccionado, libroSeleccionado, hoy, vencimiento, false);

        new Thread(() -> {
            try {
                prestamoDAO.create(nuevoPrestamo);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    vista.mostrarMensajeConfirmacion("¡Préstamo asignado con éxito en la Base de Datos!");

                    vista.getComboLibros().removeItem(libroSeleccionado);

                    vista.cerrarVentana();
                });

            } catch (DataAccessException ex) {
                javax.swing.SwingUtilities.invokeLater(() ->
                        vista.mostrarMensajeError(ex.getMessage())
                );
            }
        }).start();
    }
}
