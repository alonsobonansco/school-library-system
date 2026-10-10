package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.CoordinadorNavegacion;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.view.bibliotecario.*;

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
        ventanaPrincipalBibliotecario.addReportesMenuListener(e -> abrirVentanaReportes());
        ventanaPrincipalBibliotecario.addCerrarSesionMenuListener(e -> procesarCerrarSesion());
    }

    private void abrirVentanaLibros() {
        VentanaInventarioLibros ventanaInventarioLibros = new VentanaInventarioLibros();
        LibroDAO libroDAO = new LibroDAO();
        new ControladorInventarioLibros(ventanaInventarioLibros, libroDAO);
        ventanaInventarioLibros.setVisible(true);
    }

    private void abrirVentanaEstudiantes() {
        VentanaGestionEstudiantes ventanaGestionEstudiantes = new VentanaGestionEstudiantes();
        EstudianteDAO estudianteDAO = new EstudianteDAO();
        new ControladorGestionEstudiantes(ventanaGestionEstudiantes, estudianteDAO);
        ventanaGestionEstudiantes.setVisible(true);
    }

    private void abrirVentanaPrestamos() {
        VentanaControlPrestamos ventanaControlPrestamos = new VentanaControlPrestamos();
        new ControladorControlPrestamos(ventanaControlPrestamos);
        ventanaControlPrestamos.setVisible(true);
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
