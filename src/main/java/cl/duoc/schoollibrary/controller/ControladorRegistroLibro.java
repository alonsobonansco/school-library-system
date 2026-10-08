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
    private final Libro libroAEditar;
    private final CategoriaDAO categoriaDAO;
    private final ControladorInventarioLibros controladorInventarioLibros;
    private List<Categoria> listaCategorias = new ArrayList<>();

    public ControladorRegistroLibro(VentanaRegistroLibro ventanaRegistroLibro, LibroDAO libroDAO,
                                    ControladorInventarioLibros controladorInventarioLibros, Libro libroAEditar) {
        this.ventanaRegistroLibro = ventanaRegistroLibro;
        this.libroDAO = libroDAO;
        this.categoriaDAO = new CategoriaDAO();
        this.controladorInventarioLibros = controladorInventarioLibros;
        this.libroAEditar = libroAEditar;

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

        ventanaRegistroLibro.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (controladorInventarioLibros != null) {
                    controladorInventarioLibros.cargarDatosDesdeBD();
                }
            }
        });
    }

    private void cargarCategoriasDesdeBD() {
        new Thread(() -> {
            try {
                listaCategorias = categoriaDAO.read();
                List<String> nombresCategorias = listaCategorias.stream()
                        .map(Categoria::getNombre)
                        .toList();

                ventanaRegistroLibro.poblarCategorias(nombresCategorias);

                if (libroAEditar != null) {
                    ventanaRegistroLibro.prellenarFormulario(
                            libroAEditar.getTitulo(),
                            libroAEditar.getAutor(),
                            libroAEditar.getIsbn(),
                            libroAEditar.getEditorial(),
                            String.valueOf(libroAEditar.getStock()),
                            libroAEditar.getCategoria().getNombre()
                    );
                }

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

        new Thread(() -> {
            try {
                Optional<Categoria> categoriaSeleccionada = listaCategorias.stream()
                        .filter(c -> c.getNombre().equalsIgnoreCase(categoria))
                        .findFirst();

                if (categoriaSeleccionada.isEmpty()) {
                    ventanaRegistroLibro.mostrarMensajeError("La categoría seleccionada no es válida.");
                    return;
                }

                if (libroAEditar == null) {
                    Libro nuevoLibro = new Libro(titulo, autor, isbn, editorial, stock, categoriaSeleccionada.get());
                    libroDAO.create(nuevoLibro);
                } else {
                    libroAEditar.setTitulo(titulo);
                    libroAEditar.setAutor(autor);
                    libroAEditar.setIsbn(isbn);
                    libroAEditar.setEditorial(editorial);
                    libroAEditar.setStock(stock);
                    libroAEditar.setCategoria(categoriaSeleccionada.get());
                    libroDAO.update(libroAEditar);
                }

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
