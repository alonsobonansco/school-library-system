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


public class ControladorReportes {

    private final VentanaReportes ventanaReportes;
    private final PrestamoDAO prestamoDAO;
    private final EstudianteDAO estudianteDAO;
    private List<Prestamo> listaGlobalPrestamosCache = new ArrayList<>();


    public ControladorReportes(VentanaReportes ventanaReportes) {
        this.ventanaReportes = ventanaReportes;
        this.prestamoDAO = new PrestamoDAO();
        this.estudianteDAO = new EstudianteDAO();

        inicializarListeners();
        cargarDatosAnaliticos();
    }

    private void inicializarListeners() {
        ventanaReportes.addAtrasListener(e -> ventanaReportes.cerrarVentana());
        ventanaReportes.addComboAlumnoListener(e -> filtrarHistorialAlumnoEnMemoria());
    }

    private void cargarDatosAnaliticos() {
        AppThreadPool.ejecutarCarga(
                () -> {
                    List<Estudiante> estudiantes = estudianteDAO.read();
                    listaGlobalPrestamosCache = prestamoDAO.read();

                    List<Prestamo> librosActivos = listaGlobalPrestamosCache.stream()
                            .filter(p -> !p.isDevuelto())
                            .toList();

                    Map<String, Long> conteoPorTitulo = listaGlobalPrestamosCache.stream()
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
                    ventanaReportes.poblarComboAlumnos(resultado.estudiantes());
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

        List<Prestamo> historialFiltrado = listaGlobalPrestamosCache.stream()
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
