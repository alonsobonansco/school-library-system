package cl.duoc.schoollibrary.controller;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Administrador global de hilos de segundo plano (Asynchronous Thread Pool Engine).
 * Desvía el procesamiento pesado fuera del Event Dispatch Thread (EDT) de Swing
 * y utiliza un tamaño fijo de 2 operarios para optimizar los recursos del sistema.
 */
public final class AppThreadPool {

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    private AppThreadPool() {
    }

    /**
     * Envía una tarea asíncrona de lectura (Read/Query) al pool de hilos.
     * Retorna el conjunto hidratado de datos al hilo gráfico de Swing mediante
     * un canal de mensajería seguro para refrescar la interfaz de usuario de forma fluida.
     *
     * @param tarea Instrucción asíncrona de persistencia ejecutada en el hilo background.
     * @param exito Bloque de código gráfico encargado de renderizar la respuesta de éxito en el EDT.
     * @param error Bloque de código encargado de desplegar el aviso modal de error en el EDT.
     */
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

    /**
     * Envía una tarea asíncrona de mutación transaccional (CUD) al pool de hilos.
     * Mantiene bloqueada la interfaz del formulario y delega de forma asíncrona el flujo
     * de guardado, retornando de forma segura al EDT para lanzar los mensajes flotantes informativos.
     *
     * @param tarea         Instrucción asíncrona de mutación o escritura (Insert/Update/Delete).
     * @param interfazExito Runnable gráfico que gatilla el popup informativo y cierra la ventana en el EDT.
     * @param error         Bloque de código encargado de levantar la alerta de fallo y re-habilitar el formulario.
     */
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

    /**
     * Solicita la detención inmediata y el cierre forzado de todos los hilos activos del pool.
     */
    public static void shutdown() {
        if (!executor.isShutdown()) executor.shutdownNow();
    }

    /**
     * Contrato funcional para rutinas de lectura en segundo plano que retornan colecciones o entidades.
     */
    @FunctionalInterface
    public interface TareaSecundaria<T> {
        T ejecutar() throws Exception;
    }

    /**
     * Contrato funcional para el traspaso seguro de colecciones de datos hidratados de vuelta al EDT.
     */
    @FunctionalInterface
    public interface TareaInterfaz<T> {
        void procesar(T datos);
    }

    /**
     * Contrato funcional centralizado para el ruteo thread-safe de trazas de excepciones de retorno hacia el EDT.
     */
    @FunctionalInterface
    public interface TareaError {
        void procesar(Exception e);
    }

    /**
     * Contrato funcional para rutinas transaccionales destructivas o de inserción que operan de forma plana sin retorno.
     */
    @FunctionalInterface
    public interface RunnableAccion {
        void run() throws Exception;
    }
}
