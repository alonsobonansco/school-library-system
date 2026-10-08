package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.dao.UsuarioDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Rol;
import cl.duoc.schoollibrary.model.entity.Usuario;
import cl.duoc.schoollibrary.view.VentanaLogin;
import cl.duoc.schoollibrary.view.VentanaPortalEstudiante;
import cl.duoc.schoollibrary.view.VentanaPrincipalBibliotecario;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class ControladorLogin {

    private final VentanaLogin ventanaLogin;
    private final UsuarioDAO usuarioDAO;
    private EstudianteDAO estudianteDAO;

    public ControladorLogin(VentanaLogin ventanaLogin, UsuarioDAO usuarioDAO) {
        this.ventanaLogin = ventanaLogin;
        this.usuarioDAO = usuarioDAO;

        this.ventanaLogin.addIngresarButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarAutenticacion();
            }
        });
    }

    private void procesarAutenticacion() {
        String correo = ventanaLogin.getCorreoInput();
        String contrasenia = ventanaLogin.getContraseniaInput();

        if (correo.isBlank() || contrasenia.isBlank()) {
            ventanaLogin.mostrarMensajeError("Por favor, complete todos los campos");
            return;
        }

        new Thread(() -> {
            try {
                List<Usuario> usuarios = usuarioDAO.read();

                Optional<Usuario> usuarioEncontrado = usuarios.stream()
                        .filter(u -> u.getCorreo().trim().equalsIgnoreCase(correo.trim())
                                && u.getContrasenia().trim().equals(contrasenia.trim()))
                        .findFirst();

                if (usuarioEncontrado.isPresent()) {
                    Usuario usuario = usuarioEncontrado.get();

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        ventanaLogin.mostrarMensajeConfirmacion("¡Acceso concedido!\nBienvenido " +
                                "al Sistema," + usuario.getNombre());

                        if (usuario.getRol() == Rol.bibliotecario) {
                            VentanaPrincipalBibliotecario ventanaPrincipalBibliotecario
                                    = new VentanaPrincipalBibliotecario();
                            ventanaPrincipalBibliotecario.establecerMensajeBienvenida(usuario.getNombre());
                            ventanaPrincipalBibliotecario.setVisible(true);

                        } else {
                            try {
                                EstudianteDAO estudianteDAO = new EstudianteDAO();
                                List<Estudiante> estudiantes = estudianteDAO.read();

                                Optional<Estudiante> estudianteEncontrado = estudiantes.stream()
                                        .filter(e -> e.getCorreo().
                                                trim().equalsIgnoreCase(usuario.getCorreo().trim()))
                                        .findFirst();

                                if (estudianteEncontrado.isPresent()) {
                                    Estudiante estudiante = estudianteEncontrado.get();
                                    VentanaPortalEstudiante ventanaPortalEstudiante = new VentanaPortalEstudiante();
                                    ventanaPortalEstudiante.establecerNombreUsuario(estudiante.getNombre());

                                    new ControladorPortalEstudiante(ventanaPortalEstudiante, estudiante);
                                    ventanaPortalEstudiante.setVisible(true);
                                }
                            } catch (DataAccessException ex) {
                                ventanaLogin.mostrarMensajeError("Error al acceder a la base de datos: " + ex.getMessage());
                            }
                        }

                        ventanaLogin.cerrarVentana();
                    });
                } else {
                    ventanaLogin.mostrarMensajeError("Correo o contraseña incorrectos");
                    ventanaLogin.limpiarFormulario();
                }

            } catch (DataAccessException ex) {
                ventanaLogin.mostrarMensajeError("Error al acceder a la base de datos: " + ex.getMessage());
            }
        }).start();
    }
}
