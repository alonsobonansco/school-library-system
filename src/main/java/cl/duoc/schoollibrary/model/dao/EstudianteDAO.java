package cl.duoc.schoollibrary.model.dao;

import cl.duoc.schoollibrary.database.DatabaseConnection;
import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.exception.RestriccionIntegridadException;
import cl.duoc.schoollibrary.model.entity.Estudiante;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Componente de persistencia encargado del mapeo relacional (DAO) de la entidad Estudiante.
 * Centraliza las consultas SQL crudas y aísla la lógica JDBC delegando la ejecución física
 * al motor central DatabaseConnection a través de contratos funcionales, impidiendo la fuga
 * de recursos o conexiones abiertas hacia las capas superiores del sistema.
 */
public class EstudianteDAO {

    public void create(Estudiante estudiante) throws DataAccessException {
        String sql = "INSERT INTO estudiantes (nombre, rut, curso, correo) VALUES (?, ?, ?, ?)";

        DatabaseConnection.getInstance().ejecutarTransaccionVoid(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, estudiante.getNombre());
                ps.setString(2, estudiante.getRut());
                ps.setString(3, estudiante.getCurso());
                ps.setString(4, estudiante.getCorreo());
                ps.executeUpdate();
            }
        });
    }

    public List<Estudiante> read() throws DataAccessException {
        List<Estudiante> estudiantes = new ArrayList<>();

        String sql = "SELECT * FROM estudiantes";

        return DatabaseConnection.getInstance().ejecutarConsulta(conn -> {
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

            return estudiantes;
        });
    }

    public void update(Estudiante estudiante) throws DataAccessException {
        String sql = "UPDATE estudiantes SET nombre = ?, rut = ?, curso = ?, correo = ? WHERE id = ?";

        DatabaseConnection.getInstance().ejecutarTransaccionVoid(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, estudiante.getNombre());
                ps.setString(2, estudiante.getRut());
                ps.setString(3, estudiante.getCurso());
                ps.setString(4, estudiante.getCorreo());
                ps.setInt(5, estudiante.getId());
                ps.executeUpdate();
            }
        });
    }

    /**
     * Ejecuta la baja física del alumno por clave primaria.
     * Captura de forma aislada las excepciones numéricas nativas de restricción relacional (Error 1451)
     * generadas por préstamos o deudas activas e históricas, y las traduce de forma polimórfica
     * a la excepción de dominio RestriccionIntegridadException.
     *
     * @param idEstudiante Clave primaria única del registro de alumno a remover.
     * @throws RestriccionIntegridadException Si el estudiante posee deudas o préstamos vinculados en el sistema.
     * @throws DataAccessException            Si ocurre un fallo técnico o de red en el driver.
     */
    public void delete(int idEstudiante) throws DataAccessException {
        String sql = "DELETE FROM estudiantes WHERE id = ?";

        DatabaseConnection.getInstance().ejecutarTransaccionVoid(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idEstudiante);
                ps.executeUpdate();
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1451) {
                    throw new RestriccionIntegridadException(
                            "El estudiante cuenta con deudas o préstamos históricos registrados.", ex);
                }
                throw ex;
            }
        });
    }
}
