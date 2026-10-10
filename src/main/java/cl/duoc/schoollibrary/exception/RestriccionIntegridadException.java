package cl.duoc.schoollibrary.exception;

public class RestriccionIntegridadException extends DataAccessException {
    public RestriccionIntegridadException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
