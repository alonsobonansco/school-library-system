package cl.duoc.schoollibrary.model.dao;

import cl.duoc.schoollibrary.DatabaseConnection;
import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.entity.Categoria;
import cl.duoc.schoollibrary.model.entity.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public synchronized void create(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria)" +
                " VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, libro.getTitulo());
                ps.setString(2, libro.getAutor());
                ps.setString(3, libro.getIsbn());
                ps.setString(4, libro.getEditorial());
                ps.setInt(5, libro.getStock());
                ps.setInt(6, libro.getCategoria().getId());
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al crear el libro", ex);
        }
    }

    public synchronized List<Libro> read() {
        List<Libro> libros = new ArrayList<>();

        String sql = "SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, l.id_categoria, " +
                "c.nombre AS nombre_categoria" +
                " FROM libros l INNER JOIN categorias c ON l.id_categoria = c.id";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Categoria categoria = new Categoria(
                            rs.getInt("id_categoria"),
                            rs.getString("nombre_categoria")
                    );

                    Libro libro = new Libro(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getString("isbn"),
                            rs.getString("editorial"),
                            rs.getInt("stock"),
                            categoria
                    );

                    libros.add(libro);
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al leer los libros", ex);
        }

        return libros;
    }

    public synchronized void update(Libro libro) {
        String sql = "UPDATE libros" +
                " SET titulo = ?, autor = ?, isbn = ?, editorial = ?, stock = ?, id_categoria = ?" +
                " WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, libro.getTitulo());
                ps.setString(2, libro.getAutor());
                ps.setString(3, libro.getIsbn());
                ps.setString(4, libro.getEditorial());
                ps.setInt(5, libro.getStock());
                ps.setInt(6, libro.getCategoria().getId());
                ps.setInt(7, libro.getId());
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al actualizar el libro", ex);
        }
    }

    public synchronized void delete(int idLibro) {
        String sql = "DELETE FROM libros WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al eliminar el libro", ex);
        }
    }
}
