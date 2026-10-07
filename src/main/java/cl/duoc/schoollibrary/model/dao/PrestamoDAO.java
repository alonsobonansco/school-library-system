package cl.duoc.schoollibrary.model.dao;

import cl.duoc.schoollibrary.database.DatabaseConnection;
import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.entity.Categoria;
import cl.duoc.schoollibrary.model.entity.Estudiante;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.model.entity.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    /**
     * Registra un préstamo en el sistema y descuenta automáticamente el stock del libro.
     * Utiliza un bloque transaccional manual (commit/rollback) para asegurar la integridad de datos.
     */
    public synchronized void create(Prestamo prestamo) {
        String sqlPrestamo = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto)" +
                " VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE libros SET stock = stock - 1 WHERE id = ? AND stock > 0";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getInstance();
            conn.setAutoCommit(false);

            try (PreparedStatement psPrestamo = conn.prepareStatement(sqlPrestamo);
                 PreparedStatement psStock = conn.prepareStatement(sqlStock)) {

                psPrestamo.setInt(1, prestamo.getEstudiante().getId());
                psPrestamo.setInt(2, prestamo.getLibro().getId());
                psPrestamo.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                psPrestamo.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
                psPrestamo.setBoolean(5, prestamo.isDevuelto());
                psPrestamo.executeUpdate();

                psStock.setInt(1, prestamo.getLibro().getId());
                int filasAfectadas = psStock.executeUpdate();

                if (filasAfectadas == 0) {
                    throw new SQLException("No hay stock disponible para el libro con ID: " + prestamo.getLibro().getId());
                }

                conn.commit();

            }

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // Aquí es.
                    ex.addSuppressed(rollbackEx);
                }
            }

            throw new DataAccessException("Error al crear el préstamo y actualizar el stock", ex);
        }
    }

    public synchronized List<Prestamo> read() {
        List<Prestamo> prestamos = new ArrayList<>();

        String sql = """
                SELECT 
                    p.id, p.fecha_prestamo, p.fecha_devolucion, p.devuelto,
                    p.id_estudiante, e.nombre AS nombre_estudiante, e.rut AS rut_estudiante, 
                                     e.curso, e.correo AS correo_estudiante,
                    p.id_libro, l.titulo, l.autor, l.isbn, l.editorial, l.stock,
                    l.id_categoria, c.nombre AS nombre_categoria
                FROM prestamos p
                INNER JOIN estudiantes e ON p.id_estudiante = e.id
                INNER JOIN libros l ON p.id_libro = l.id
                INNER JOIN categorias c ON l.id_categoria = c.id
                """;

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
                            rs.getInt("id_libro"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getString("isbn"),
                            rs.getString("editorial"),
                            rs.getInt("stock"),
                            categoria
                    );

                    Estudiante estudiante = new Estudiante(
                            rs.getInt("id_estudiante"),
                            rs.getString("nombre_estudiante"),
                            rs.getString("rut_estudiante"),
                            rs.getString("curso"),
                            rs.getString("correo_estudiante")
                    );

                    Prestamo prestamo = new Prestamo(
                            rs.getInt("id"),
                            estudiante,
                            libro,
                            rs.getDate("fecha_prestamo").toLocalDate(),
                            rs.getDate("fecha_devolucion").toLocalDate(),
                            rs.getBoolean("devuelto")
                    );

                    prestamos.add(prestamo);
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al leer los préstamos", ex);
        }

        return prestamos;
    }

    public synchronized void registrarDevolucion(int idPrestamo, int idLibro) {
        String sqlPrestamo = "UPDATE prestamos SET devuelto = TRUE WHERE id = ?";
        String sqlStock = "UPDATE libros SET stock = stock + 1 WHERE id = ?";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getInstance();
            conn.setAutoCommit(false);

            try (PreparedStatement psPrestamo = conn.prepareStatement(sqlPrestamo);
                 PreparedStatement psStock = conn.prepareStatement(sqlStock)) {

                psPrestamo.setInt(1, idPrestamo);
                psPrestamo.executeUpdate();

                psStock.setInt(1, idLibro);
                psStock.executeUpdate();

                conn.commit();
            }

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();

                } catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
            }

            throw new DataAccessException("Error al registrar la devolución del préstamo", ex);
        }
    }
}
