package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaControlPrestamos extends VentanaBase {

    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;

    private JButton btnNuevoPrestamo;
    private JButton btnRegistrarDevolucion;
    private JButton btnAtras;

    public VentanaControlPrestamos() {
        super("Panel de Control - Control de Préstamos y Devoluciones", 850, 550);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        String[] columnas = {"ID Préstamo", "RUT Estudiante", "Nombre Alumno", "Título del Libro", "Fecha Salida", "Fecha Vencimiento", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPrestamos = new JTable(modeloTabla);
        tablaPrestamos.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaPrestamos.getTableHeader().setReorderingAllowed(false);
        tablaPrestamos.getTableHeader().setResizingAllowed(false);

        tablaPrestamos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaPrestamos.getColumnModel().getColumn(0).setPreferredWidth(70);   // ID
        tablaPrestamos.getColumnModel().getColumn(1).setPreferredWidth(90);   // RUT
        tablaPrestamos.getColumnModel().getColumn(2).setPreferredWidth(160);  // Alumno
        tablaPrestamos.getColumnModel().getColumn(3).setPreferredWidth(210);  // Libro (Espacioso)
        tablaPrestamos.getColumnModel().getColumn(4).setPreferredWidth(90);   // Salida
        tablaPrestamos.getColumnModel().getColumn(5).setPreferredWidth(110);  // Vencimiento
        tablaPrestamos.getColumnModel().getColumn(6).setPreferredWidth(100);  // Estado semántico

        Font fuenteBotones = new Font("Arial", Font.BOLD, 13);

        btnNuevoPrestamo = new JButton("Asignar Nuevo Préstamo");
        btnNuevoPrestamo.setFont(fuenteBotones);
        btnNuevoPrestamo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRegistrarDevolucion = new JButton("Registrar Devolución");
        btnRegistrarDevolucion.setFont(fuenteBotones);
        btnRegistrarDevolucion.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAtras = new JButton("Atrás");
        btnAtras.setFont(fuenteBotones);
        btnAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JLabel lblTituloSeccion = new JLabel("Historial de Operaciones Vigentes e Históricas");
        lblTituloSeccion.setFont(new Font("Arial", Font.BOLD, 14));
        panelSuperior.add(lblTituloSeccion, BorderLayout.WEST);
        add(panelSuperior, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tablaPrestamos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        panelInferior.add(btnNuevoPrestamo);
        panelInferior.add(btnRegistrarDevolucion);
        panelInferior.add(btnAtras);
        add(panelInferior, BorderLayout.SOUTH);
    }

    public void actualizarTabla(List<Prestamo> prestamos) {
        SwingUtilities.invokeLater(() -> {
            modeloTabla.setRowCount(0);
            for (Prestamo p : prestamos) {
                String estado = p.isDevuelto() ? "DEVUELTO" : "VIGENTE";

                Object[] fila = {
                        p.getId(),
                        p.getEstudiante().getRut(),
                        p.getEstudiante().getNombre(),
                        p.getLibro().getTitulo(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucion(),
                        estado
                };
                modeloTabla.addRow(fila);
            }
        });
    }

    public int getFilaSeleccionada() {
        return tablaPrestamos.getSelectedRow();
    }

    public void addNuevoPrestamoListener(ActionListener listener) {
        btnNuevoPrestamo.addActionListener(listener);
    }

    public void addDevolucionListener(ActionListener listener) {
        btnRegistrarDevolucion.addActionListener(listener);
    }

    public void addAtrasListener(ActionListener listener) {
        btnAtras.addActionListener(listener);
    }
}
