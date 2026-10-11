package cl.duoc.schoollibrary.util;

/**
 * Suite central defensiva encargada de validar restricciones semánticas de datos.
 * Provee un esquema reactivo Fail-Fast que aborta flujos de ejecución anómalos
 * lanzando excepciones de control antes de permitir que la corrupción de datos escale.
 */
public final class Validator {

    private Validator() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable.");
    }

    public static String validarTexto(String valor, String nombreCampo, int cantidadMaxima) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " no puede estar vacío.");
        }

        String textoSanitizado = valor.trim();

        if (textoSanitizado.length() > cantidadMaxima) {
            throw new IllegalArgumentException(
                    "El campo " + nombreCampo + " excede la cantidad máxima de caracteres permitidos.");
        }

        return textoSanitizado;
    }

    public static String validarEmail(String valor, int cantidadMaxima) {
        String emailSanitizado = validarTexto(valor, "email", cantidadMaxima);

        if (!emailSanitizado.contains("@") || !emailSanitizado.contains(".")) {
            throw new IllegalArgumentException("El email proporcionado no tiene un formato válido.");
        }

        return emailSanitizado;
    }

    public static int validarNumeroPositivo(int valor, String nombreCampo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " debe ser un número positivo.");
        }

        return valor;
    }

    /**
     * Evalúa de forma genérica la existencia física de una referencia objetual.
     * Utilizado masivamente para validar la inyección de dependencias y agregaciones de entidades.
     *
     * @param objeto      Instancia o puntero de memoria a evaluar.
     * @param nombreCampo Etiqueta semántica del atributo evaluado para personalizar el mensaje de error.
     * @param <T>         Tipo genérico de la entidad en evaluación.
     * @return El mismo objeto verificado para permitir asignaciones en línea limpias.
     * @throws IllegalArgumentException Si el puntero de memoria es nulo.
     */
    public static <T> T validarObjetoNoNulo(T objeto, String nombreCampo) {
        if (objeto == null) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " no puede ser nulo.");
        }

        return objeto;
    }
}
