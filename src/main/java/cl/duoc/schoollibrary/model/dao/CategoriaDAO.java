package cl.duoc.schoollibrary.model.dao;

import cl.duoc.schoollibrary.DatabaseConnection;
import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.entity.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public synchronized void create(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, categoria.getNombre());
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al crear la categoría", ex);
        }
    }

    public synchronized List<Categoria> read() {
        List<Categoria> categorias = new ArrayList<>();

        String sql = "SELECT * FROM categorias";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Categoria categoria = new Categoria(
                            rs.getInt("id"),
                            rs.getString("nombre"));

                    categorias.add(categoria);
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al leer las categorías", ex);
        }

        return categorias;
    }

    public synchronized void update(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, categoria.getNombre());
                ps.setInt(2, categoria.getId());
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al actualizar la categoría", ex);
        }
    }

    public synchronized void delete(int  idCategoria) {
        String sql = "DELETE FROM categorias WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idCategoria);
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al eliminar la categoría", ex);
        }
    }
}
