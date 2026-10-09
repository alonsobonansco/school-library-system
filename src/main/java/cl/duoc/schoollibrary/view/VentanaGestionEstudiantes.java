package cl.duoc.schoollibrary.view;

import cl.duoc.schoollibrary.model.entity.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel de control centralizado para la administración del padrón de estudiantes.
 * Incorpora el motor de escucha DocumentListener y bloqueo de cabeceras inmutables.
 */
public class VentanaGestionEstudiantes extends VentanaBase {

    private JTextField txtBuscar;
    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnAtras;

    public VentanaGestionEstudiantes() {
        super("Panel de Control - Gestión de Estudiantes", 850, 550);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));

        String[] columnas = {"ID", "RUT", "Nombre Completo", "Curso", "Correo Electrónico"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEstudiantes = new JTable(modeloTabla);
        tablaEstudiantes.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaEstudiantes.getTableHeader().setReorderingAllowed(false);
        tablaEstudiantes.getTableHeader().setResizingAllowed(false);

        tablaEstudiantes.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaEstudiantes.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaEstudiantes.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaEstudiantes.getColumnModel().getColumn(2).setPreferredWidth(260);
        tablaEstudiantes.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaEstudiantes.getColumnModel().getColumn(4).setPreferredWidth(230);

        Font fuenteBotones = new Font("Arial", Font.BOLD, 13);
        btnNuevo = new JButton("Registrar Alumno");
        btnNuevo.setFont(fuenteBotones);
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEditar = new JButton("Modificar Perfil");
        btnEditar.setFont(fuenteBotones);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEliminar = new JButton("Dar de Baja");
        btnEliminar.setFont(fuenteBotones);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAtras = new JButton("Atrás");
        btnAtras.setFont(fuenteBotones);
        btnAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JPanel panelBusquedaInterior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel lblBuscar = new JLabel("Filtrar por Nombre / RUT:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 13));
        panelBusquedaInterior.add(lblBuscar);
        panelBusquedaInterior.add(txtBuscar);
        panelSuperior.add(panelBusquedaInterior, BorderLayout.WEST);

        add(panelSuperior, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tablaEstudiantes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelInferior.add(btnNuevo);
        panelInferior.add(btnEditar);
        panelInferior.add(btnEliminar);
        panelInferior.add(btnAtras);
        add(panelInferior, BorderLayout.SOUTH);
    }

    public void actualizarTabla(List<Estudiante> estudiantes) {
        SwingUtilities.invokeLater(() -> {
            modeloTabla.setRowCount(0);
            for (Estudiante e : estudiantes) {
                Object[] fila = {e.getId(), e.getRut(), e.getNombre(), e.getCurso(), e.getCorreo()};
                modeloTabla.addRow(fila);
            }
        });
    }

    public String getTextoBusqueda() {
        return txtBuscar.getText().trim();
    }

    public Integer getIdEstudianteSeleccionado() {
        int fila = tablaEstudiantes.getSelectedRow();
        if (fila == -1) return null;
        return (Integer) modeloTabla.getValueAt(fila, 0);
    }

    public int getFilaSeleccionada() {
        return tablaEstudiantes.getSelectedRow();
    }

    public void addBusquedaDocumentListener(Runnable accion) {
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                accion.run();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                accion.run();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                accion.run();
            }
        });
    }

    public void addNuevoButtonListener(ActionListener listener) {
        btnNuevo.addActionListener(listener);
    }

    public void addEditarButtonListener(ActionListener listener) {
        btnEditar.addActionListener(listener);
    }

    public void addEliminarButtonListener(ActionListener listener) {
        btnEliminar.addActionListener(listener);
    }

    public void addAtrasButtonListener(ActionListener listener) {
        btnAtras.addActionListener(listener);
    }
}
