package cl.duoc.schoollibrary.model.entity;

import cl.duoc.schoollibrary.util.Validator;

/**
 * Entidad de dominio que representa la categorización o literaria de los libros.
 * Consolida el encapsulamiento inmutable de sus atributos mediante ganchos automáticos de validación,
 * defendiendo la integridad estructural del modelo relacional antes de tocar la capa de persistencia.
 */
public class Categoria {

    private int id;
    private String nombre;

    public Categoria() {
    }

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
}
