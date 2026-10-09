package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.dao.PrestamoDAO;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.VentanaControlPrestamos;
import cl.duoc.schoollibrary.view.VentanaRegistroPrestamo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorControlPrestamos {

    private final VentanaControlPrestamos vista;
    private final PrestamoDAO prestamoDAO;

    private List<Prestamo> listaPrestamosCache = new ArrayList<>();

    public ControladorControlPrestamos(VentanaControlPrestamos vista) {
        this.vista = vista;
        this.prestamoDAO = new PrestamoDAO();

        inicializarListeners();
        cargarPrestamosDesdeBD();
    }

    private void inicializarListeners() {
        vista.addNuevoPrestamoListener(e -> abrirFormularioAsignacion());
        vista.addDevolucionListener(e -> procesarDevolucionAdministrativa());
        vista.addAtrasListener(e -> vista.cerrarVentana());
    }

    public void cargarPrestamosDesdeBD() {
        new Thread(() -> {
            try {
                listaPrestamosCache = prestamoDAO.read();
                vista.actualizarTabla(listaPrestamosCache);
            } catch (DataAccessException ex) {
                vista.mostrarMensajeError("Fallo al sincronizar historial: " + ex.getMessage());
            }
        }).start();
    }

    private void abrirFormularioAsignacion() {
        SwingUtilities.invokeLater(() -> {
            VentanaRegistroPrestamo ventanaReg = new VentanaRegistroPrestamo();

            new ControladorRegistroPrestamo(ventanaReg, this);

            ventanaReg.setVisible(true);
        });
    }

    private void procesarDevolucionAdministrativa() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (filaSeleccionada == -1) {
            vista.mostrarMensajeError("Por favor, seleccione un préstamo de la tabla para registrar su devolución.");
            return;
        }

        Prestamo prestamoAEditar = listaPrestamosCache.get(filaSeleccionada);

        if (prestamoAEditar.isDevuelto()) {
            vista.mostrarMensajeError("El préstamo seleccionado ya figura como DEVUELTO en el sistema.");
            return;
        }

        if (!vista.confirmarEliminacion("préstamo y retornar la copia al stock físico disponible")) {
            return;
        }

        int idPrestamo = prestamoAEditar.getId();
        int idLibro = prestamoAEditar.getLibro().getId();

        new Thread(() -> {
            try {
                prestamoDAO.registrarDevolucion(idPrestamo, idLibro);

                SwingUtilities.invokeLater(() -> {
                    vista.mostrarMensajeConfirmacion("Devolución procesada con éxito.\nEl libro ha retornado al estante.");
                    cargarPrestamosDesdeBD();
                });

            } catch (DataAccessException ex) {
                vista.mostrarMensajeError("Fallo relacional al procesar devolución: " + ex.getMessage());
            }
        }).start();
    }
}
