package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.controller.bibliotecario.ControladorPortalBibliotecario;
import cl.duoc.schoollibrary.controller.estudiante.ControladorPortalEstudiante;
import cl.duoc.schoollibrary.model.dao.UsuarioDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Usuario;
import cl.duoc.schoollibrary.view.VentanaLogin;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaPortalBibliotecario;
import cl.duoc.schoollibrary.view.estudiante.VentanaPortalEstudiante;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Orquestador central del ciclo de vida y navegación de la suite de software.
 * Centraliza las transiciones, gestiona la ventana activa en memoria RAM
 * e inyecta las dependencias de nivel raíz bajo el patrón Singleton.
 */
public final class CoordinadorNavegacion {

    private static CoordinadorNavegacion instancia;
    private JFrame ventanaActiva;

    private CoordinadorNavegacion() {
    }

    /**
     * Retorna la instancia única del coordinador de rutas globales.
     */
    public static synchronized CoordinadorNavegacion getInstancia() {
        if (instancia == null) {
            instancia = new CoordinadorNavegacion();
        }
        return instancia;
    }

    /**
     * Levanta el formulario de Login de forma segura en el EDT.
     * Destruye de la memoria RAM cualquier ventana previa activa, instancia los componentes
     * de autenticación iniciales y acopla el gancho de cierre definitivo de la JVM.
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
     * Transiciona el contexto de ejecución hacia el Portal del Alumno.
     * Descarga la pantalla anterior e hidrata de forma asíncrona la vista principal del estudiante
     * inyectándole los privilegios y metadatos del registro de sesión correspondiente.
     *
     * @param alumnoLogueado Entidad del estudiante autenticado que gobernará las búsquedas y préstamos.
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
     * Transiciona el contexto de ejecución hacia la Consola Administrativa.
     * Cierra el Login, levanta el panel del Bibliotecario e inicializa los sub-controladores
     * de gestión interna para libros, alumnos e historiales analíticos.
     *
     * @param sesion Entidad de usuario administrativo logueado en la plataforma.
     */
    public void mostrarPrincipalBibliotecario(Usuario sesion) {
        SwingUtilities.invokeLater(() -> {
            destruirVentanaActiva();

            VentanaPortalBibliotecario vistaAdmin = new VentanaPortalBibliotecario();
            vistaAdmin.establecerMensajeFondo(sesion.getNombre());
            new ControladorPortalBibliotecario(vistaAdmin);

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
