package cl.duoc.schoollibrary.view;

import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class VentanaReportes extends VentanaBase {

    private JTable tablaTopLibros;
    private DefaultTableModel modeloTopLibros;
    private JComboBox<Estudiante> comboAlumnosReporte;
    private JTable tablaHistorialAlumno;
    private DefaultTableModel modeloHistorialAlumno;
    private JTable tablaLibrosActivos;
    private DefaultTableModel modeloLibrosActivos;

    private JButton btnAtras;

    public VentanaReportes() {
        super("Módulo Analítico - Reportes y Estadísticas de Biblioteca", 850, 550);

        inicializarComponentes();
        construirLayoutGlobal();
    }

    private void inicializarComponentes() {
        btnAtras = new JButton("Atrás");
        btnAtras.setFont(new Font("Arial", Font.BOLD, 12));
        btnAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String[] columnasTop = {"Título Libro", "Cantidad Préstamos"};
        modeloTopLibros = new DefaultTableModel(columnasTop, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaTopLibros = new JTable(modeloTopLibros);
        configurarEstiloTabla(tablaTopLibros, new int[]{180, 60});

        comboAlumnosReporte = new JComboBox<>();
        comboAlumnosReporte.setFont(new Font("Arial", Font.PLAIN, 12));

        String[] columnasHistorial = {"Título Libro", "Estado"};
        modeloHistorialAlumno = new DefaultTableModel(columnasHistorial, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaHistorialAlumno = new JTable(modeloHistorialAlumno);
        configurarEstiloTabla(tablaHistorialAlumno, new int[]{160, 80});

        String[] columnasCalle = {"ID", "Título Libro", "Alumno Responsable", "Vencimiento"};
        modeloLibrosActivos = new DefaultTableModel(columnasCalle, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaLibrosActivos = new JTable(modeloLibrosActivos);
        configurarEstiloTabla(tablaLibrosActivos, new int[]{40, 240, 150, 140});
    }

    private void configurarEstiloTabla(JTable tabla, int[] anchos) {
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setResizingAllowed(false);

        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private void construirLayoutGlobal() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        JLabel lblTitulo = new JLabel("Consola de Inteligencia y Analítica de Datos", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(btnAtras, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        JPanel panelTopRoot = new JPanel(new BorderLayout(5, 5));
        panelTopRoot.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Libros Más Prestados"));
        panelTopRoot.add(new JScrollPane(tablaTopLibros), BorderLayout.CENTER);

        JPanel panelHistorialRoot = new JPanel(new BorderLayout(5, 5));
        panelHistorialRoot.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Historial por Estudiante"));

        JPanel panelComboFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelComboFiltro.add(new JLabel("Estudiante:"));
        panelComboFiltro.add(comboAlumnosReporte);
        panelHistorialRoot.add(panelComboFiltro, BorderLayout.NORTH);
        panelHistorialRoot.add(new JScrollPane(tablaHistorialAlumno), BorderLayout.CENTER);

        JSplitPane splitSuperior = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelTopRoot, panelHistorialRoot);
        splitSuperior.setDividerLocation(410);
        splitSuperior.setResizeWeight(0.5);
        splitSuperior.setEnabled(false);

        JPanel panelCalleRoot = new JPanel(new BorderLayout(5, 5));
        panelCalleRoot.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Listado de Libros en Préstamo"));
        panelCalleRoot.add(new JScrollPane(tablaLibrosActivos), BorderLayout.CENTER);

        JPanel panelCentralContenedor = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCentralContenedor.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        panelCentralContenedor.add(splitSuperior);
        panelCentralContenedor.add(panelCalleRoot);

        add(panelCentralContenedor, BorderLayout.CENTER);
    }

    public void poblarComboAlumnos(List<Estudiante> estudiantes) {
        SwingUtilities.invokeLater(() -> {
            comboAlumnosReporte.removeAllItems();
            for (Estudiante e : estudiantes) {
                comboAlumnosReporte.addItem(e);
            }
        });
    }

    public void actualizarTopLibros(List<Object[]> datos) {
        SwingUtilities.invokeLater(() -> {
            modeloTopLibros.setRowCount(0);
            for (Object[] fila : datos) {
                modeloTopLibros.addRow(fila);
            }
        });
    }

    public void actualizarHistorialAlumno(List<Prestamo> prestamos) {
        SwingUtilities.invokeLater(() -> {
            modeloHistorialAlumno.setRowCount(0);
            for (Prestamo p : prestamos) {
                String estado = p.isDevuelto() ? "DEVUELTO" : "VIGENTE";
                Object[] fila = {p.getLibro().getTitulo(), estado};
                modeloHistorialAlumno.addRow(fila);
            }
        });
    }

    public void actualizarLibrosActivos(List<Prestamo> prestamos) {
        SwingUtilities.invokeLater(() -> {
            modeloLibrosActivos.setRowCount(0);

            LocalDate hoy = LocalDate.now();

            for (Prestamo p : prestamos) {
                LocalDate vencimiento = p.getFechaDevolucion();

                String vencimientoStr = vencimiento.toString();

                if (!p.isDevuelto() && vencimiento.isBefore(hoy)) {
                    vencimientoStr += "   [ATRASADO]";
                }

                Object[] fila = {
                        p.getId(),
                        p.getLibro().getTitulo(),
                        p.getEstudiante().getNombre(),
                        vencimientoStr
                };
                modeloLibrosActivos.addRow(fila);
            }
        });
    }

    public Estudiante getAlumnoSeleccionado() {
        return (Estudiante) comboAlumnosReporte.getSelectedItem();
    }

    public void addComboAlumnoListener(ActionListener listener) {
        comboAlumnosReporte.addActionListener(listener);
    }

    public void addAtrasListener(ActionListener listener) {
        btnAtras.addActionListener(listener);
    }
}
