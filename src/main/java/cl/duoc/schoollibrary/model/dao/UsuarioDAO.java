package cl.duoc.schoollibrary.model.dao;

import cl.duoc.schoollibrary.database.DatabaseConnection;
import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.entity.Rol;
import cl.duoc.schoollibrary.model.entity.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void create(Usuario usuario) throws DataAccessException {
        String sql = "INSERT INTO usuarios (nombre, rut, correo, contraseña, rol) VALUES (?, ?, ?, ?, ?)";

        DatabaseConnection.getInstance().ejecutarTransaccionVoid(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getRut());
                ps.setString(3, usuario.getCorreo());
                ps.setString(4, usuario.getContrasenia());
                ps.setString(5, usuario.getRol().name());
                ps.executeUpdate();
            }
        });
    }

    public List<Usuario> read() throws DataAccessException {
        String sql = "SELECT * FROM usuarios";

        return DatabaseConnection.getInstance().ejecutarConsulta(conn -> {
            List<Usuario> usuarios = new ArrayList<>();

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String textoRol = rs.getString("rol");
                    Rol rolEnum = Rol.valueOf(textoRol.trim());

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

            return usuarios;
        });
    }

    public void update(Usuario usuario) throws DataAccessException {
        String sql = "UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, contraseña = ?, rol = ? WHERE id = ?";

        DatabaseConnection.getInstance().ejecutarTransaccionVoid(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getRut());
                ps.setString(3, usuario.getCorreo());
                ps.setString(4, usuario.getContrasenia());
                ps.setString(5, usuario.getRol().name());
                ps.setInt(6, usuario.getId());
                ps.executeUpdate();
            }
        });
    }

    public void delete(int idUsuario) throws DataAccessException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        DatabaseConnection.getInstance().ejecutarTransaccionVoid(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }
        });
    }
}
