package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.exception.RestriccionIntegridadException;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaGestionEstudiantes;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroEstudiante;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class ControladorGestionEstudiantes {

    private final VentanaGestionEstudiantes ventanaGestionEstudiantes;
    private final EstudianteDAO estudianteDAO;
    private List<Estudiante> listaEstudiantesEnMemoria = new ArrayList<>();

    public ControladorGestionEstudiantes(VentanaGestionEstudiantes ventanaGestionEstudiantes,
                                         EstudianteDAO estudianteDAO) {
        this.ventanaGestionEstudiantes = ventanaGestionEstudiantes;
        this.estudianteDAO = estudianteDAO;

        inicializarListeners();
        cargarEstudiantesDesdeBD();
    }

    private void inicializarListeners() {
        ventanaGestionEstudiantes.addBusquedaDocumentListener(this::aplicarFiltrosMemoria);
        ventanaGestionEstudiantes.addAgregarListener(e -> {
            ventanaGestionEstudiantes.setAccionesHabilitadas(false);
            abrirFormularioRegistro(null);
        });
        ventanaGestionEstudiantes.addEditarListener(e -> {
            int filaSeleccionada = ventanaGestionEstudiantes.getFilaSeleccionada();
            if (filaSeleccionada == -1) {
                ventanaGestionEstudiantes.mostrarMensajeError(
                        "Debe seleccionar un alumno de la tabla para modificar su perfil.");
                return;
            }
            ventanaGestionEstudiantes.setAccionesHabilitadas(false);
            Estudiante estudianteSeleccionado = listaEstudiantesEnMemoria.get(filaSeleccionada);
            abrirFormularioRegistro(estudianteSeleccionado);
        });
        ventanaGestionEstudiantes.addEliminarListener(e -> procesarBajaEstudiante());
        ventanaGestionEstudiantes.addVolverAtrasListener(e -> ventanaGestionEstudiantes.cerrarVentana());
    }

    public void cargarEstudiantesDesdeBD() {
        AppThreadPool.ejecutarCarga(
                estudianteDAO::read,
                estudiantes -> {
                    this.listaEstudiantesEnMemoria = estudiantes;
                    // Se prefiere actualizar la tabla con la lista que se recibe.
                    ventanaGestionEstudiantes.actualizarTabla(estudiantes);
                },
                ex -> ventanaGestionEstudiantes.mostrarMensajeError(
                        "Fallo al sincronizar alumnos: " + ex.getMessage()
                )
        );
    }

    private void aplicarFiltrosMemoria() {
        String texto = ventanaGestionEstudiantes.getTextoBusqueda().toLowerCase().trim();

        List<Estudiante> filtrados = listaEstudiantesEnMemoria.stream()
                .filter(e -> texto.isBlank() ||
                        e.getNombre().toLowerCase().contains(texto) ||
                        e.getRut().toLowerCase().contains(texto))
                .toList();

        ventanaGestionEstudiantes.actualizarTabla(filtrados);
    }

    private void abrirFormularioRegistro(Estudiante estudianteAEditar) {
        VentanaRegistroEstudiante ventanaRegistroEstudiante = new VentanaRegistroEstudiante();
        ventanaRegistroEstudiante.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaGestionEstudiantes.setAccionesHabilitadas(true);
            }
        });
        new ControladorRegistroEstudiante(
                ventanaRegistroEstudiante, estudianteDAO, this, estudianteAEditar);

        if (estudianteAEditar != null) {
            ventanaRegistroEstudiante.prellenarFormulario(
                    estudianteAEditar.getRut(),
                    estudianteAEditar.getNombre(),
                    estudianteAEditar.getCurso(),
                    estudianteAEditar.getCorreo()
            );
        }

        ventanaRegistroEstudiante.setVisible(true);
    }

    private void procesarBajaEstudiante() {
        Integer idEstudiante = ventanaGestionEstudiantes.getIdEstudianteSeleccionado();
        if (idEstudiante == null) {
            ventanaGestionEstudiantes.mostrarMensajeError(
                    "Debe seleccionar un estudiante de la tabla para darle de baja.");
            return;
        }

        if (!ventanaGestionEstudiantes.confirmarEliminacion("estudiante del sistema")) {
            return;
        }

        ventanaGestionEstudiantes.setAccionesHabilitadas(false);

        AppThreadPool.ejecutarAccion(
                () -> estudianteDAO.delete(idEstudiante),
                () -> {
                    ventanaGestionEstudiantes.mostrarMensajeConfirmacion(
                            "El estudiante ha sido dado de baja exitosamente.");
                    cargarEstudiantesDesdeBD();
                    ventanaGestionEstudiantes.setAccionesHabilitadas(true);
                },
                ex -> {
                    if (ex instanceof RestriccionIntegridadException) {
                        ventanaGestionEstudiantes.mostrarMensajeError(
                                "No es posible dar de baja a este estudiante porque" +
                                        " posee registros de préstamos asociados.");
                    } else {
                        ventanaGestionEstudiantes.mostrarMensajeError(
                                "Error al eliminar registro: " + ex.getMessage()
                        );
                    }
                    ventanaGestionEstudiantes.setAccionesHabilitadas(true);
                }
        );
    }
}
