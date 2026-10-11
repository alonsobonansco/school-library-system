package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Formulario modular de registro y modificación de perfiles de estudiantes.
 * Implementa una distribución basada en GridBagLayout para alinear de forma simétrica
 * los campos de entrada de texto e incorpora capacidades de prellenado para actuar
 * de forma polimórfica tanto en flujos de creación como de edición.
 */
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
        rutTextField = new JTextField(15);
        rutTextField.setFont(FUENTE_CAMPO);

        nombreTextField = new JTextField(15);
        nombreTextField.setFont(FUENTE_CAMPO);

        cursoTextField = new JTextField(15);
        cursoTextField.setFont(FUENTE_CAMPO);

        correoTextField = new JTextField(15);
        correoTextField.setFont(FUENTE_CAMPO);

        guardarButton = new JButton("Guardar");
        guardarButton.setFont(FUENTE_BOTON);
        guardarButton.setCursor(CURSOR_CLICK);

        atrasButton = new JButton("Atrás");
        atrasButton.setFont(FUENTE_BOTON);
        atrasButton.setCursor(CURSOR_CLICK);
    }

    private void construirLayout() {
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.weightx = 1.0;

        agregarComponenteForm(panelFormulario, new JLabel("RUT:"), gbc, 0, 0, FUENTE_LABEL);
        gbc.gridx = 1;
        panelFormulario.add(rutTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Nombre Completo:"), gbc, 0, 1, FUENTE_LABEL);
        gbc.gridx = 1;
        panelFormulario.add(nombreTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Curso:"), gbc, 0, 2, FUENTE_LABEL);
        gbc.gridx = 1;
        panelFormulario.add(cursoTextField, gbc);

        agregarComponenteForm(panelFormulario, new JLabel("Correo Electrónico:"), gbc, 0, 3, FUENTE_LABEL);
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

    public void setFormularioHabilitado(boolean habilitado) {
        guardarButton.setEnabled(habilitado);
        atrasButton.setEnabled(habilitado);
    }
}
