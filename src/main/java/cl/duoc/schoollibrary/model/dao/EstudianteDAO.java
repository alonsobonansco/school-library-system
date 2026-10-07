package cl.duoc.schoollibrary.model.dao;

import cl.duoc.schoollibrary.database.DatabaseConnection;
import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.entity.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    public synchronized void create(Estudiante estudiante) {
        String sql = "INSERT INTO estudiantes (nombre, rut, curso, correo) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, estudiante.getNombre());
                ps.setString(2, estudiante.getRut());
                ps.setString(3, estudiante.getCurso());
                ps.setString(4, estudiante.getCorreo());
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al crear el estudiante", ex);
        }

    }

    public synchronized List<Estudiante> read() {
        List<Estudiante> estudiantes = new ArrayList<>();

        String sql = "SELECT * FROM estudiantes";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Estudiante estudiante = new Estudiante(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("rut"),
                            rs.getString("curso"),
                            rs.getString("correo"));

                    estudiantes.add(estudiante);
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al leer los estudiantes", ex);
        }

        return estudiantes;

    }

    public synchronized void update(Estudiante estudiante) {
        String sql = "UPDATE estudiantes SET nombre = ?, rut = ?, curso = ?, correo = ? WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, estudiante.getNombre());
                ps.setString(2, estudiante.getRut());
                ps.setString(3, estudiante.getCurso());
                ps.setString(4, estudiante.getCorreo());
                ps.setInt(5, estudiante.getId());
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al actualizar el estudiante", ex);
        }

    }

    public synchronized void delete(int idEstudiante) {
        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.getInstance();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idEstudiante);
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error al eliminar el estudiante", ex);
        }
    }
}
