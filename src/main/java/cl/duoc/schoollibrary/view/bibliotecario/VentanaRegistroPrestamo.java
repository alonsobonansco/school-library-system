package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;


public class VentanaRegistroPrestamo extends VentanaBase {

    private JComboBox<Estudiante> comboEstudiantes;
    private JComboBox<Libro> comboLibros;
    private JButton btnGuardar;
    private JButton btnAtras;

    public VentanaRegistroPrestamo() {
        super("Asignación de Nuevo Préstamo", 480, 280);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        comboEstudiantes = new JComboBox<>();
        comboEstudiantes.setFont(new Font("Arial", Font.PLAIN, 12));

        comboLibros = new JComboBox<>();
        comboLibros.setFont(new Font("Arial", Font.PLAIN, 12));

        Font fuenteBotones = new Font("Arial", Font.BOLD, 12);

        btnGuardar = new JButton("Registrar Préstamo");
        btnGuardar.setFont(fuenteBotones);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAtras = new JButton("Cancelar");
        btnAtras.setFont(fuenteBotones);
        btnAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void construirLayout() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 35, 10, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.weightx = 1.0;

        Font fuenteLabels = new Font("Arial", Font.BOLD, 12);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblEst = new JLabel("Seleccionar Alumno:");
        lblEst.setFont(fuenteLabels);
        panelForm.add(lblEst, gbc);
        gbc.gridx = 1;
        panelForm.add(comboEstudiantes, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblLib = new JLabel("Seleccionar Libro:");
        lblLib.setFont(fuenteLabels);
        panelForm.add(lblLib, gbc);
        gbc.gridx = 1;
        panelForm.add(comboLibros, gbc);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        panelAcciones.add(btnGuardar);
        panelAcciones.add(btnAtras);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    public void poblarComboEstudiantes(List<Estudiante> estudiantes) {
        comboEstudiantes.removeAllItems();
        for (Estudiante e : estudiantes) comboEstudiantes.addItem(e);

    }

    public void poblarComboLibros(List<Libro> libros) {
        comboLibros.removeAllItems();
        for (Libro l : libros) comboLibros.addItem(l);

    }

    public JComboBox<Estudiante> getComboEstudiantes() {
        return comboEstudiantes;
    }

    public JComboBox<Libro> getComboLibros() {
        return comboLibros;
    }

    public void addGuardarListener(ActionListener listener) {
        btnGuardar.addActionListener(listener);
    }

    public void addVolverAtrasListener(ActionListener listener) {
        btnAtras.addActionListener(listener);
    }
}
