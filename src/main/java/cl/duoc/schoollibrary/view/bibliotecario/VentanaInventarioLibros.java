package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaInventarioLibros extends VentanaBase {

    private JTextField buscarTextField;
    private JComboBox<String> categoriaComboBox;
    private JTable librosTable;
    private DefaultTableModel modeloTable;

    private JButton agregarButton;
    private JButton editarButton;
    private JButton eliminarButton;
    private JButton atrasButton;

    public VentanaInventarioLibros() {
        super("Panel de Control - Inventario de Libros", 850, 550);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        buscarTextField = new JTextField(20);
        buscarTextField.setFont(new Font("Arial", Font.PLAIN, 14));

        categoriaComboBox = new JComboBox<>();
        categoriaComboBox.setFont(new Font("Arial", Font.PLAIN, 13));

        String[] columnas = {"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"};
        modeloTable = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        librosTable = new JTable(modeloTable);
        librosTable.setFont(new Font("Arial", Font.PLAIN, 13));
        librosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Font fuenteBotones = new Font("Arial", Font.BOLD, 13);
        agregarButton = new JButton("Añadir Libro");
        agregarButton.setFont(fuenteBotones);
        agregarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        editarButton = new JButton("Editar Seleccionado");
        editarButton.setFont(fuenteBotones);
        editarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        eliminarButton = new JButton("Eliminar Catálogo");
        eliminarButton.setFont(fuenteBotones);
        eliminarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        atrasButton = new JButton("Atrás");
        atrasButton.setFont(fuenteBotones);
        atrasButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        librosTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        librosTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        librosTable.getColumnModel().getColumn(5).setPreferredWidth(50);
        librosTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        librosTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        librosTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        librosTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        librosTable.getColumnModel().getColumn(4).setPreferredWidth(130);
    }

    private void construirLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        JLabel buscarLabel = new JLabel("Buscar por Título / ISBN:");
        buscarLabel.setFont(new Font("Arial", Font.BOLD, 13));
        panelSuperior.add(buscarLabel);
        panelSuperior.add(buscarTextField);

        JLabel categoriaLabel = new JLabel("Categoría:");
        categoriaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        panelSuperior.add(categoriaLabel);
        panelSuperior.add(categoriaComboBox);
        add(panelSuperior, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(librosTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelInferior.add(agregarButton);
        panelInferior.add(editarButton);
        panelInferior.add(eliminarButton);
        panelInferior.add(atrasButton);
        add(panelInferior, BorderLayout.SOUTH);
    }

    public void actualizarTabla(List<Libro> libros) {
        SwingUtilities.invokeLater(() -> {
            modeloTable.setRowCount(0);
            for (Libro l : libros) {
                Object[] fila = {
                        l.getId(),
                        l.getTitulo(),
                        l.getAutor(),
                        l.getIsbn(),
                        l.getEditorial(),
                        l.getStock(),
                        l.getCategoria().getNombre()
                };
                modeloTable.addRow(fila);
            }
        });
    }

    public void poblarComboBoxCategorias(List<String> nombresCategorias) {
        SwingUtilities.invokeLater(() -> {
            categoriaComboBox.removeAllItems();
            categoriaComboBox.addItem("Todas");
            for (String cat : nombresCategorias) {
                categoriaComboBox.addItem(cat);
            }
        });
    }

    public String getTextoBusqueda() {
        return buscarTextField.getText().trim();
    }

    public String getCategoriaSeleccionada() {
        return (String) categoriaComboBox.getSelectedItem();
    }

    public Integer getIdLibroSeleccionado() {
        int filaSeleccionada = librosTable.getSelectedRow();
        if (filaSeleccionada == -1) return null;
        return (Integer) modeloTable.getValueAt(filaSeleccionada, 0);
    }

    /**
     * Captura las filas completas seleccionadas para enviar los datos listos al prellenado.
     */
    public Libro getLibroSeleccionadoEnTabla(List<Libro> cacheGlobal) {
        Integer id = getIdLibroSeleccionado();
        if (id == null) return null;
        return cacheGlobal.stream().filter(l -> l.getId() == id).findFirst().orElse(null);
    }

    public void addBusquedaDocumentListener(Runnable accion) {
        buscarTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
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

    public void addCategoriaComboListener(ActionListener listener) {
        categoriaComboBox.addActionListener(listener);
    }

    public void addNuevoButtonListener(ActionListener listener) {
        agregarButton.addActionListener(listener);
    }

    public void addEditarButtonListener(ActionListener listener) {
        editarButton.addActionListener(listener);
    }

    public void addEliminarButtonListener(ActionListener listener) {
        eliminarButton.addActionListener(listener);
    }

    public void addAtrasButtonListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
    }
}
