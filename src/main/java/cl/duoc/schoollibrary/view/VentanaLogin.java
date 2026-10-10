package cl.duoc.schoollibrary.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VentanaLogin extends VentanaBase {

    private JLabel tituloLabel;
    private JTextField correoTextField;
    private JPasswordField contraseniaPasswordField;
    private JButton ingresarButton;

    public VentanaLogin() {
        super("Biblioteca Escolar - Autenticación", 450, 350);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Biblioteca Escolar", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 24));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));

        Font fuente = new Font("Arial", Font.PLAIN, 14);

        correoTextField = new JTextField();
        correoTextField.setFont(fuente);

        contraseniaPasswordField = new JPasswordField();
        contraseniaPasswordField.setFont(fuente);

        ingresarButton = new JButton("Ingresar al Sistema");
        ingresarButton.setFont(fuente);
        // Para que al pasar el mouse sobre el botón cambie el cursor a una mano, indicando que es clickeable
        ingresarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 40, 25, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel correoLabel = new JLabel("Correo Electrónico:");
        correoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        panelCentral.add(correoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCentral.add(correoTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel contraseniaLabel = new JLabel("Contraseña:");
        contraseniaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        panelCentral.add(contraseniaLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelCentral.add(contraseniaPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 8, 8, 8);
        panelCentral.add(ingresarButton, gbc);

        add(panelCentral, BorderLayout.CENTER);
    }

    public String getCorreoInput() {
        return correoTextField.getText().trim();
    }

    public String getContraseniaInput() {
        return new String(contraseniaPasswordField.getPassword());
    }

    public void addIngresarListener(ActionListener listener) {
        ingresarButton.addActionListener(listener);
    }

    public void limpiarFormulario() {
        correoTextField.setText("");
        contraseniaPasswordField.setText("");
        correoTextField.requestFocus();
    }
}
