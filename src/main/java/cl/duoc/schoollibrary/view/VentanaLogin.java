package cl.duoc.schoollibrary.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Pantalla raíz e interfaz de autenticación para el sistema de biblioteca.
 * Provee una tarjeta central estilizada para el ingreso seguro de credenciales
 * aislando por completo la interacción del hilo EDT de Swing.
 */
public class VentanaLogin extends VentanaBase {

    private JLabel tituloLabel;
    private JTextField correoTextField;
    private JPasswordField contraseniaPasswordField;
    private JButton ingresarButton;

    public VentanaLogin() {
        super("Biblioteca Escolar - Autenticación", 450, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Biblioteca Escolar", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 24));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));

        correoTextField = new JTextField();
        correoTextField.setFont(new Font("Arial", Font.PLAIN, 14));

        contraseniaPasswordField = new JPasswordField();
        contraseniaPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));

        ingresarButton = new JButton("Ingresar al Sistema");
        ingresarButton.setFont(new Font("Arial", Font.BOLD, 14));
        ingresarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);

        JPanel tarjetaCentral = new JPanel(new GridBagLayout());
        tarjetaCentral.setBorder(BorderFactory.createEmptyBorder(10, 40, 25, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setFont(new Font("Arial", Font.BOLD, 13));
        tarjetaCentral.add(lblCorreo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        tarjetaCentral.add(correoTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblContrasenia = new JLabel("Contraseña:");
        lblContrasenia.setFont(new Font("Arial", Font.BOLD, 13));
        tarjetaCentral.add(lblContrasenia, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        tarjetaCentral.add(contraseniaPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 8, 8, 8);
        tarjetaCentral.add(ingresarButton, gbc);

        add(tarjetaCentral, BorderLayout.CENTER);
    }

    public String getCorreoInput() {
        return correoTextField.getText().trim();
    }

    public String getContraseniaInput() {
        return new String(contraseniaPasswordField.getPassword());
    }

    public void addIngresarButtonListener(ActionListener listener) {
        ingresarButton.addActionListener(listener);
    }

    public void limpiarFormulario() {
        SwingUtilities.invokeLater(() -> {
            correoTextField.setText("");
            contraseniaPasswordField.setText("");
            correoTextField.requestFocus();
        });
    }
}
