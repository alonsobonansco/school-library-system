package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.dao.PrestamoDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaReportes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ControladorReportes {

    private final VentanaReportes vista;
    private final PrestamoDAO prestamoDAO;
    private final EstudianteDAO estudianteDAO;
    private List<Prestamo> listaGlobalPrestamosCache = new ArrayList<>();

    public ControladorReportes(VentanaReportes vista) {
        this.vista = vista;
        this.prestamoDAO = new PrestamoDAO();
        this.estudianteDAO = new EstudianteDAO();

        inicializarListeners();
        cargarDatosAnaliticos();
    }

    private void inicializarListeners() {
        vista.addAtrasListener(e -> vista.cerrarVentana());
        vista.addComboAlumnoListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filtrarHistorialAlumnoEnMemoria();
            }
        });
    }

    private void cargarDatosAnaliticos() {
        new Thread(() -> {
            try {
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

                javax.swing.SwingUtilities.invokeLater(() -> {
                    vista.poblarComboAlumnos(estudiantes);
                    vista.actualizarLibrosActivos(librosActivos);

                    vista.actualizarTopLibros(topLibros);

                    filtrarHistorialAlumnoEnMemoria();
                });

            } catch (DataAccessException ex) {
                javax.swing.SwingUtilities.invokeLater(() ->
                        vista.mostrarMensajeError("Error al generar reportes: " + ex.getMessage())
                );
            }
        }).start();
    }

    private void filtrarHistorialAlumnoEnMemoria() {
        Estudiante seleccionado = vista.getAlumnoSeleccionado();
        if (seleccionado == null) return;

        List<Prestamo> historialFiltrado = listaGlobalPrestamosCache.stream()
                .filter(p -> p.getEstudiante().getId() == seleccionado.getId())
                .toList();

        vista.actualizarHistorialAlumno(historialFiltrado);
    }
}
