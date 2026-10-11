package cl.duoc.schoollibrary.exception;

/**
 * Excepción base personalizada encargada de encapsular y aislar los fallos de la capa de datos.
 * Actúa como un cortafuegos (Firewall) arquitectónico que unifica los errores técnicos de persistencia,
 * impidiendo la fuga de librerías nativas como java.sql hacia los controladores superiores.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
