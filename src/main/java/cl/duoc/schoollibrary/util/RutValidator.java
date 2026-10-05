package cl.duoc.schoollibrary.util;

public final class RutValidator {

    private RutValidator() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable.");
    }

    public static String validarRut(String rut) {

        if (rut == null || rut.isBlank()) {
            throw new IllegalArgumentException("El RUT no puede estar vacío.");
        }

        String rutSanitizado = rut.replace(".", "")
                                  .replace(" ", "")
                                  .trim().toUpperCase();

        if (!rutSanitizado.contains("-") && rutSanitizado.length() > 7) {
            rutSanitizado = rutSanitizado.substring(0, rutSanitizado.length() - 1)
                    + "-" + rutSanitizado.charAt(rutSanitizado.length() - 1);
        }

        if (!rutSanitizado.matches("^[0-9]{6,8}-[0-9K]$")) {
            throw new IllegalArgumentException("Formato de RUT inválido.");
        }

        if (!validarModulo11(rutSanitizado)) {
            throw new IllegalArgumentException("Dígito verificador incorrecto.");
        }

        return rutSanitizado;
    }

    private static boolean validarModulo11(String rut) {
        String[] partes = rut.split("-");
        String numero = partes[0];
        String dvIngresado = partes[1].toUpperCase();

        int suma = 0;
        int multiplicador = 2;

        for (int i = numero.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(numero.charAt(i)) * multiplicador;
            multiplicador++;
            if (multiplicador > 7) {
                multiplicador = 2;
            }
        }

        int resto = 11 - (suma % 11);

        String dvCalculado = switch (resto) {
            case 11 -> "0";
            case 10 -> "K";
            default -> String.valueOf(resto);
        };

        return dvCalculado.equals(dvIngresado);
    }
}