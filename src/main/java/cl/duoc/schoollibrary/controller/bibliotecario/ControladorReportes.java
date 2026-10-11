package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.dao.PrestamoDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaReportes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cerebro administrativo encargado de generar reportes analíticos y estadísticos
 * sobre el historial de préstamos, devoluciones y actividad de los estudiantes.
 */
public class ControladorReportes {

    private final VentanaReportes ventanaReportes;
    private final PrestamoDAO prestamoDAO;
    private final EstudianteDAO estudianteDAO;
    private List<Prestamo> listaPrestamosEnMemoria = new ArrayList<>();


    public ControladorReportes(VentanaReportes ventanaReportes) {
        this.ventanaReportes = ventanaReportes;
        this.prestamoDAO = new PrestamoDAO();
        this.estudianteDAO = new EstudianteDAO();

        inicializarListeners();
        cargarDatosAnaliticos();
    }

    private void inicializarListeners() {
        ventanaReportes.addVolverAtrasListener(e -> ventanaReportes.cerrarVentana());
        ventanaReportes.addComboAlumnoListener(e -> filtrarHistorialAlumnoEnMemoria());
    }

    /**
     * Coordina el procesamiento y la carga asíncrona de los datos analíticos del sistema.
     * Consolida múltiples lecturas desde la persistencia y ejecuta agrupamientos reactivos en memoria
     * mediante Streams y operaciones lambda para estructurar los rankings métricos de títulos más solicitados,
     * despachando el resultado empaquetado de forma segura de vuelta al hilo de la interfaz gráfica.
     */
    private void cargarDatosAnaliticos() {
        AppThreadPool.ejecutarCarga(
                () -> {
                    List<Estudiante> estudiantes = estudianteDAO.read();
                    listaPrestamosEnMemoria = prestamoDAO.read();

                    List<Prestamo> librosActivos = listaPrestamosEnMemoria.stream()
                            .filter(p -> !p.isDevuelto())
                            .toList();

                    Map<String, Long> conteoPorTitulo = listaPrestamosEnMemoria.stream()
                            .collect(Collectors.groupingBy(
                                    p -> p.getLibro().getTitulo(),
                                    Collectors.counting()
                            ));

                    List<Object[]> topLibros = conteoPorTitulo.entrySet().stream()
                            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                            .limit(5)
                            .map(entry -> new Object[]{
                                    entry.getKey(),
                                    entry.getValue().intValue()
                            })
                            .toList();

                    return new DatosReporte(estudiantes, librosActivos, topLibros);
                },
                resultado -> {
                    ventanaReportes.poblarAlumnos(resultado.estudiantes());
                    ventanaReportes.actualizarLibrosActivos(resultado.librosActivos());
                    ventanaReportes.actualizarTopLibros(resultado.topLibros());

                    filtrarHistorialAlumnoEnMemoria();
                },
                ex -> ventanaReportes.mostrarMensajeError("Error al generar reportes: " + ex.getMessage())
        );
    }

    private void filtrarHistorialAlumnoEnMemoria() {
        Estudiante seleccionado = ventanaReportes.getAlumnoSeleccionado();
        if (seleccionado == null) return;

        List<Prestamo> historialFiltrado = listaPrestamosEnMemoria.stream()
                .filter(p -> p.getEstudiante().getId() == seleccionado.getId())
                .toList();

        ventanaReportes.actualizarHistorialAlumno(historialFiltrado);
    }

    // record: generación automática de constructor, getters, equals, hashCode y toString
    private record DatosReporte(
            List<Estudiante> estudiantes,
            List<Prestamo> librosActivos,
            List<Object[]> topLibros
    ) {
    }
}
