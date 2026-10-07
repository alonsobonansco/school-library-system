package cl.duoc.schoollibrary.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import java.util.List;

public class VentanaRegistroLibro extends VentanaBase {

    private JLabel tituloLabel; //NECESARIO?
    private JTextField tituloTextField;
    private JTextField autorTextField;
    private JTextField isbnTextField;
    private JTextField categoriaTextField;
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
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        Font fuenteCampos = new Font("Arial", Font.PLAIN, 12);
        tituloTextField = new JTextField(15);
        tituloTextField.setFont(fuenteCampos);
        autorTextField = new JTextField(15);
        autorTextField.setFont(fuenteCampos);
        isbnTextField = new JTextField(15);
        isbnTextField.setFont(fuenteCampos);
        categoriaTextField = new JTextField(15);
        categoriaTextField.setFont(fuenteCampos);
        editorialTextField = new JTextField(15);
        editorialTextField.setFont(fuenteCampos);
        stockTextField = new JTextField(15);
        stockTextField.setFont(fuenteCampos);

        categoriaComboBox = new JComboBox<>();
        categoriaComboBox.setFont(fuenteCampos);

        Font fuenteBotones = new Font("Arial", Font.BOLD, 12);
        guardarButton = new JButton("Guardar");
        guardarButton.setFont(fuenteBotones);
        guardarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        atrasButton = new JButton("Atrás");
        atrasButton.setFont(fuenteBotones);
        atrasButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 8, 4, 8); // Reducimos el espaciado para que respire en 350px de alto
        gbc.weightx = 1.0;

        Font fuenteLabels = new Font("Arial", Font.BOLD, 12);

        agregarComponenteForm(panelFormulario, new JLabel("Título:"), gbc, 0, 0, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(tituloTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Autor:"), gbc, 0, 1, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(autorTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("ISBN:"), gbc, 0, 2, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(isbnTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Editorial:"), gbc, 0, 3, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(editorialTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Stock:"), gbc, 0, 4, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(stockTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Categoría:"), gbc, 0, 5, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(categoriaComboBox, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(guardarButton);
        panelBotones.add(atrasButton);
        add(panelBotones, BorderLayout.SOUTH);

        atrasButton.addActionListener(e -> cerrarVentana());
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
        SwingUtilities.invokeLater(() -> {
            categoriaComboBox.removeAllItems();
            for (String cat : categorias) {
                categoriaComboBox.addItem(cat);
            }
        });
    }

    public void prellenarFormulario(String titulo, String autor, String isbn, String editorial, String stock, String categoria) {
        SwingUtilities.invokeLater(() -> {
            tituloTextField.setText(titulo);
            autorTextField.setText(autor);
            isbnTextField.setText(isbn);
            editorialTextField.setText(editorial);
            stockTextField.setText(stock);
            categoriaComboBox.setSelectedItem(categoria);
        });
    }

    public void addGuardarListener(ActionListener listener) {
        guardarButton.addActionListener(listener);
    }
}
