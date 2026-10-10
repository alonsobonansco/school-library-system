package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class VentanaReportes extends VentanaBase {

    private JTable topLibrosTable;
    private DefaultTableModel topLibrosTableModel;
    private JComboBox<Estudiante> estudiantesComboBox;
    private JTable historialEstudianteTable;
    private DefaultTableModel historialEstudianteTableModel;
    private JTable librosActivosTable;
    private DefaultTableModel librosActivosTableModel;
    private JButton atrasButton;

    public VentanaReportes() {
        super("Reportes y Estadísticas de Biblioteca", 850, 550);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        atrasButton = new JButton("Atrás");
        atrasButton.setFont(new Font("Arial", Font.BOLD, 12));
        atrasButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String[] columnasTopLibros = {"Título Libro", "Cantidad Préstamos"};
        topLibrosTableModel = new DefaultTableModel(columnasTopLibros, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        topLibrosTable = new JTable(topLibrosTableModel);
        configurarEstiloTabla(topLibrosTable, new int[]{180, 60});

        estudiantesComboBox = new JComboBox<>();
        estudiantesComboBox.setFont(new Font("Arial", Font.PLAIN, 12));

        String[] columnasHistorialEstudiante = {"Título Libro", "Estado"};
        historialEstudianteTableModel = new DefaultTableModel(columnasHistorialEstudiante, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historialEstudianteTable = new JTable(historialEstudianteTableModel);
        configurarEstiloTabla(historialEstudianteTable, new int[]{160, 80});

        String[] columnasLibrosActivos = {"ID", "Título Libro", "Alumno Responsable", "Vencimiento"};
        librosActivosTableModel = new DefaultTableModel(columnasLibrosActivos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        librosActivosTable = new JTable(librosActivosTableModel);
        configurarEstiloTabla(librosActivosTable, new int[]{40, 240, 150, 140});
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

    private void construirLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        JLabel tituloLabel = new JLabel("Panel de Inteligencia y Análisis de Datos", SwingConstants.LEFT);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(tituloLabel, BorderLayout.WEST);
        panelTitulo.add(atrasButton, BorderLayout.EAST);
        add(panelTitulo, BorderLayout.NORTH);

        JPanel panelTopLibros = new JPanel(new BorderLayout(5, 5));
        panelTopLibros.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Libros Más Prestados"));
        panelTopLibros.add(new JScrollPane(topLibrosTable), BorderLayout.CENTER);

        JPanel panelHistorialEstudiante = new JPanel(new BorderLayout(5, 5));
        panelHistorialEstudiante.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Historial por Estudiante"));

        JPanel panelFiltroEstudiante = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltroEstudiante.add(new JLabel("Estudiante:"));
        panelFiltroEstudiante.add(estudiantesComboBox);
        panelHistorialEstudiante.add(panelFiltroEstudiante, BorderLayout.NORTH);
        panelHistorialEstudiante.add(new JScrollPane(historialEstudianteTable), BorderLayout.CENTER);

        JSplitPane divisionSuperior = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        divisionSuperior.setLeftComponent(panelTopLibros);
        divisionSuperior.setRightComponent(panelHistorialEstudiante);

        divisionSuperior.setDividerLocation(410);
        divisionSuperior.setResizeWeight(0.5);
        divisionSuperior.setEnabled(false);

        JPanel panelLibrosActivos = new JPanel(new BorderLayout(5, 5));
        panelLibrosActivos.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), "Listado de Libros en Préstamo"));
        panelLibrosActivos.add(new JScrollPane(librosActivosTable), BorderLayout.CENTER);

        JPanel panelContenedor = new JPanel(new GridLayout(2, 1, 10, 10));
        panelContenedor.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        panelContenedor.add(divisionSuperior);
        panelContenedor.add(panelLibrosActivos);

        add(panelContenedor, BorderLayout.CENTER);
    }

    public void poblarAlumnos(List<Estudiante> estudiantes) {
        estudiantesComboBox.removeAllItems();
        for (Estudiante e : estudiantes) {
            estudiantesComboBox.addItem(e);
        }
    }

    public void actualizarTopLibros(List<Object[]> datos) {
        topLibrosTableModel.setRowCount(0);
        for (Object[] fila : datos) {
            topLibrosTableModel.addRow(fila);
        }
    }

    public void actualizarHistorialAlumno(List<Prestamo> prestamos) {
        historialEstudianteTableModel.setRowCount(0);
        for (Prestamo p : prestamos) {
            String estado = p.isDevuelto() ? "DEVUELTO" : "VIGENTE";
            Object[] fila = {p.getLibro().getTitulo(), estado};
            historialEstudianteTableModel.addRow(fila);
        }
    }

    public void actualizarLibrosActivos(List<Prestamo> prestamos) {
        librosActivosTableModel.setRowCount(0);

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
            librosActivosTableModel.addRow(fila);
        }
    }

    public Estudiante getAlumnoSeleccionado() {
        return (Estudiante) estudiantesComboBox.getSelectedItem();
    }

    public void addComboAlumnoListener(ActionListener listener) {
        estudiantesComboBox.addActionListener(listener);
    }

    public void addVolverAtrasListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
    }
}
