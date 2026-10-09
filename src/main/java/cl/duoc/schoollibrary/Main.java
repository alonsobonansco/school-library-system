package cl.duoc.schoollibrary;

import cl.duoc.schoollibrary.controller.CoordinadorNavegacion;
import cl.duoc.schoollibrary.database.DatabaseConnection;

public class Main {
    public static void main(String[] args) {

        System.out.println("[Sistema] Iniciando la aplicación SchoolLibrary...");
        CoordinadorNavegacion.getInstancia().mostrarLogin();
    }
}
