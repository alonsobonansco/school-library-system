package cl.duoc.schoollibrary.util;

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

    public static <T> T validarObjetoNoNulo(T objeto, String nombreCampo) {
        if (objeto == null) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " no puede ser nulo.");
        }

        return objeto;
    }
}
