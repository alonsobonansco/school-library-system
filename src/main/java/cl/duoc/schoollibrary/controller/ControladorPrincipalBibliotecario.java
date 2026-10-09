package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.dao.PrestamoDAO;
import cl.duoc.schoollibrary.view.*;

public class ControladorPrincipalBibliotecario {

    private final VentanaPrincipalBibliotecario ventanaPrincipalBibliotecario;

    public ControladorPrincipalBibliotecario(VentanaPrincipalBibliotecario ventanaPrincipalBibliotecario) {
        this.ventanaPrincipalBibliotecario = ventanaPrincipalBibliotecario;
        inicializarListeners();
    }

    private void inicializarListeners() {
        ventanaPrincipalBibliotecario.addLibrosMenuListener(e -> abrirVentanaLibros());
        ventanaPrincipalBibliotecario.addEstudiantesMenuListener(e -> abrirVentanaEstudiantes());
        ventanaPrincipalBibliotecario.addPrestamosMenuListener(e -> abrirVentanaPrestamos());

        ventanaPrincipalBibliotecario.addCerrarSesionMenuListener(e -> procesarCerrarSesion());
    }

    private void abrirVentanaLibros() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            VentanaInventarioLibros ventanaInventarioLibros = new VentanaInventarioLibros();
            LibroDAO libroDAO = new LibroDAO();

            new ControladorInventarioLibros(ventanaInventarioLibros, libroDAO);
            ventanaInventarioLibros.setVisible(true);
        });
    }

    private void abrirVentanaEstudiantes() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            VentanaGestionEstudiantes ventanaGestionEstudiantes = new VentanaGestionEstudiantes();
            EstudianteDAO estudianteDAO = new EstudianteDAO();

            new ControladorGestionEstudiantes(ventanaGestionEstudiantes, estudianteDAO);
            ventanaGestionEstudiantes.setVisible(true);
        });
    }

    private void abrirVentanaPrestamos() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            VentanaControlPrestamos ventanaControlPrestamos = new VentanaControlPrestamos();

            new ControladorControlPrestamos(ventanaControlPrestamos);
            ventanaControlPrestamos.setVisible(true);
        });
    }

    private void procesarCerrarSesion() {
        CoordinadorNavegacion.getInstancia().mostrarLogin();
    }
}
