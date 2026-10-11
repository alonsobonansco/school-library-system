package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroEstudiante;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Cerebro operativo encargado de coordinar la creación y actualización (CRUD)
 * de las fichas de estudiantes interactuando asíncronamente con la capa de datos.
 */
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
        ventanaRegistroEstudiante.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (controladorGestionEstudiantes != null) {
                    controladorGestionEstudiantes.cargarEstudiantesDesdeBD();
                }
            }
        });
        ventanaRegistroEstudiante.addVolverAtrasListener(e -> ventanaRegistroEstudiante.cerrarVentana());
    }

    /**
     * Extrae de forma segura las cadenas de texto del formulario y gestiona el flujo asíncrono de persistencia.
     * Evalúa la nulidad del objeto de referencia para conmutar de forma dinámica entre un comando de inserción (CREATE)
     * o una actualización relacional (UPDATE), aplicando un blindaje de controles mecánicos contra spams de clics dobles.
     */
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
