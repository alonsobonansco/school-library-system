package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import java.util.List;

/**
 * Formulario modular de registro y modificación de ejemplares del catálogo bibliográfico.
 * Implementa una distribución matemática basada en GridBagLayout para estructurar
 * las cajas de texto relacionales e incorpora capacidades duales de prellenado para actuar
 * de forma polimórfica en flujos tanto de creación de nuevos registros como de edición.
 */
public class VentanaRegistroLibro extends VentanaBase {

    private JLabel tituloLabel;
    private JTextField tituloTextField;
    private JTextField autorTextField;
    private JTextField isbnTextField;
    private JTextField editorialTextField;
    private JTextField stockTextField;
    private JComboBox<String> categoriaComboBox;
    private JButton guardarButton;
    private JButton atrasButton;

    public VentanaRegistroLibro() {
        super("Registro de Libro", 450, 350);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Información del Libro", SwingConstants.CENTER);
        tituloLabel.setFont(FUENTE_TITULO);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        tituloTextField = new JTextField(15);
        tituloTextField.setFont(FUENTE_CAMPO);
        autorTextField = new JTextField(15);
        autorTextField.setFont(FUENTE_CAMPO);
        isbnTextField = new JTextField(15);
        isbnTextField.setFont(FUENTE_CAMPO);
        editorialTextField = new JTextField(15);
        editorialTextField.setFont(FUENTE_CAMPO);
        stockTextField = new JTextField(15);
        stockTextField.setFont(FUENTE_CAMPO);

        categoriaComboBox = new JComboBox<>();
        categoriaComboBox.setFont(FUENTE_CAMPO);

        guardarButton = new JButton("Guardar");
        guardarButton.setFont(FUENTE_BOTON);
        guardarButton.setCursor(CURSOR_CLICK);

        atrasButton = new JButton("Atrás");
        atrasButton.setFont(FUENTE_BOTON);
        atrasButton.setCursor(CURSOR_CLICK);
    }

    private void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.weightx = 1.0;

        agregarComponenteForm(panelFormulario, new JLabel("Título:"), gbc, 0, 0, FUENTE_LABEL);
        gbc.gridx = 1;
        panelFormulario.add(tituloTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Autor:"), gbc, 0, 1, FUENTE_LABEL);
        gbc.gridx = 1;
        panelFormulario.add(autorTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("ISBN:"), gbc, 0, 2, FUENTE_LABEL);
        gbc.gridx = 1;
        panelFormulario.add(isbnTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Editorial:"), gbc, 0, 3, FUENTE_LABEL);
        gbc.gridx = 1;
        panelFormulario.add(editorialTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Stock:"), gbc, 0, 4, FUENTE_LABEL);
        gbc.gridx = 1;
        panelFormulario.add(stockTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Categoría:"), gbc, 0, 5, FUENTE_LABEL);
        gbc.gridx = 1;
        panelFormulario.add(categoriaComboBox, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(guardarButton);
        panelBotones.add(atrasButton);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void agregarComponenteForm(JPanel panel, JLabel label, GridBagConstraints gbc, int x, int y, Font fuente) {
        gbc.gridx = x;
        gbc.gridy = y;
        label.setFont(fuente);
        panel.add(label, gbc);
    }

    public String getTituloInput() {
        return tituloTextField.getText().trim();
    }

    public String getAutorInput() {
        return autorTextField.getText().trim();
    }

    public String getIsbnInput() {
        return isbnTextField.getText().trim();
    }

    public String getEditorialInput() {
        return editorialTextField.getText().trim();
    }

    public String getStockInput() {
        return stockTextField.getText().trim();
    }

    public String getCategoriaSeleccionada() {
        return (String) categoriaComboBox.getSelectedItem();
    }

    public void poblarCategorias(List<String> categorias) {
        categoriaComboBox.removeAllItems();
        for (String categoria : categorias) {
            categoriaComboBox.addItem(categoria);
        }
    }

    public void prellenarFormulario(String titulo, String autor, String isbn,
                                    String editorial, String stock, String categoria) {
        tituloTextField.setText(titulo);
        autorTextField.setText(autor);
        isbnTextField.setText(isbn);
        editorialTextField.setText(editorial);
        stockTextField.setText(stock);
        categoriaComboBox.setSelectedItem(categoria);
    }

    public void addGuardarListener(ActionListener listener) {
        guardarButton.addActionListener(listener);
    }

    public void addVolverAtrasListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
    }

    public void setFormularioHabilitado(boolean habilitado) {
        guardarButton.setEnabled(habilitado);
        atrasButton.setEnabled(habilitado);
    }
}
