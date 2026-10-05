package cl.duoc.schoollibrary.model.entity;

import cl.duoc.schoollibrary.util.RutValidator;
import cl.duoc.schoollibrary.util.Validator;

public class Estudiante {

    private int id;
    private String nombre;
    private String rut;
    private String curso;
    private String correo;

    public Estudiante() {}

    public Estudiante(int id, String nombre, String rut, String curso, String correo) {
        this.id = id;
        this.nombre = Validator.validarTexto(nombre, "nombre de estudiante", 100);
        this.rut = RutValidator.validarRut(rut);
        this.curso = Validator.validarTexto(curso, "curso", 20);
        this.correo = Validator.validarEmail(correo, 100);
    }

    public Estudiante(String nombre, String rut, String curso, String correo) {
        this.id = 0;
        this.nombre = Validator.validarTexto(nombre, "nombre de estudiante", 100);
        this.rut = RutValidator.validarRut(rut);
        this.curso = Validator.validarTexto(curso, "curso", 20);
        this.correo = Validator.validarEmail(correo, 100);
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
        this.nombre = Validator.validarTexto(nombre, "nombre de estudiante", 100);
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = RutValidator.validarRut(rut);
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = Validator.validarTexto(curso, "curso", 20);
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = Validator.validarEmail(correo, 100);
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", rut='" + rut + '\'' +
                ", curso='" + curso + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}
