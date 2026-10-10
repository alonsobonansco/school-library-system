package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroEstudiante;

public class ControladorRegistroEstudiante {

    private final VentanaRegistroEstudiante ventanaRegistroEstudiante;
    private final EstudianteDAO estudianteDAO;
    private final Estudiante estudianteAEditar;
    private final ControladorGestionEstudiantes controladorGestionEstudiantes;

    public ControladorRegistroEstudiante(VentanaRegistroEstudiante ventanaRegistroEstudiante, EstudianteDAO estudianteDAO,
                                         ControladorGestionEstudiantes controladorGestionEstudiantes,
                                         Estudiante estudianteAEditar) {
        this.ventanaRegistroEstudiante = ventanaRegistroEstudiante;
        this.estudianteDAO = estudianteDAO;
        this.controladorGestionEstudiantes = controladorGestionEstudiantes;
        this.estudianteAEditar = estudianteAEditar;

        inicializarListeners();
    }

    private void inicializarListeners() {
        ventanaRegistroEstudiante.addGuardarListener(e -> procesarGuardadoEstudiante());
        ventanaRegistroEstudiante.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (controladorGestionEstudiantes != null) {
                    controladorGestionEstudiantes.cargarEstudiantesDesdeBD();
                }
            }
        });
        ventanaRegistroEstudiante.addVolverAtrasListener(e -> ventanaRegistroEstudiante.cerrarVentana());
    }

    private void procesarGuardadoEstudiante() {
        String rut = ventanaRegistroEstudiante.getRutInput();
        String nombre = ventanaRegistroEstudiante.getNombreInput();
        String curso = ventanaRegistroEstudiante.getCursoInput();
        String correo = ventanaRegistroEstudiante.getCorreoInput();

        if (rut.isBlank() || nombre.isBlank() || curso.isBlank() || correo.isBlank()) {
            ventanaRegistroEstudiante.mostrarMensajeError("Todos los campos del formulario son obligatorios.");
            return;
        }

        ventanaRegistroEstudiante.setFormularioHabilitado(false);

        AppThreadPool.ejecutarAccion(
                () -> {
                    if (estudianteAEditar == null) {
                        Estudiante nuevoEstudiante = new Estudiante(0, nombre, rut, curso, correo);
                        estudianteDAO.create(nuevoEstudiante);
                    } else {
                        estudianteAEditar.setRut(rut);
                        estudianteAEditar.setNombre(nombre);
                        estudianteAEditar.setCurso(curso);
                        estudianteAEditar.setCorreo(correo);
                        estudianteDAO.update(estudianteAEditar);
                    }
                },
                () -> {
                    String mensaje = (estudianteAEditar == null) ? "Estudiante registrado exitosamente." :
                            "Perfil de estudiante actualizado correctamente.";
                    ventanaRegistroEstudiante.mostrarMensajeConfirmacion(mensaje);
                    ventanaRegistroEstudiante.cerrarVentana();
                },
                ex -> {
                    ventanaRegistroEstudiante.mostrarMensajeError("Error al persistir el estudiante: "
                            + ex.getMessage());
                    ventanaRegistroEstudiante.setFormularioHabilitado(true);
                }
        );
    }
}
