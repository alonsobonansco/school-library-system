package cl.duoc.schoollibrary.model.entity;

import cl.duoc.schoollibrary.util.Validator;

public class Libro {

    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private String editorial;
    private int stock;
    private Categoria categoria;

    public Libro() {}

    public Libro(int id, String titulo, String autor,
                 String isbn, String editorial, int stock, Categoria categoria) {
        this.id = id;
        this.titulo = Validator.validarTexto(titulo, "título", 200);
        this.autor = Validator.validarTexto(autor, "autor", 100);
        this.isbn = Validator.validarTexto(isbn, "isbn", 20);
        this.editorial = Validator.validarTexto(editorial, "editorial", 100);
        this.stock = Validator.validarNumeroPositivo(stock, "stock");
        this.categoria = Validator.validarObjetoNoNulo(categoria, "categoría");
    }

    public Libro(String titulo, String autor,
                 String isbn, String editorial, int stock, Categoria categoria) {
        this.id = 0;
        this.titulo = Validator.validarTexto(titulo, "título", 200);
        this.autor = Validator.validarTexto(autor, "autor", 100);
        this.isbn = Validator.validarTexto(isbn, "isbn", 20);
        this.editorial = Validator.validarTexto(editorial, "editorial", 100);
        this.stock = Validator.validarNumeroPositivo(stock, "stock");
        this.categoria = Validator.validarObjetoNoNulo(categoria, "categoría");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = Validator.validarNumeroPositivo(id, "id");
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = Validator.validarTexto(titulo, "título", 200);
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = Validator.validarTexto(autor, "autor", 100);
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = Validator.validarTexto(isbn, "isbn", 20);
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = Validator.validarTexto(editorial, "editorial", 100);
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = Validator.validarNumeroPositivo(stock, "stock");
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = Validator.validarObjetoNoNulo(categoria, "categoría");
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", isbn='" + isbn + '\'' +
                ", editorial='" + editorial + '\'' +
                ", stock=" + stock +
                ", categoria=" + categoria +
                '}';
    }
}
