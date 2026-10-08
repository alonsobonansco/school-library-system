package cl.duoc.schoollibrary;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static volatile Connection conexion;

    private DatabaseConnection() {
    }

    public static Connection getInstance() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            synchronized (DatabaseConnection.class) {
                if (conexion == null || conexion.isClosed()) {
                    conexion = java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("¡Conexión Singleton inicializada con éxito!");
                }
            }
        }

        return conexion;
    }
}
