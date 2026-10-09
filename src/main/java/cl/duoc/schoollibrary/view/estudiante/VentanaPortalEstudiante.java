package cl.duoc.schoollibrary.view.estudiante;

import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.model.entity.Prestamo;
import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Interfaz principal y portal de autoservicio exclusivo para usuarios con rol Estudiante.
 * Centraliza la consulta de catálogo y el historial de préstamos activos mediante pestañas.
 */
public class VentanaPortalEstudiante extends VentanaBase {

    private JLabel tituloLabel;
    private JTabbedPane pestaniasPanel;
    private JTextField buscarLibroTextField;
    private JComboBox<String> categoriaComboBox;
    private JTable librosTable;
    private DefaultTableModel tablaLibrosModel;
    private JButton solicitarPrestamoButton;
    private JTable prestamosTable;
    private DefaultTableModel tablaPrestamosModel;
    private JButton devolverLibroButton;
    private JButton cerrarSesionButton;

    public VentanaPortalEstudiante() {
        super("Portal del Estudiante - Sistema de Biblioteca", 850, 550);

        inicializarComponentes();
        inicializarPestaniaCatalogo();
        inicializarPestaniaMisPrestamos();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Bienvenido al Portal de Autoservicio", SwingConstants.LEFT);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        pestaniasPanel = new JTabbedPane();
        pestaniasPanel.setFont(new Font("Arial", Font.BOLD, 13));

        cerrarSesionButton = new JButton("Cerrar Sesión");
        cerrarSesionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void inicializarPestaniaCatalogo() {
        buscarLibroTextField = new JTextField(20);
        buscarLibroTextField.setFont(new Font("Arial", Font.PLAIN, 14));
        categoriaComboBox = new JComboBox<>();
        categoriaComboBox.setFont(new Font("Arial", Font.PLAIN, 13));

        String[] columnasLibros = {"Título", "Autor", "ISBN", "Categoría", "Disponibilidad"};
        tablaLibrosModel = new DefaultTableModel(columnasLibros, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        librosTable = new JTable(tablaLibrosModel);
        librosTable.setFont(new Font("Arial", Font.PLAIN, 13));
        librosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        librosTable.getTableHeader().setReorderingAllowed(false);
        librosTable.getTableHeader().setResizingAllowed(false);

        librosTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        librosTable.getColumnModel().getColumn(0).setPreferredWidth(280);
        librosTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        librosTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        librosTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        librosTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        solicitarPrestamoButton = new JButton("Solicitar Préstamo");
        solicitarPrestamoButton.setFont(new Font("Arial", Font.BOLD, 13));
        solicitarPrestamoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void inicializarPestaniaMisPrestamos() {
        String[] columnasPrestamos = {"ID Préstamo", "Título del Libro", "Fecha Préstamo", "Fecha Vencimiento"};
        tablaPrestamosModel = new DefaultTableModel(columnasPrestamos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        prestamosTable = new JTable(tablaPrestamosModel);
        prestamosTable.setFont(new Font("Arial", Font.PLAIN, 13));
        prestamosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prestamosTable.getTableHeader().setReorderingAllowed(false);
        prestamosTable.getTableHeader().setResizingAllowed(false);

        devolverLibroButton = new JButton("Marcar Devolución");
        devolverLibroButton.setFont(new Font("Arial", Font.BOLD, 13));
        devolverLibroButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(tituloLabel, BorderLayout.NORTH);

        JPanel panelContenederLogout = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        panelContenederLogout.add(cerrarSesionButton);
        panelSuperior.add(panelContenederLogout, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCatalogoRoot = new JPanel(new BorderLayout(10, 10));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panelFiltros.add(new JLabel("Buscar Libro / ISBN:"));
        panelFiltros.add(buscarLibroTextField);
        panelFiltros.add(new JLabel("Categoría:"));
        panelFiltros.add(categoriaComboBox);
        panelCatalogoRoot.add(panelFiltros, BorderLayout.NORTH);

        JScrollPane scrollLibros = new JScrollPane(librosTable);
        scrollLibros.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        panelCatalogoRoot.add(scrollLibros, BorderLayout.CENTER);

        JPanel panelBotonCatalogo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotonCatalogo.add(solicitarPrestamoButton);
        panelCatalogoRoot.add(panelBotonCatalogo, BorderLayout.SOUTH);

        JPanel panelPrestamosRoot = new JPanel(new BorderLayout(10, 10));

        JScrollPane scrollPrestamos = new JScrollPane(prestamosTable);
        scrollPrestamos.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        panelPrestamosRoot.add(scrollPrestamos, BorderLayout.CENTER);

        JPanel panelBotonPrestamos = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotonPrestamos.add(devolverLibroButton);
        panelPrestamosRoot.add(panelBotonPrestamos, BorderLayout.SOUTH);

        pestaniasPanel.addTab("Consultar Catálogo", panelCatalogoRoot);
        pestaniasPanel.addTab("Mis Préstamos Activos", panelPrestamosRoot);

        add(pestaniasPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void establecerNombreUsuario(String nombre) {
        SwingUtilities.invokeLater(() -> tituloLabel.setText("Bienvenid@ al Portal de Autoservicio: " + nombre));
    }

    public void actualizarTablaLibros(List<Libro> libros) {
        SwingUtilities.invokeLater(() -> {
            tablaLibrosModel.setRowCount(0);
            for (Libro l : libros) {
                String disponibilidad = (l.getStock() > 0) ? "DISPONIBLE (" + l.getStock() + " u)" : "AGOTADO";
                Object[] fila = {l.getTitulo(), l.getAutor(), l.getIsbn(), l.getCategoria().getNombre(), disponibilidad};
                tablaLibrosModel.addRow(fila);
            }
        });
    }

    public void actualizarTablaPrestamos(List<Prestamo> prestamos) {
        SwingUtilities.invokeLater(() -> {
            tablaPrestamosModel.setRowCount(0);
            for (Prestamo p : prestamos) {
                Object[] fila = {p.getId(), p.getLibro().getTitulo(), p.getFechaPrestamo(), p.getFechaDevolucion()};
                tablaPrestamosModel.addRow(fila);
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
        return buscarLibroTextField.getText().trim();
    }

    public String getCategoriaSeleccionada() {
        return (String) categoriaComboBox.getSelectedItem();
    }

    public void addBusquedaDocumentListener(Runnable accion) {
        buscarLibroTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
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

    public void addSolicitarButtonListener(ActionListener listener) {
        solicitarPrestamoButton.addActionListener(listener);
    }

    public void addDevolverButtonListener(ActionListener listener) {
        devolverLibroButton.addActionListener(listener);
    }

    public void addCerrarSesionButtonListener(ActionListener listener) {
        cerrarSesionButton.addActionListener(listener);
    }

    public int getFilaSeleccionadaLibro() {
        return librosTable.getSelectedRow();
    }

    public Integer getFilaSeleccionadaPrestamo() {
        // ?
        return prestamosTable.getSelectedRow() != -1 ? prestamosTable.getSelectedRow() : null;
    }
}
