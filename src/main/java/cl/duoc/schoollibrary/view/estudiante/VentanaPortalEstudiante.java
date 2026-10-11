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
 * Consola gráfica de autoservicio para el perfil Estudiante.
 * Implementa una distribución de paneles por pestañas (Tabbed Layout) para unificar
 * el catálogo reactivo de consultas bibliográficas y el panel operacional de préstamos.
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
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        inicializarComponentes();
        inicializarPestaniaCatalogo();
        inicializarPestaniaMisPrestamos();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Bienvenido al Portal de Autoservicio", SwingConstants.LEFT);
        tituloLabel.setFont(FUENTE_TITULO.deriveFont(16f));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        pestaniasPanel = new JTabbedPane();
        pestaniasPanel.setFont(FUENTE_BOTON.deriveFont(13f));

        cerrarSesionButton = new JButton("Cerrar Sesión");
        cerrarSesionButton.setFont(FUENTE_BOTON);
        cerrarSesionButton.setCursor(CURSOR_CLICK);
    }

    private void inicializarPestaniaCatalogo() {
        buscarLibroTextField = new JTextField(20);
        buscarLibroTextField.setFont(FUENTE_CAMPO.deriveFont(14f));
        categoriaComboBox = new JComboBox<>();
        categoriaComboBox.setFont(FUENTE_CAMPO.deriveFont(13f));

        String[] columnasLibros = {"Título", "Autor", "ISBN", "Categoría", "Disponibilidad"};
        tablaLibrosModel = new DefaultTableModel(columnasLibros, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        librosTable = new JTable(tablaLibrosModel);
        librosTable.setFont(FUENTE_CAMPO.deriveFont(13f));
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
        solicitarPrestamoButton.setFont(FUENTE_BOTON.deriveFont(13f));
        solicitarPrestamoButton.setCursor(CURSOR_CLICK);
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
        prestamosTable.setFont(FUENTE_CAMPO.deriveFont(13f));
        prestamosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prestamosTable.getTableHeader().setReorderingAllowed(false);
        prestamosTable.getTableHeader().setResizingAllowed(false);

        devolverLibroButton = new JButton("Devolver Libro");
        devolverLibroButton.setFont(FUENTE_BOTON.deriveFont(13f));
        devolverLibroButton.setCursor(CURSOR_CLICK);
    }

    private void construirLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(tituloLabel, BorderLayout.NORTH);

        JPanel panelCerrarSesion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        panelCerrarSesion.add(cerrarSesionButton);
        panelSuperior.add(panelCerrarSesion, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCatalogo = new JPanel(new BorderLayout(10, 10));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));

        JLabel buscarLabel = new JLabel("Buscar Libro / ISBN:");
        buscarLabel.setFont(FUENTE_LABEL.deriveFont(13f));
        panelFiltros.add(buscarLabel);
        panelFiltros.add(buscarLibroTextField);

        JLabel categoriaLabel = new JLabel("Categoría:");
        categoriaLabel.setFont(FUENTE_LABEL.deriveFont(13f));
        panelFiltros.add(categoriaLabel);
        panelFiltros.add(categoriaComboBox);

        panelCatalogo.add(panelFiltros, BorderLayout.NORTH);

        JScrollPane scrollLibros = new JScrollPane(librosTable);
        scrollLibros.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        panelCatalogo.add(scrollLibros, BorderLayout.CENTER);

        JPanel panelBotonCatalogo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotonCatalogo.add(solicitarPrestamoButton);
        panelCatalogo.add(panelBotonCatalogo, BorderLayout.SOUTH);

        JPanel panelPrestamos = new JPanel(new BorderLayout(10, 10));

        JScrollPane scrollPrestamos = new JScrollPane(prestamosTable);
        scrollPrestamos.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        panelPrestamos.add(scrollPrestamos, BorderLayout.CENTER);

        JPanel panelBotonPrestamos = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotonPrestamos.add(devolverLibroButton);
        panelPrestamos.add(panelBotonPrestamos, BorderLayout.SOUTH);

        pestaniasPanel.addTab("Consultar Catálogo", panelCatalogo);
        pestaniasPanel.addTab("Mis Préstamos Activos", panelPrestamos);

        add(pestaniasPanel, BorderLayout.CENTER);
    }

    public void establecerNombreUsuario(String nombre) {
        tituloLabel.setText("Bienvenido/a al Portal de Autoservicio: " + nombre);
    }

    public void actualizarTablaLibros(List<Libro> libros) {
        tablaLibrosModel.setRowCount(0);
        for (Libro l : libros) {
            String disponibilidad = (l.getStock() > 0) ? "DISPONIBLE (" + l.getStock() + " u)" : "AGOTADO";
            Object[] fila = {l.getTitulo(), l.getAutor(), l.getIsbn(), l.getCategoria().getNombre(), disponibilidad};
            tablaLibrosModel.addRow(fila);
        }
    }

    public void actualizarTablaPrestamos(List<Prestamo> prestamos) {
        tablaPrestamosModel.setRowCount(0);
        for (Prestamo p : prestamos) {
            Object[] fila = {p.getId(), p.getLibro().getTitulo(), p.getFechaPrestamo(), p.getFechaDevolucion()};
            tablaPrestamosModel.addRow(fila);
        }
    }

    public void poblarCategorias(List<String> nombresCategorias) {
        categoriaComboBox.removeAllItems();
        categoriaComboBox.addItem("Todas");
        for (String categoria : nombresCategorias) {
            categoriaComboBox.addItem(categoria);
        }
    }

    public String getTextoBusqueda() {
        return buscarLibroTextField.getText().trim();
    }

    public String getCategoriaSeleccionada() {
        return (String) categoriaComboBox.getSelectedItem();
    }

    public int getFilaSeleccionadaLibro() {
        return librosTable.getSelectedRow();
    }

    public int getFilaSeleccionadaPrestamo() {
        return prestamosTable.getSelectedRow();
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

    public void addSolicitarPrestamoListener(ActionListener listener) {
        solicitarPrestamoButton.addActionListener(listener);
    }

    public void addDevolverLibroListener(ActionListener listener) {
        devolverLibroButton.addActionListener(listener);
    }

    public void addCerrarSesionListener(ActionListener listener) {
        cerrarSesionButton.addActionListener(listener);
    }

    public void setFormularioHabilitado(boolean habilitado) {
        devolverLibroButton.setEnabled(habilitado);
    }
}
