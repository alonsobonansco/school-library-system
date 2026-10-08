package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.view.VentanaRegistroEstudiante;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorRegistroEstudiante {

    private final VentanaRegistroEstudiante vista;
    private final EstudianteDAO estudianteDAO;
    private final Estudiante estudianteAEditar;
    private final ControladorGestionEstudiantes controladorLista;

    public ControladorRegistroEstudiante(VentanaRegistroEstudiante vista, EstudianteDAO estudianteDAO,
                                         ControladorGestionEstudiantes controladorLista, Estudiante estudianteAEditar) {
        this.vista = vista;
        this.estudianteDAO = estudianteDAO;
        this.controladorLista = controladorLista;
        this.estudianteAEditar = estudianteAEditar;

        inicializarListeners();
    }

    private void inicializarListeners() {
        vista.addGuardarListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarGuardadoEstudiante();
            }
        });

        vista.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (controladorLista != null) {
                    controladorLista.cargarEstudiantesDesdeBD();
                }
            }
        });
    }

    private void procesarGuardadoEstudiante() {
        String rut = vista.getRutInput();
        String nombre = vista.getNombreInput();
        String curso = vista.getCursoInput();
        String correo = vista.getCorreoInput();

        if (rut.isBlank() || nombre.isBlank() || curso.isBlank() || correo.isBlank()) {
            vista.mostrarMensajeError("Todos los campos del formulario son obligatorios.");
            return;
        }

        new Thread(() -> {
            try {
                if (estudianteAEditar == null) {
                    Estudiante nuevoEstudiante = new Estudiante(0, nombre, rut, curso, correo);
                    estudianteDAO.create(nuevoEstudiante);

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        vista.mostrarMensajeConfirmacion("Estudiante registrado exitosamente.");
                        vista.cerrarVentana();
                    });
                } else {
                    estudianteAEditar.setRut(rut);
                    estudianteAEditar.setNombre(nombre);
                    estudianteAEditar.setCurso(curso);
                    estudianteAEditar.setCorreo(correo);

                    estudianteDAO.update(estudianteAEditar);

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        vista.mostrarMensajeConfirmacion("Perfil de estudiante actualizado correctamente.");
                        vista.cerrarVentana();
                    });
                }
            } catch (DataAccessException ex) {
                javax.swing.SwingUtilities.invokeLater(() ->
                        vista.mostrarMensajeError("Error al persistir el estudiante: " + ex.getMessage())
                );
            }
        }).start();
    }
}
