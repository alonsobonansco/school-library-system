package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaControlPrestamos extends VentanaBase {

    private JTable prestamosTable;
    private DefaultTableModel prestamosTableModel;
    private JButton agregarButton;
    private JButton registrarDevolucionButton;
    private JButton atrasButton;

    public VentanaControlPrestamos() {
        super("Panel de Control - Control de Préstamos y Devoluciones", 850, 550);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        String[] columnas = {"ID", "RUT Alumno", "Nombre Alumno", "Título del Libro", "Fecha Salida", "Fecha Vencimiento", "Estado"};
        prestamosTableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        prestamosTable = new JTable(prestamosTableModel);
        prestamosTable.setFont(new Font("Arial", Font.PLAIN, 13));
        prestamosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        prestamosTable.getTableHeader().setReorderingAllowed(false);
        prestamosTable.getTableHeader().setResizingAllowed(false);

        prestamosTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        prestamosTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        prestamosTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        prestamosTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        prestamosTable.getColumnModel().getColumn(3).setPreferredWidth(210);
        prestamosTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        prestamosTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        prestamosTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        Font fuenteBotones = new Font("Arial", Font.BOLD, 13);

        agregarButton = new JButton("Asignar Nuevo Préstamo");
        agregarButton.setFont(fuenteBotones);
        agregarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registrarDevolucionButton = new JButton("Registrar Devolución");
        registrarDevolucionButton.setFont(fuenteBotones);
        registrarDevolucionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        atrasButton = new JButton("Atrás");
        atrasButton.setFont(fuenteBotones);
        atrasButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JLabel lblTituloSeccion = new JLabel("Historial de Operaciones Vigentes e Históricas");
        lblTituloSeccion.setFont(new Font("Arial", Font.BOLD, 14));
        panelSuperior.add(lblTituloSeccion, BorderLayout.WEST);
        add(panelSuperior, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(prestamosTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        panelInferior.add(agregarButton);
        panelInferior.add(registrarDevolucionButton);
        panelInferior.add(atrasButton);
        add(panelInferior, BorderLayout.SOUTH);
    }

    public void actualizarTabla(List<Prestamo> prestamos) {
        prestamosTableModel.setRowCount(0);
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
            prestamosTableModel.addRow(fila);
        }
    }

    public int getFilaSeleccionada() {
        return prestamosTable.getSelectedRow();
    }

    public void addRegistrarPrestamoListener(ActionListener listener) {
        agregarButton.addActionListener(listener);
    }

    public void addRegistrarDevolucionListener(ActionListener listener) {
        registrarDevolucionButton.addActionListener(listener);
    }

    public void addVolverAtrasListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
    }
}
