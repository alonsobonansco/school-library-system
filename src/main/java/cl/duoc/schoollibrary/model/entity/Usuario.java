package cl.duoc.schoollibrary.model.entity;

public class Usuario {

    private int id;
    private String nombre;
    private String rut;
    private String correo;
    private String contrasenia;
    private Rol rol;

    // Constructor vacío para posibles usos posteriores.
    public Usuario() {}

    // Constructor con ID
    public Usuario(int id, String nombre, String rut, String correo, String contrasenia, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.rol = rol;
    }

    // Constructor sin ID
    public Usuario(String nombre, String rut, String correo, String contrasenia, Rol rol) {
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", rut='" + rut + '\'' +
                ", correo='" + correo + '\'' +
                ", rol=" + rol +
                '}';
    }
}

