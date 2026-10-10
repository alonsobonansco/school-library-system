package cl.duoc.schoollibrary.controller;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AppThreadPool {

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    private AppThreadPool() {
    }

    public static <T> void ejecutarCarga(TareaSecundaria<T> tarea, TareaInterfaz<T> exito, TareaError error) {
        executor.submit(() -> {
            try {
                T resultado = tarea.ejecutar();
                SwingUtilities.invokeLater(() -> exito.procesar(resultado));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> error.procesar(e));
            }
        });
    }

    public static void ejecutarAccion(RunnableAccion tarea, Runnable interfazExito, TareaError error) {
        executor.submit(() -> {
            try {
                tarea.run();
                SwingUtilities.invokeLater(interfazExito);
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> error.procesar(e));
            }
        });
    }

    public static void shutdown() {
        if (!executor.isShutdown()) executor.shutdownNow();
    }

    @FunctionalInterface
    public interface TareaSecundaria<T> {
        T ejecutar() throws Exception;
    }

    @FunctionalInterface
    public interface TareaInterfaz<T> {
        void procesar(T datos);
    }

    @FunctionalInterface
    public interface TareaError {
        void procesar(Exception e);
    }

    @FunctionalInterface
    public interface RunnableAccion {
        void run() throws Exception;
    }
}
