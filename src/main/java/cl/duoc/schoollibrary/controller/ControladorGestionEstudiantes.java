package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.view.VentanaGestionEstudiantes;
import cl.duoc.schoollibrary.view.VentanaRegistroEstudiante;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorGestionEstudiantes {

    private final VentanaGestionEstudiantes vista;
    private final EstudianteDAO estudianteDAO;

    private List<Estudiante> listaEstudiantesCache = new ArrayList<>();

    public ControladorGestionEstudiantes(VentanaGestionEstudiantes vista, EstudianteDAO estudianteDAO) {
        this.vista = vista;
        this.estudianteDAO = estudianteDAO;

        inicializarListeners();
        cargarEstudiantesDesdeBD();
    }

    private void inicializarListeners() {
        vista.addBusquedaDocumentListener(this::aplicarFiltrosMemoria);

        vista.addNuevoButtonListener(e -> abrirFormularioFlotante(null));

        vista.addEditarButtonListener(e -> {
            int fila = vista.getFilaSeleccionada();
            if (fila == -1) {
                vista.mostrarMensajeError("Por favor, seleccione un alumno de la tabla para modificar su perfil.");
                return;
            }
            Estudiante seleccionado = listaEstudiantesCache.get(fila);
            abrirFormularioFlotante(seleccionado);
        });

        vista.addEliminarButtonListener(e -> procesarBajaEstudiante());

        vista.addAtrasButtonListener(e -> vista.cerrarVentana());
    }

    public void cargarEstudiantesDesdeBD() {
        new Thread(() -> {
            try {
                listaEstudiantesCache = estudianteDAO.read();
                vista.actualizarTabla(listaEstudiantesCache);
            } catch (DataAccessException ex) {
                vista.mostrarMensajeError("Fallo al sincronizar alumnos: " + ex.getMessage());
            }
        }).start();
    }

    private void aplicarFiltrosMemoria() {
        String texto = vista.getTextoBusqueda().toLowerCase().trim();

        List<Estudiante> filtrados = listaEstudiantesCache.stream()
                .filter(e -> texto.isBlank() ||
                        e.getNombre().toLowerCase().contains(texto) ||
                        e.getRut().toLowerCase().contains(texto))
                .toList();

        vista.actualizarTabla(filtrados);
    }

    private void abrirFormularioFlotante(Estudiante estudianteAEditar) {
        SwingUtilities.invokeLater(() -> {
            VentanaRegistroEstudiante ventanaReg = new VentanaRegistroEstudiante();

            new ControladorRegistroEstudiante(ventanaReg, estudianteDAO, this, estudianteAEditar);

            if (estudianteAEditar != null) {
                ventanaReg.prellenarFormulario(
                        estudianteAEditar.getRut(),
                        estudianteAEditar.getNombre(),
                        estudianteAEditar.getCurso(),
                        estudianteAEditar.getCorreo()
                );
            }

            ventanaReg.setVisible(true);
        });
    }

    private void procesarBajaEstudiante() {
        Integer idEstudiante = vista.getIdEstudianteSeleccionado();
        if (idEstudiante == null) {
            vista.mostrarMensajeError("Debe seleccionar un estudiante de la tabla para darle de baja.");
            return;
        }

        if (!vista.confirmarEliminacion("estudiante seleccionado del sistema escolar")) {
            return;
        }

        new Thread(() -> {
            try {
                estudianteDAO.delete(idEstudiante);

                SwingUtilities.invokeLater(() -> {
                    vista.mostrarMensajeConfirmacion("El estudiante ha sido dado de baja exitosamente.");
                    cargarEstudiantesDesdeBD();
                });
            } catch (DataAccessException ex) {
                vista.mostrarMensajeError("Error al eliminar registro: " + ex.getMessage());
            }
        }).start();
    }
}
