package cl.duoc.schoollibrary.model.entity;

/* Se dejan en minúsculas para seguir las instrucciones aunque el estándar de Java diga que los enums
   deberían ir en mayúsculas. */

/**
 * Enumeración encargada de definir los roles de acceso del sistema.
 * Determina de forma estricta los privilegios de seguridad y las rutas de navegación
 * disponibles para cada perfil de usuario autenticado en la plataforma.
 */
public enum Rol {
    bibliotecario,
    estudiante
}
