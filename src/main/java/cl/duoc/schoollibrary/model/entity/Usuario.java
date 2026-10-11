package cl.duoc.schoollibrary.model.entity;

import cl.duoc.schoollibrary.util.RutValidator;
import cl.duoc.schoollibrary.util.Validator;

/**
 * Entidad de dominio que modela el perfil de acceso, credenciales y privilegios de un usuario.
 * Centraliza la validación defensiva invariante de credenciales de seguridad, correos y RUN/RUT,
 * asegurando la consistencia del estado de autenticación antes de interactuar con la capa de persistencia.
 */
public class Usuario {

    private int id;
    private String nombre;
    private String rut;
    private String correo;
    private String contrasenia;
    private Rol rol;

    // Constructor vacío para posibles usos posteriores.
    public Usuario() {
    }

    // Constructor con ID
    public Usuario(int id, String nombre, String rut, String correo, String contrasenia, Rol rol) {
        this.id = id;
        this.nombre = Validator.validarTexto(nombre, "nombre de usuario", 100);
        this.rut = RutValidator.validarRut(rut);
        this.correo = Validator.validarEmail(correo, 100);
        this.contrasenia = Validator.validarTexto(contrasenia, "contraseña", 100);
        this.rol = Validator.validarObjetoNoNulo(rol, "rol");
    }

    // Constructor sin ID
    public Usuario(String nombre, String rut, String correo, String contrasenia, Rol rol) {
        this.id = 0;
        this.nombre = Validator.validarTexto(nombre, "nombre de usuario", 100);
        this.rut = RutValidator.validarRut(rut);
        this.correo = Validator.validarEmail(correo, 100);
        this.contrasenia = Validator.validarTexto(contrasenia, "contraseña", 100);
        this.rol = Validator.validarObjetoNoNulo(rol, "rol");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = Validator.validarNumeroPositivo(id, "id");
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Validator.validarTexto(nombre, "nombre de usuario", 100);
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = RutValidator.validarRut(rut);
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = Validator.validarEmail(correo, 100);
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = Validator.validarTexto(contrasenia, "contraseña", 100);
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = Validator.validarObjetoNoNulo(rol, "rol");
    }
}

