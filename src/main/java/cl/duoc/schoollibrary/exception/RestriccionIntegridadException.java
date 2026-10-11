package cl.duoc.schoollibrary.exception;

/**
 * Excepción de dominio especializada encargada de abstraer las violaciones de integridad referencial.
 * Traduce de forma limpia los códigos de error físicos del motor relacional (como el SQL 1451 de llaves foráneas)
 * en un tipo semántico de negocio, permitiendo que el controlador detecte el fallo mediante polimorfismo puro.
 */
public class RestriccionIntegridadException extends DataAccessException {
    public RestriccionIntegridadException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
