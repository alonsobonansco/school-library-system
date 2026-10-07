package cl.duoc.schoollibrary;

import cl.duoc.schoollibrary.controller.ControladorInventarioLibros;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.dao.UsuarioDAO;
import cl.duoc.schoollibrary.view.VentanaInventarioLibros;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            VentanaInventarioLibros ventanaPrincipal = new VentanaInventarioLibros();
            LibroDAO usuarioDAO = new LibroDAO();

            ControladorInventarioLibros controladorInventarioLibros =
                    new ControladorInventarioLibros(ventanaPrincipal, usuarioDAO);

            ventanaPrincipal.setVisible(true);
        });

        try {
            DatabaseConnection.getInstance();

            System.out.println("[Sistema] Iniciando la aplicación SchoolLibrary...");

        } catch (SQLException ex) {
            System.out.println("Error en la conexión a la base de datos: " + ex.getMessage());
        }
    }
}
