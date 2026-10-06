package cl.duoc.schoollibrary;

import cl.duoc.schoollibrary.controller.ControladorLogin;
import cl.duoc.schoollibrary.model.dao.UsuarioDAO;
import cl.duoc.schoollibrary.view.VentanaLogin;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            VentanaLogin ventanaPrincipal = new VentanaLogin();
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            new ControladorLogin(ventanaPrincipal, usuarioDAO);

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
