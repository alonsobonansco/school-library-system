package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.CoordinadorNavegacion;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.view.bibliotecario.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Cerebro administrativo encargado de coordinar las rutas de navegación global
 * y la instanciación de consolas de gestión secundaria para el perfil Bibliotecario.
 * Bloquea mecánicamente el acceso a los sub-módulos durante las transiciones para impedir
 * la apertura redundante de ventanas duplicadas en la interfaz de usuario.
 */
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
        ventanaPortalBibliotecario.setMenuItemsHabilitados(false);
        VentanaGestionLibros ventanaGestionLibros = new VentanaGestionLibros();
        ventanaGestionLibros.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaPortalBibliotecario.setMenuItemsHabilitados(true);
            }
        });
        LibroDAO libroDAO = new LibroDAO();
        new ControladorGestionLibros(ventanaGestionLibros, libroDAO);
        ventanaGestionLibros.setVisible(true);
    }

    private void abrirVentanaEstudiantes() {
        ventanaPortalBibliotecario.setMenuItemsHabilitados(false);
        VentanaGestionEstudiantes ventanaGestionEstudiantes = new VentanaGestionEstudiantes();
        ventanaGestionEstudiantes.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaPortalBibliotecario.setMenuItemsHabilitados(true);
            }
        });
        EstudianteDAO estudianteDAO = new EstudianteDAO();
        new ControladorGestionEstudiantes(ventanaGestionEstudiantes, estudianteDAO);
        ventanaGestionEstudiantes.setVisible(true);
    }

    private void abrirVentanaPrestamos() {
        ventanaPortalBibliotecario.setMenuItemsHabilitados(false);
        VentanaGestionPrestamos ventanaGestionPrestamos = new VentanaGestionPrestamos();
        ventanaGestionPrestamos.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaPortalBibliotecario.setMenuItemsHabilitados(true);
            }
        });
        new ControladorGestionPrestamos(ventanaGestionPrestamos);
        ventanaGestionPrestamos.setVisible(true);
    }

    private void abrirVentanaReportes() {
        ventanaPortalBibliotecario.setMenuItemsHabilitados(false);
        VentanaReportes ventanaReportes = new VentanaReportes();
        ventanaReportes.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaPortalBibliotecario.setMenuItemsHabilitados(true);
            }
        });
        new ControladorReportes(ventanaReportes);
        ventanaReportes.setVisible(true);
    }

    private void procesarCerrarSesion() {
        CoordinadorNavegacion.getInstancia().mostrarLogin();
    }
}
