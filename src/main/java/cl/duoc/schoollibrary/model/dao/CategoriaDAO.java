package cl.duoc.schoollibrary.model.dao;

import cl.duoc.schoollibrary.database.DatabaseConnection;
import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.entity.Categoria;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Componente de persistencia encargado del mapeo relacional (DAO) de la entidad Categoría.
 * Centraliza el acceso CRUD al maestro de clasificaciones literarias del sistema, abstrayendo
 * las consultas JDBC crudas y aislando la lógica de lectura para alimentar de forma segura
 * los filtros reactivos de la interfaz gráfica.
 */
public class CategoriaDAO {

    public void create(Categoria categoria) throws DataAccessException {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";

        DatabaseConnection.getInstance().ejecutarTransaccionVoid(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, categoria.getNombre());
                ps.executeUpdate();
            }
        });
    }

    public List<Categoria> read() throws DataAccessException {
        List<Categoria> categorias = new ArrayList<>();

        String sql = "SELECT * FROM categorias";

        return DatabaseConnection.getInstance().ejecutarConsulta(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Categoria categoria = new Categoria(
                            rs.getInt("id"),
                            rs.getString("nombre"));
                    categorias.add(categoria);
                }
            }
            return categorias;
        });
    }

    public void update(Categoria categoria) throws DataAccessException {
        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";

        DatabaseConnection.getInstance().ejecutarTransaccionVoid(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, categoria.getNombre());
                ps.setInt(2, categoria.getId());
                ps.executeUpdate();
            }
        });
    }

    public void delete(int idCategoria) throws DataAccessException {
        String sql = "DELETE FROM categorias WHERE id = ?";

        DatabaseConnection.getInstance().ejecutarTransaccionVoid(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCategoria);
                ps.executeUpdate();
            }
        });
    }
}
