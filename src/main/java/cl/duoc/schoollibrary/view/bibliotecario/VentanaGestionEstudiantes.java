package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel de control centralizado para la administración del padrón de estudiantes.
 * Incorpora el motor de escucha DocumentListener y bloqueo de cabeceras inmutables.
 */
public class VentanaGestionEstudiantes extends VentanaBase {

    private JTextField buscarTextField;
    private JTable estudiantesTable;
    private DefaultTableModel estudiantesTableModel;

    private JButton agregarButton;
    private JButton editarButton;
    private JButton eliminarButton;
    private JButton atrasButton;

    public VentanaGestionEstudiantes() {
        super("Panel de Control - Gestión de Estudiantes", 850, 550);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        buscarTextField = new JTextField(20);
        buscarTextField.setFont(FUENTE_CAMPO.deriveFont(14f));

        String[] columnas = {"ID", "RUT", "Nombre Completo", "Curso", "Correo Electrónico"};
        estudiantesTableModel = crearModeloTablaNoEditable(columnas);

        estudiantesTable = new JTable(estudiantesTableModel);
        estudiantesTable.setFont(FUENTE_CAMPO.deriveFont(13f));
        estudiantesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        estudiantesTable.getTableHeader().setReorderingAllowed(false);
        estudiantesTable.getTableHeader().setResizingAllowed(false);

        estudiantesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        estudiantesTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        estudiantesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        estudiantesTable.getColumnModel().getColumn(2).setPreferredWidth(260);
        estudiantesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        estudiantesTable.getColumnModel().getColumn(4).setPreferredWidth(230);

        agregarButton = new JButton("Registrar Alumno");
        agregarButton.setFont(FUENTE_BOTON.deriveFont(13f));
        agregarButton.setCursor(CURSOR_CLICK);

        editarButton = new JButton("Editar Perfil");
        editarButton.setFont(FUENTE_BOTON.deriveFont(13f));
        editarButton.setCursor(CURSOR_CLICK);

        eliminarButton = new JButton("Dar de Baja");
        eliminarButton.setFont(FUENTE_BOTON.deriveFont(13f));
        eliminarButton.setCursor(CURSOR_CLICK);

        atrasButton = new JButton("Atrás");
        atrasButton.setFont(FUENTE_BOTON.deriveFont(13f));
        atrasButton.setCursor(CURSOR_CLICK);
    }

    private void construirLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel lblBuscar = new JLabel("Buscar por Nombre / RUT:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 13));
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(buscarTextField);
        panelSuperior.add(panelBusqueda, BorderLayout.WEST);

        add(panelSuperior, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(estudiantesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelInferior.add(agregarButton);
        panelInferior.add(editarButton);
        panelInferior.add(eliminarButton);
        panelInferior.add(atrasButton);
        add(panelInferior, BorderLayout.SOUTH);
    }

    public void actualizarTabla(List<Estudiante> estudiantes) {
        estudiantesTableModel.setRowCount(0);
        for (Estudiante e : estudiantes) {
            Object[] fila = {e.getId(), e.getRut(), e.getNombre(), e.getCurso(), e.getCorreo()};
            estudiantesTableModel.addRow(fila);
        }
    }

    public String getTextoBusqueda() {
        return buscarTextField.getText().trim();
    }

    public Integer getIdEstudianteSeleccionado() {
        int fila = estudiantesTable.getSelectedRow();
        if (fila == -1) return null;
        return (Integer) estudiantesTableModel.getValueAt(fila, 0);
    }

    public int getFilaSeleccionada() {
        return estudiantesTable.getSelectedRow();
    }

    public void addBusquedaDocumentListener(Runnable accion) {
        buscarTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                accion.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                accion.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                accion.run();
            }
        });
    }

    public void addAgregarListener(ActionListener listener) {
        agregarButton.addActionListener(listener);
    }

    public void addEditarListener(ActionListener listener) {
        editarButton.addActionListener(listener);
    }

    public void addEliminarListener(ActionListener listener) {
        eliminarButton.addActionListener(listener);
    }

    public void addVolverAtrasListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
    }
}
