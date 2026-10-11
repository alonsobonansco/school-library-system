package cl.duoc.schoollibrary;

import cl.duoc.schoollibrary.controller.CoordinadorNavegacion;

/**
 * Punto de entrada único de la aplicación (Bootstrap Entry Point).
 * Inicializa los componentes estructurales del sistema de biblioteca escolar
 * delegando de forma inmediata el control del ciclo de vida visual y la
 * inyección de dependencias iniciales al CoordinadorNavegacion de la suite.
 */
public class Main {
    public static void main(String[] args) {
        CoordinadorNavegacion.getInstancia().mostrarLogin();
    }
}
