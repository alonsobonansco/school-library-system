package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Formulario modal de asignación y registro de nuevos préstamos bibliotecarios.
 * Implementa una distribución matemática basada en GridBagLayout para estructurar
 * las cajas de selección relacional de alumnos y libros, incorporando métodos semánticos
 * para congelar los gatillos de envío durante los procesos asíncronos de persistencia.
 */
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
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 45, 10, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(6, 0, 4, 0);
        JLabel alumnoLabel = new JLabel("Estudiante:");
        alumnoLabel.setFont(FUENTE_LABEL);
        panelFormulario.add(alumnoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        panelFormulario.add(estudiantesComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel librosLabel = new JLabel("Libro:");
        librosLabel.setFont(FUENTE_LABEL);
        panelFormulario.add(librosLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
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
