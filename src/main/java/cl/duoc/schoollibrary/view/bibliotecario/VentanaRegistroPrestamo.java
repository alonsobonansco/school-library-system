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
        estudiantesComboBox.setFont(new Font("Arial", Font.PLAIN, 12));

        librosComboBox = new JComboBox<>();
        librosComboBox.setFont(new Font("Arial", Font.PLAIN, 12));

        Font fuenteBotones = new Font("Arial", Font.BOLD, 12);

        guardarButton = new JButton("Registrar Préstamo");
        guardarButton.setFont(fuenteBotones);
        guardarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        atrasButton = new JButton("Cancelar");
        atrasButton.setFont(fuenteBotones);
        atrasButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 35, 10, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.weightx = 1.0;

        Font fuenteLabels = new Font("Arial", Font.BOLD, 12);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblEst = new JLabel("Seleccionar Alumno:");
        lblEst.setFont(fuenteLabels);
        panelFormulario.add(lblEst, gbc);
        gbc.gridx = 1;
        panelFormulario.add(estudiantesComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblLib = new JLabel("Seleccionar Libro:");
        lblLib.setFont(fuenteLabels);
        panelFormulario.add(lblLib, gbc);
        gbc.gridx = 1;
        panelFormulario.add(librosComboBox, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        panel.add(guardarButton);
        panel.add(atrasButton);
        add(panel, BorderLayout.SOUTH);
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
}
