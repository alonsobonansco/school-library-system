package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.CoordinadorNavegacion;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.view.bibliotecario.*;

public class ControladorPortalBibliotecario {

    private final VentanaPortalBibliotecario ventanaPortalBibliotecario;

    public ControladorPortalBibliotecario(VentanaPortalBibliotecario ventanaPortalBibliotecario) {
        this.ventanaPortalBibliotecario = ventanaPortalBibliotecario;
        inicializarListeners();
    }

    private void inicializarListeners() {
        ventanaPortalBibliotecario.addLibrosMenuListener(e -> abrirVentanaLibros());
        ventanaPortalBibliotecario.addEstudiantesMenuListener(e -> abrirVentanaEstudiantes());
        ventanaPortalBibliotecario.addPrestamosMenuListener(e -> abrirVentanaPrestamos());
        ventanaPortalBibliotecario.addReportesMenuListener(e -> abrirVentanaReportes());
        ventanaPortalBibliotecario.addCerrarSesionMenuListener(e -> procesarCerrarSesion());
    }

    private void abrirVentanaLibros() {
        VentanaGestionLibros ventanaGestionLibros = new VentanaGestionLibros();
        LibroDAO libroDAO = new LibroDAO();
        new ControladorGestionLibros(ventanaGestionLibros, libroDAO);
        ventanaGestionLibros.setVisible(true);
    }

    private void abrirVentanaEstudiantes() {
        VentanaGestionEstudiantes ventanaGestionEstudiantes = new VentanaGestionEstudiantes();
        EstudianteDAO estudianteDAO = new EstudianteDAO();
        new ControladorGestionEstudiantes(ventanaGestionEstudiantes, estudianteDAO);
        ventanaGestionEstudiantes.setVisible(true);
    }

    private void abrirVentanaPrestamos() {
        VentanaGestionPrestamos ventanaGestionPrestamos = new VentanaGestionPrestamos();
        new ControladorGestionPrestamos(ventanaGestionPrestamos);
        ventanaGestionPrestamos.setVisible(true);
    }

    private void abrirVentanaReportes() {
        VentanaReportes ventanaReportes = new VentanaReportes();
        new ControladorReportes(ventanaReportes);
        ventanaReportes.setVisible(true);
    }

    private void procesarCerrarSesion() {
        CoordinadorNavegacion.getInstancia().mostrarLogin();
    }
}
