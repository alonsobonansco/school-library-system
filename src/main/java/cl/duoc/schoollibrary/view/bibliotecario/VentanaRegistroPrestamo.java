package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;


public class VentanaRegistroPrestamo extends VentanaBase {

    private JComboBox<Estudiante> estudiantesComboBox;
    private JComboBox<Libro> librosComboBox;
    private JButton guardarButton;
    private JButton atrasButton;

    public VentanaRegistroPrestamo() {
        super("Asignación de Nuevo Préstamo", 480, 280);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        estudiantesComboBox = new JComboBox<>();
        estudiantesComboBox.setFont(FUENTE_CAMPO);

        librosComboBox = new JComboBox<>();
        librosComboBox.setFont(FUENTE_CAMPO);

        guardarButton = new JButton("Registrar Préstamo");
        guardarButton.setFont(FUENTE_BOTON);
        guardarButton.setCursor(CURSOR_CLICK);

        atrasButton = new JButton("Cancelar");
        atrasButton.setFont(FUENTE_BOTON);
        atrasButton.setCursor(CURSOR_CLICK);
    }

    private void construirLayout() {
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 35, 10, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel alumnoLabel = new JLabel("Seleccionar Alumno:");
        alumnoLabel.setFont(FUENTE_LABEL);
        panelFormulario.add(alumnoLabel, gbc);
        gbc.gridx = 1;
        panelFormulario.add(estudiantesComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel librosLabel = new JLabel("Seleccionar Libro:");
        librosLabel.setFont(FUENTE_LABEL);
        panelFormulario.add(librosLabel, gbc);
        gbc.gridx = 1;
        panelFormulario.add(librosComboBox, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        panelBotones.add(guardarButton);
        panelBotones.add(atrasButton);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void poblarEstudiantes(List<Estudiante> estudiantes) {
        estudiantesComboBox.removeAllItems();
        for (Estudiante e : estudiantes) estudiantesComboBox.addItem(e);

    }

    public void poblarLibros(List<Libro> libros) {
        librosComboBox.removeAllItems();
        for (Libro l : libros) librosComboBox.addItem(l);

    }

    public JComboBox<Estudiante> getEstudiantesCombo() {
        return estudiantesComboBox;
    }

    public JComboBox<Libro> getLibrosCombo() {
        return librosComboBox;
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
