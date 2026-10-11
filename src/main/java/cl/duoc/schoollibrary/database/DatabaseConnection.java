package cl.duoc.schoollibrary.database;

import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.exception.RestriccionIntegridadException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Motor central de persistencia y conexión relacional bajo el patrón Singleton.
 * Centraliza el socket físico de MySQL y provee un embudo de sincronización (Mutex)
 * para garantizar la seguridad de los hilos background de la suite.
 */
public final class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static DatabaseConnection instancia;
    private final Object lock = new Object();
    private Connection conexion;

    private DatabaseConnection() {
    }

    /**
     * Retorna la instancia única del coordinador de conexión.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    /**
     * Envuelve y ejecuta un bloque de instrucciones SQL que alteran datos (CUD).
     * Controla el ciclo de vida del auto-commit, serializa el acceso mediante el objeto lock
     * y gestiona de forma automática los rollbacks transaccionales en caso de fallo.
     *
     * @param operacion Lambda o bloque funcional que contiene las consultas JDBC.
     * @param <T>       Tipo de entidad o resultado polimórfico esperado por el DAO.
     * @return El resultado procesado de la operación transaccional.
     * @throws RestriccionIntegridadException Si el motor detecta una violación de claves foráneas.
     * @throws DataAccessException            Si ocurre un fallo técnico o de red en el driver.
     */
    public <T> T ejecutarTransaccion(ConnectionOperation<T> operacion) throws DataAccessException {
        synchronized (lock) {
            Connection conn = null;
            try {
                conn = obtenerConexion();
                conn.setAutoCommit(false);
                T resultado = operacion.ejecutar(conn);
                conn.commit();
                return resultado;

            } catch (RestriccionIntegridadException ex) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        ex.addSuppressed(rollbackEx);
                    }
                }
                throw ex;
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
                        LOGGER.severe("Error al restablecer el auto-commit: " + ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Variación funcional de ejecutarTransaccion diseñada para operaciones
     * de escritura que no necesitan retornar ningún dato (Void).
     */
    public void ejecutarTransaccionVoid(ConnectionOperationVoid operacion) throws DataAccessException {
        ejecutarTransaccion(conn -> {
            operacion.ejecutar(conn);
            return null;
        });
    }

    /**
     * Envuelve y ejecuta consultas de lectura pura (Read/Select) sobre el socket.
     * Mantiene el auto-commit activo por eficiencia y serializa el tráfico mediante el lock central.
     *
     * @param operacion Lambda de lectura JDBC.
     * @return El objeto o colección mapeada desde la base de datos.
     */
    public <T> T ejecutarConsulta(ConnectionOperation<T> operacion) throws DataAccessException {
        synchronized (lock) {
            try {
                Connection conn = obtenerConexion();
                return operacion.ejecutar(conn);
            } catch (RestriccionIntegridadException ex) {
                throw ex;
            } catch (SQLException | RuntimeException ex) {
                throw new DataAccessException("Error al ejecutar la consulta: " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * Inicializa o recupera de forma perezosa (Lazy) la conexión física compartida.
     */
    private Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión Singleton inicializada con éxito!");
        }

        return conexion;
    }

    /**
     * Contrato polimórfico para inyectar operaciones JDBC con tipo de retorno.
     */
    @FunctionalInterface
    public interface ConnectionOperation<T> {
        T ejecutar(Connection connection) throws SQLException;
    }

    /**
     * Contrato polimórfico para inyectar operaciones JDBC de ejecución plana sin retorno.
     */
    @FunctionalInterface
    public interface ConnectionOperationVoid {
        void ejecutar(Connection connection) throws SQLException;
    }
}
