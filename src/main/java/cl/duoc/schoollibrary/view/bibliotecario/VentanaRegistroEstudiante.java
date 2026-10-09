package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VentanaRegistroEstudiante extends VentanaBase {

    private JTextField txtRut;
    private JTextField txtNombre;
    private JTextField txtCurso;
    private JTextField txtCorreo;

    private JButton btnGuardar;
    private JButton btnAtras;

    public VentanaRegistroEstudiante() {
        super("Registro de Estudiante", 450, 350);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        Font fuenteCampos = new Font("Arial", Font.PLAIN, 12);

        txtRut = new JTextField(15);
        txtRut.setFont(fuenteCampos);

        txtNombre = new JTextField(15);
        txtNombre.setFont(fuenteCampos);

        txtCurso = new JTextField(15);
        txtCurso.setFont(fuenteCampos);

        txtCorreo = new JTextField(15);
        txtCorreo.setFont(fuenteCampos);

        Font fuenteBotones = new Font("Arial", Font.BOLD, 12);
        btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(fuenteBotones);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAtras = new JButton("Atrás");
        btnAtras.setFont(fuenteBotones);
        btnAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.weightx = 1.0;

        Font fuenteLabels = new Font("Arial", Font.BOLD, 12);

        agregarComponenteForm(panelFormulario, new JLabel("RUT:"), gbc, 0, 0, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(txtRut, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Nombre Completo:"), gbc, 0, 1, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(txtNombre, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Curso:"), gbc, 0, 2, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(txtCurso, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Correo Electrónico:"), gbc, 0, 3, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(txtCorreo, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnAtras);
        add(panelBotones, BorderLayout.SOUTH);

        btnAtras.addActionListener(e -> cerrarVentana());
    }

    private void agregarComponenteForm(JPanel panel, JLabel label, GridBagConstraints gbc, int x, int y, Font fuente) {
        gbc.gridx = x;
        gbc.gridy = y;
        label.setFont(fuente);
        panel.add(label, gbc);
    }

    public String getRutInput() {
        return txtRut.getText().trim();
    }

    public String getNombreInput() {
        return txtNombre.getText().trim();
    }

    public String getCursoInput() {
        return txtCurso.getText().trim();
    }

    public String getCorreoInput() {
        return txtCorreo.getText().trim();
    }

    public void prellenarFormulario(String rut, String nombre, String curso, String correo) {
        SwingUtilities.invokeLater(() -> {
            txtRut.setText(rut);
            txtNombre.setText(nombre);
            txtCurso.setText(curso);
            txtCorreo.setText(correo);
        });
    }

    public void addGuardarListener(ActionListener listener) {
        btnGuardar.addActionListener(listener);
    }
}
