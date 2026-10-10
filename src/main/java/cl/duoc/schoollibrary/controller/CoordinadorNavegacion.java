package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.controller.bibliotecario.ControladorPrincipalBibliotecario;
import cl.duoc.schoollibrary.controller.estudiante.ControladorPortalEstudiante;
import cl.duoc.schoollibrary.model.dao.UsuarioDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Usuario;
import cl.duoc.schoollibrary.view.VentanaLogin;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaPrincipalBibliotecario;
import cl.duoc.schoollibrary.view.estudiante.VentanaPortalEstudiante;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Orquestador central del ciclo de vida y navegación de la suite de software.
 * Centraliza las transiciones y la inyección de dependencias de nivel raíz.
 */
public final class CoordinadorNavegacion {

    private static CoordinadorNavegacion instancia;
    private JFrame ventanaActiva;

    private CoordinadorNavegacion() {
    }

    public static synchronized CoordinadorNavegacion getInstancia() {
        if (instancia == null) {
            instancia = new CoordinadorNavegacion();
        }
        return instancia;
    }

    /**
     * Levanta un Login fresco limpiando cualquier rastro de pantallas previas.
     */
    public void mostrarLogin() {
        SwingUtilities.invokeLater(() -> {
            destruirVentanaActiva();

            VentanaLogin vistaLogin = new VentanaLogin();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            new ControladorLogin(vistaLogin, usuarioDAO);

            cerrarProcesos(vistaLogin);
            ventanaActiva = vistaLogin;
            ventanaActiva.setVisible(true);
        });
    }

    /**
     * Cierra la ventana activa e hidrata el portal del alumno.
     */
    public void mostrarPortalEstudiante(Estudiante alumnoLogueado) {
        SwingUtilities.invokeLater(() -> {
            destruirVentanaActiva();

            VentanaPortalEstudiante vistaEstudiante = new VentanaPortalEstudiante();
            new ControladorPortalEstudiante(vistaEstudiante, alumnoLogueado);

            cerrarProcesos(vistaEstudiante);
            ventanaActiva = vistaEstudiante;
            ventanaActiva.setVisible(true);
        });
    }

    /**
     * Cierra la ventana activa e hidrata la consola administrativa.
     */
    public void mostrarPrincipalBibliotecario(Usuario sesion) {
        SwingUtilities.invokeLater(() -> {
            destruirVentanaActiva();

            VentanaPrincipalBibliotecario vistaAdmin = new VentanaPrincipalBibliotecario();
            vistaAdmin.establecerMensajeFondo(sesion.getNombre());
            new ControladorPrincipalBibliotecario(vistaAdmin);

            cerrarProcesos(vistaAdmin);
            ventanaActiva = vistaAdmin;
            ventanaActiva.setVisible(true);
        });
    }

    private void destruirVentanaActiva() {
        if (ventanaActiva != null) {
            ventanaActiva.dispose();
            ventanaActiva = null;
        }
    }

    private void cerrarProcesos(JFrame ventana) {
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AppThreadPool.shutdown();
            }
        });
    }
}
