package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.model.dao.EstudianteDAO;
import cl.duoc.schoollibrary.model.dao.UsuarioDAO;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Rol;
import cl.duoc.schoollibrary.model.entity.Usuario;
import cl.duoc.schoollibrary.view.VentanaLogin;

import java.util.List;
import java.util.Optional;

public class ControladorLogin {

    private final VentanaLogin ventanaLogin;
    private final UsuarioDAO usuarioDAO;
    private final EstudianteDAO estudianteDAO;

    public ControladorLogin(VentanaLogin ventanaLogin, UsuarioDAO usuarioDAO) {
        this.ventanaLogin = ventanaLogin;
        this.usuarioDAO = usuarioDAO;
        this.estudianteDAO = new EstudianteDAO();

        inicializarListeners();
    }

    private void inicializarListeners() {
        this.ventanaLogin.addIngresarButtonListener(e -> procesarAutenticacion());
    }

    private void procesarAutenticacion() {
        String correo = ventanaLogin.getCorreoInput();
        String contrasenia = ventanaLogin.getContraseniaInput();

        if (correo.isBlank() || contrasenia.isBlank()) {
            ventanaLogin.mostrarMensajeError("Por favor, complete todos los campos");
            return;
        }

        AppThreadPool.ejecutarCarga(
                () -> {
                    List<Usuario> usuarios = usuarioDAO.read();

                    Optional<Usuario> usuarioEncontrado = usuarios.stream()
                            .filter(u -> u.getCorreo().trim().equalsIgnoreCase(correo)
                                    && u.getContrasenia().trim().equals(contrasenia))
                            .findFirst();

                    if (usuarioEncontrado.isEmpty()) {
                        return Optional.empty();
                    }

                    Usuario usuario = usuarioEncontrado.get();

                    if (usuario.getRol() == Rol.estudiante) {
                        List<Estudiante> estudiantes = estudianteDAO.read();

                        Optional<Estudiante> estudianteEncontrado = estudiantes.stream()
                                .filter(e -> e.getCorreo().trim().equalsIgnoreCase(usuario.getCorreo().trim()))
                                .findFirst();

                        if (estudianteEncontrado.isPresent()) return estudianteEncontrado.get();
                    }

                    return usuario;
                },
                perfilAutenticado -> {
                    if (perfilAutenticado instanceof Optional && ((Optional<?>) perfilAutenticado).isEmpty()) {
                        ventanaLogin.mostrarMensajeError("Correo o contraseña incorrectos.");
                        ventanaLogin.limpiarFormulario();
                        return;
                    }

                    if (perfilAutenticado instanceof Usuario usuarioAdmin) {
                        ventanaLogin.mostrarMensajeConfirmacion("Acceso concedido\nBienvenido/a al Sistema");
                        CoordinadorNavegacion.getInstancia().mostrarPrincipalBibliotecario(usuarioAdmin);
                        ventanaLogin.cerrarVentana();
                    } else if (perfilAutenticado instanceof Estudiante estudianteAlumno) {
                        ventanaLogin.mostrarMensajeConfirmacion("Acceso concedido\nBienvenido/a al Portal");
                        CoordinadorNavegacion.getInstancia().mostrarPortalEstudiante(estudianteAlumno);
                        ventanaLogin.cerrarVentana();
                    }
                },
                ex -> {
                    ventanaLogin.mostrarMensajeError("Error al acceder a la base de datos: " + ex.getMessage());
                }

        );
    }
}
