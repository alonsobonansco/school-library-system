package cl.duoc.schoollibrary.view;

import cl.duoc.schoollibrary.model.entity.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaInventarioLibros extends VentanaBase {

    private JTextField txtBuscar;
    private JComboBox<String> comboCategorias;
    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;

    public VentanaInventarioLibros() {
        super("Panel de Control - Inventario de Libros", 850, 550);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));

        comboCategorias = new JComboBox<>();
        comboCategorias.setFont(new Font("Arial", Font.PLAIN, 13));

        String[] columnas = {"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaLibros = new JTable(modeloTabla);
        tablaLibros.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Font fuenteBotones = new Font("Arial", Font.BOLD, 13);
        btnNuevo = new JButton("Añadir Libro");
        btnNuevo.setFont(fuenteBotones);
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEditar = new JButton("Editar Seleccionado");
        btnEditar.setFont(fuenteBotones);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEliminar = new JButton("Eliminar Catálogo");
        btnEliminar.setFont(fuenteBotones);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        JLabel lblBuscar = new JLabel("Buscar por Título / ISBN:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 13));
        panelSuperior.add(lblBuscar);
        panelSuperior.add(txtBuscar);

        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setFont(new Font("Arial", Font.BOLD, 13));
        panelSuperior.add(lblCategoria);
        panelSuperior.add(comboCategorias);
        add(panelSuperior, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tablaLibros);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelInferior.add(btnNuevo);
        panelInferior.add(btnEditar);
        panelInferior.add(btnEliminar);
        add(panelInferior, BorderLayout.SOUTH);
    }

    public void actualizarTabla(List<Libro> libros) {
        SwingUtilities.invokeLater(() -> {
            modeloTabla.setRowCount(0);
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
                modeloTabla.addRow(fila);
            }
        });
    }

    public void poblarComboBoxCategorias(List<String> nombresCategorias) {
        SwingUtilities.invokeLater(() -> {
            comboCategorias.removeAllItems();
            comboCategorias.addItem("Todas");
            for (String cat : nombresCategorias) {
                comboCategorias.addItem(cat);
            }
        });
    }

    public String getTextoBusqueda() {
        return txtBuscar.getText().trim();
    }

    public String getCategoriaSeleccionada() {
        return (String) comboCategorias.getSelectedItem();
    }

    public Integer getIdLibroSeleccionado() {
        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada == -1) return null;
        return (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
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

    public void addCategoriaComboListener(ActionListener listener) {
        comboCategorias.addActionListener(listener);
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
}
