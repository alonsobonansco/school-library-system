package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.model.dao.PrestamoDAO;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaControlPrestamos;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroPrestamo;

import java.util.ArrayList;
import java.util.List;

public class ControladorControlPrestamos {

    private final VentanaControlPrestamos ventanaControlPrestamos;
    private final PrestamoDAO prestamoDAO;
    private List<Prestamo> listaPrestamosEnMemoria = new ArrayList<>();

    public ControladorControlPrestamos(VentanaControlPrestamos ventanaControlPrestamos) {
        this.ventanaControlPrestamos = ventanaControlPrestamos;
        this.prestamoDAO = new PrestamoDAO();

        inicializarListeners();
        cargarPrestamosDesdeBD();
    }

    private void inicializarListeners() {
        ventanaControlPrestamos.addRegistrarPrestamoListener(e -> abrirFormularioAsignacion());
        ventanaControlPrestamos.addRegistrarDevolucionListener(e -> procesarDevolucion());
        ventanaControlPrestamos.addVolverAtrasListener(e -> ventanaControlPrestamos.cerrarVentana());
    }

    public void cargarPrestamosDesdeBD() {
        AppThreadPool.ejecutarCarga(
                prestamoDAO::read,
                prestamos -> {
                    this.listaPrestamosEnMemoria = prestamos;
                    ventanaControlPrestamos.actualizarTabla(prestamos);
                },
                ex -> ventanaControlPrestamos.mostrarMensajeError(
                        "Fallo al sincronizar historial: " + ex.getMessage()
                ));
    }

    private void abrirFormularioAsignacion() {
        VentanaRegistroPrestamo ventanaReg = new VentanaRegistroPrestamo();
        new ControladorRegistroPrestamo(ventanaReg, this);
        ventanaReg.setVisible(true);
    }

    private void procesarDevolucion() {
        int filaSeleccionada = ventanaControlPrestamos.getFilaSeleccionada();
        if (filaSeleccionada == -1) {
            ventanaControlPrestamos.mostrarMensajeError("Debe seleccionar un préstamo de la tabla para registrar su devolución.");
            return;
        }

        Prestamo prestamoAEditar = listaPrestamosEnMemoria.get(filaSeleccionada);

        if (prestamoAEditar.isDevuelto()) {
            ventanaControlPrestamos.mostrarMensajeError("El préstamo seleccionado ya figura como DEVUELTO en el sistema.");
            return;
        }

        if (!ventanaControlPrestamos.confirmarEliminacion("préstamo y retornar la copia al stock físico disponible")) {
            return;
        }

        int idPrestamo = prestamoAEditar.getId();
        int idLibro = prestamoAEditar.getLibro().getId();

        AppThreadPool.ejecutarAccion(
                () -> prestamoDAO.registrarDevolucion(idPrestamo, idLibro),
                () -> {
                    ventanaControlPrestamos.mostrarMensajeConfirmacion(
                            "Devolución procesada con éxito.\nEl libro ha retornado al estante.");
                    cargarPrestamosDesdeBD();
                },
                ex -> ventanaControlPrestamos.mostrarMensajeError(
                        "Fallo relacional al procesar devolución: " + ex.getMessage())
        );
    }
}
