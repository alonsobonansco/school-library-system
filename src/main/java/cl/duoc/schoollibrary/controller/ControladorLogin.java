package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.dao.UsuarioDAO;
import cl.duoc.schoollibrary.model.entity.Rol;
import cl.duoc.schoollibrary.model.entity.Usuario;
import cl.duoc.schoollibrary.view.VentanaLogin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class ControladorLogin {

    private final VentanaLogin ventanaLogin;
    private final UsuarioDAO usuarioDAO;

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
                        if (usuario.getRol() == Rol.bibliotecario) {
                            ventanaLogin.mostrarMensajeConfirmacion("[AUTH SUCCESS] Bienvenido Bibliotecario: "
                                    + usuario.getNombre());
                        } else {
                            ventanaLogin.mostrarMensajeConfirmacion("[AUTH SUCCESS] Bienvenido Estudiante: "
                                    + usuario.getNombre());
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
