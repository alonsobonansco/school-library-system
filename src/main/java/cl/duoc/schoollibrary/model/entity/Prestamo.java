package cl.duoc.schoollibrary.model.entity;

import cl.duoc.schoollibrary.util.Validator;

import java.time.LocalDate;

public class Prestamo {

    private int id;
    private Estudiante estudiante;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto = false;

    public Prestamo() {
    }

    public Prestamo(int id, Estudiante estudiante, Libro libro,
                    LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this.id = id;
        this.estudiante = Validator.validarObjetoNoNulo(estudiante, "estudiante");
        this.libro = Validator.validarObjetoNoNulo(libro, "libro");
        this.fechaPrestamo = Validator.validarObjetoNoNulo(fechaPrestamo, "fecha de préstamo");
        this.fechaDevolucion = Validator.validarObjetoNoNulo(fechaDevolucion, "fecha de devolución");
        this.devuelto = devuelto;
    }

    public Prestamo(Estudiante estudiante, Libro libro,
                    LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this.id = 0;
        this.estudiante = Validator.validarObjetoNoNulo(estudiante, "estudiante");
        this.libro = Validator.validarObjetoNoNulo(libro, "libro");
        this.fechaPrestamo = Validator.validarObjetoNoNulo(fechaPrestamo, "fecha de préstamo");
        this.fechaDevolucion = Validator.validarObjetoNoNulo(fechaDevolucion, "fecha de devolución");
        this.devuelto = devuelto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = Validator.validarNumeroPositivo(id, "id");
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = Validator.validarObjetoNoNulo(estudiante, "estudiante");
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = Validator.validarObjetoNoNulo(libro, "libro");
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = Validator.validarObjetoNoNulo(fechaPrestamo, "fecha de préstamo");
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = Validator.validarObjetoNoNulo(fechaDevolucion, "fecha de devolución");
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }
}
