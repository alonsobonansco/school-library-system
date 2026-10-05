package cl.duoc.schoollibrary.model.entity;

import cl.duoc.schoollibrary.util.Validator;

public class Categoria {

    private int id;
    private String nombre;

    public Categoria() {}

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = Validator.validarTexto(nombre, "nombre de categoría", 100);
    }

    public Categoria(String nombre) {
        this.id = 0;
        this.nombre = Validator.validarTexto(nombre, "nombre de categoría", 100);
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
        this.nombre = Validator.validarTexto(nombre, "nombre de categoría", 100);
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
