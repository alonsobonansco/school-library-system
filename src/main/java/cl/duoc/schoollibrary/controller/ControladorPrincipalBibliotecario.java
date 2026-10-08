package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.view.VentanaGestionEstudiantes;
import cl.duoc.schoollibrary.view.VentanaInventarioLibros;
import cl.duoc.schoollibrary.view.VentanaPrincipalBibliotecario;

public class ControladorPrincipalBibliotecario {

    private final VentanaPrincipalBibliotecario ventanaPrincipalBibliotecario;

    public ControladorPrincipalBibliotecario(VentanaPrincipalBibliotecario ventanaPrincipalBibliotecario) {
        this.ventanaPrincipalBibliotecario = ventanaPrincipalBibliotecario;
        inicializarListeners();
    }

    private void inicializarListeners() {
        ventanaPrincipalBibliotecario.addLibrosMenuListener(e -> abrirVentanaLibros());
        ventanaPrincipalBibliotecario.addEstudiantesMenuListener(e -> abrirVentanaEstudiantes());
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
}
