package cl.duoc.schoollibrary.controller;

import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.dao.CategoriaDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.entity.Categoria;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.view.VentanaRegistroLibro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControladorRegistroLibro {

    private final VentanaRegistroLibro ventanaRegistroLibro;
    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;
    private final ControladorInventarioLibros controladorInventarioLibros;
    private List<Categoria> listaCategorias = new ArrayList<>();

    public ControladorRegistroLibro(VentanaRegistroLibro ventanaRegistroLibro, LibroDAO libroDAO,
                                    ControladorInventarioLibros controladorInventarioLibros) {
        this.ventanaRegistroLibro = ventanaRegistroLibro;
        this.libroDAO = libroDAO;
        this.categoriaDAO = new CategoriaDAO();
        this.controladorInventarioLibros = controladorInventarioLibros;

        inicializarListeners();
        cargarCategoriasDesdeBD();
    }

    private void inicializarListeners() {
        ventanaRegistroLibro.addGuardarListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarRegistroLibro();
            }
        });
    }

    private void cargarCategoriasDesdeBD() {
        new Thread (() -> {
            try {
                listaCategorias = categoriaDAO.read();

                List<String> nombresCategorias = listaCategorias.stream()
                        .map(Categoria::getNombre)
                        .toList();

                ventanaRegistroLibro.poblarCategorias(nombresCategorias);

            } catch (DataAccessException e) {
                ventanaRegistroLibro.mostrarMensajeError("Error al cargar las categorías: " + e.getMessage());
            }
        }).start();
    }

    private void procesarRegistroLibro() {
        String titulo = ventanaRegistroLibro.getTituloInput();
        String autor = ventanaRegistroLibro.getAutorInput();
        String isbn = ventanaRegistroLibro.getIsbnInput();
        String editorial = ventanaRegistroLibro.getEditorialInput();
        String stockStr = ventanaRegistroLibro.getStockInput();
        String categoria = ventanaRegistroLibro.getCategoriaSeleccionada();

        if (titulo.isBlank() || autor.isBlank() || isbn.isBlank() || editorial.isBlank() || categoria == null) {
            ventanaRegistroLibro.mostrarMensajeError("Todos los campos son obligatorios.");
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            ventanaRegistroLibro.mostrarMensajeError("El stock no puede ser negativo.");
            return;
        }

        Optional<Categoria> categoriaSeleccionada = listaCategorias.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(categoria))
                .findFirst();

        if (categoriaSeleccionada.isEmpty()) {
            ventanaRegistroLibro.mostrarMensajeError("La categoría seleccionada no es válida.");
            return;
        }

        Libro nuevoLibro = new Libro(titulo, autor, isbn, editorial, stock, categoriaSeleccionada.get());

        new Thread(() -> {
            try {
                libroDAO.create(nuevoLibro);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    ventanaRegistroLibro.mostrarMensajeConfirmacion("Libro registrado correctamente.");

                    if (controladorInventarioLibros != null) {
                        controladorInventarioLibros.cargarDatosDesdeBD();
                    }

                    ventanaRegistroLibro.cerrarVentana();
                });

            } catch (DataAccessException ex) {
                ventanaRegistroLibro.mostrarMensajeError("Error al registrar el libro: " + ex.getMessage());
            }
        }).start();
    }
}
