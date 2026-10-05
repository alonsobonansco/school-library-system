package cl.duoc.schoollibrary.model.dao;

import cl.duoc.schoollibrary.DatabaseConnection;
import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.entity.Rol;
import cl.duoc.schoollibrary.model.entity.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public synchronized void create(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, rut, correo, contraseña, rol) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getRut());
                ps.setString(3, usuario.getCorreo());
                ps.setString(4, usuario.getContrasenia());
                ps.setString(5, usuario.getRol().name());
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al crear el usuario", ex);
        }
    }

    public synchronized List<Usuario> read() {
        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT * FROM usuarios";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String textoRol = rs.getString("rol");
                    Rol rolEnum = Rol.valueOf(textoRol.toLowerCase().trim());

                    Usuario usuario = new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("rut"),
                            rs.getString("correo"),
                            rs.getString("contraseña"),
                            rolEnum
                    );

                    usuarios.add(usuario);
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al leer los usuarios", ex);
        }

        return usuarios;
    }

    public synchronized void update(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, contraseña = ?, rol = ? WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getRut());
                ps.setString(3, usuario.getCorreo());
                ps.setString(4, usuario.getContrasenia());
                ps.setString(5, usuario.getRol().name());
                ps.setInt(6, usuario.getId());
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al actualizar el usuario", ex);
        }
    }

    public synchronized void delete(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al eliminar el usuario", ex);
        }
    }
}
