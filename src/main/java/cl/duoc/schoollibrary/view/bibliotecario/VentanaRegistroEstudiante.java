package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VentanaRegistroEstudiante extends VentanaBase {

    private JTextField rutTextField;
    private JTextField nombreTextField;
    private JTextField cursoTextField;
    private JTextField correoTextField;

    private JButton guardarButton;
    private JButton atrasButton;

    public VentanaRegistroEstudiante() {
        super("Registro de Estudiante", 450, 350);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        Font fuenteCampos = new Font("Arial", Font.PLAIN, 12);

        rutTextField = new JTextField(15);
        rutTextField.setFont(fuenteCampos);

        nombreTextField = new JTextField(15);
        nombreTextField.setFont(fuenteCampos);

        cursoTextField = new JTextField(15);
        cursoTextField.setFont(fuenteCampos);

        correoTextField = new JTextField(15);
        correoTextField.setFont(fuenteCampos);

        Font fuenteBotones = new Font("Arial", Font.BOLD, 12);
        guardarButton = new JButton("Guardar");
        guardarButton.setFont(fuenteBotones);
        guardarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        atrasButton = new JButton("Atrás");
        atrasButton.setFont(fuenteBotones);
        atrasButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        panelFormulario.add(rutTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Nombre Completo:"), gbc, 0, 1, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(nombreTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Curso:"), gbc, 0, 2, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(cursoTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Correo Electrónico:"), gbc, 0, 3, fuenteLabels);
        gbc.gridx = 1;
        panelFormulario.add(correoTextField, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
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

    public String getRutInput() {
        return rutTextField.getText().trim();
    }

    public String getNombreInput() {
        return nombreTextField.getText().trim();
    }

    public String getCursoInput() {
        return cursoTextField.getText().trim();
    }

    public String getCorreoInput() {
        return correoTextField.getText().trim();
    }

    public void prellenarFormulario(String rut, String nombre, String curso, String correo) {
        rutTextField.setText(rut);
        nombreTextField.setText(nombre);
        cursoTextField.setText(curso);
        correoTextField.setText(correo);
    }

    public void addGuardarListener(ActionListener listener) {
        guardarButton.addActionListener(listener);
    }

    public void addVolverAtrasListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
    }
}
