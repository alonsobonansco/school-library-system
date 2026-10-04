package cl.duoc.schoollibrary;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseConnection.getInstance();

            System.out.println("[Sistema] Iniciando la aplicación SchoolLibrary...");

        } catch (SQLException ex) {
            System.out.println("Error en la conexión a la base de datos: " + ex.getMessage());
        }
    }
}
