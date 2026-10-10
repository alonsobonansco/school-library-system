package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.model.dao.PrestamoDAO;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaGestionPrestamos;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroPrestamo;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class ControladorGestionPrestamos {

    private final VentanaGestionPrestamos ventanaGestionPrestamos;
    private final PrestamoDAO prestamoDAO;
    private List<Prestamo> listaPrestamosEnMemoria = new ArrayList<>();

    public ControladorGestionPrestamos(VentanaGestionPrestamos ventanaGestionPrestamos) {
        this.ventanaGestionPrestamos = ventanaGestionPrestamos;
        this.prestamoDAO = new PrestamoDAO();

        inicializarListeners();
        cargarPrestamosDesdeBD();
    }

    private void inicializarListeners() {
        ventanaGestionPrestamos.addRegistrarPrestamoListener(e -> {
            ventanaGestionPrestamos.setAccionesHabilitadas(false);
            abrirFormularioAsignacion();
        });
        ventanaGestionPrestamos.addRegistrarDevolucionListener(e -> procesarDevolucion());
        ventanaGestionPrestamos.addVolverAtrasListener(e -> ventanaGestionPrestamos.cerrarVentana());
    }

    public void cargarPrestamosDesdeBD() {
        AppThreadPool.ejecutarCarga(
                prestamoDAO::read,
                prestamos -> {
                    this.listaPrestamosEnMemoria = prestamos;
                    ventanaGestionPrestamos.actualizarTabla(prestamos);
                },
                ex -> ventanaGestionPrestamos.mostrarMensajeError(
                        "Fallo al sincronizar historial: " + ex.getMessage()
                ));
    }

    private void abrirFormularioAsignacion() {
        VentanaRegistroPrestamo ventanaRegistroPrestamo = new VentanaRegistroPrestamo();
        ventanaRegistroPrestamo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaGestionPrestamos.setAccionesHabilitadas(true);
            }
        });
        new ControladorRegistroPrestamo(ventanaRegistroPrestamo, this);
        ventanaRegistroPrestamo.setVisible(true);
    }

    private void procesarDevolucion() {
        int filaSeleccionada = ventanaGestionPrestamos.getFilaSeleccionada();
        if (filaSeleccionada == -1) {
            ventanaGestionPrestamos.mostrarMensajeError("Debe seleccionar un préstamo de la tabla para registrar su devolución.");
            return;
        }

        Prestamo prestamoAEditar = listaPrestamosEnMemoria.get(filaSeleccionada);

        if (prestamoAEditar.isDevuelto()) {
            ventanaGestionPrestamos.mostrarMensajeError("El préstamo seleccionado ya figura como DEVUELTO en el sistema.");
            return;
        }

        if (!ventanaGestionPrestamos.confirmarDevolucion()) {
            return;
        }

        int idPrestamo = prestamoAEditar.getId();
        int idLibro = prestamoAEditar.getLibro().getId();

        ventanaGestionPrestamos.setAccionesHabilitadas(false);

        AppThreadPool.ejecutarAccion(
                () -> prestamoDAO.registrarDevolucion(idPrestamo, idLibro),
                () -> {
                    ventanaGestionPrestamos.mostrarMensajeConfirmacion(
                            "Devolución procesada con éxito.\nEl libro ha retornado al estante.");
                    cargarPrestamosDesdeBD();
                    ventanaGestionPrestamos.setAccionesHabilitadas(true);
                },
                ex -> {
                    ventanaGestionPrestamos.mostrarMensajeError(
                            "Fallo relacional al procesar devolución: " + ex.getMessage());
                    ventanaGestionPrestamos.setAccionesHabilitadas(true);
                }
        );
    }
}
