package cl.duoc.schoollibrary.database;

import cl.duoc.schoollibrary.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static DatabaseConnection instancia;
    private final Object lock = new Object();
    private Connection conexion;

    private DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    public <T> T ejecutarTransaccion(ConnectionOperation<T> operacion) throws DataAccessException {
        synchronized (lock) {
            Connection conn = null;
            try {
                conn = obtenerConexion();
                conn.setAutoCommit(false);
                T resultado = operacion.ejecutar(conn);
                conn.commit();
                return resultado;

            } catch (SQLException | RuntimeException ex) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        ex.addSuppressed(rollbackEx);
                    }
                }
                throw new DataAccessException("Error al ejecutar la transacción: " + ex.getMessage(), ex);
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void ejecutarTransaccionVoid(ConnectionOperationVoid operacion) throws DataAccessException {
        ejecutarTransaccion(conn -> {
            operacion.ejecutar(conn);
            return null;
        });
    }

    public <T> T ejecutarConsulta(ConnectionOperation<T> operacion) throws DataAccessException {
        synchronized (lock) {
            try {
                Connection conn = obtenerConexion();
                return operacion.ejecutar(conn);
            } catch (SQLException | RuntimeException ex) {
                throw new DataAccessException("Error al ejecutar la consulta: " + ex.getMessage(), ex);
            }
        }
    }

    private Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        }

        return conexion;
    }

    @FunctionalInterface
    public interface ConnectionOperation<T> {
        T ejecutar(Connection connection) throws SQLException;
    }

    @FunctionalInterface
    public interface ConnectionOperationVoid {
        void ejecutar(Connection connection) throws SQLException;
    }
}
