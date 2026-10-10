package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaGestionEstudiantes;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroEstudiante;

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
        ventanaGestionEstudiantes.addAgregarListener(e -> abrirFormularioFlotante(null));
        ventanaGestionEstudiantes.addEditarListener(e -> {
            int fila = ventanaGestionEstudiantes.getFilaSeleccionada();
            if (fila == -1) {
                ventanaGestionEstudiantes.mostrarMensajeError(
                        "Debe seleccionar un alumno de la tabla para modificar su perfil.");
                return;
            }
            Estudiante seleccionado = listaEstudiantesEnMemoria.get(fila);
            abrirFormularioFlotante(seleccionado);
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

    private void abrirFormularioFlotante(Estudiante estudianteAEditar) {
        VentanaRegistroEstudiante ventanaRegistroEstudiante = new VentanaRegistroEstudiante();
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

        AppThreadPool.ejecutarAccion(
                () -> estudianteDAO.delete(idEstudiante),
                () -> {
                    ventanaGestionEstudiantes.mostrarMensajeConfirmacion(
                            "El estudiante ha sido dado de baja exitosamente.");
                    cargarEstudiantesDesdeBD();
                },
                ex -> ventanaGestionEstudiantes.mostrarMensajeError(
                        "Error al eliminar registro: " + ex.getMessage()
                )
        );
    }
}
