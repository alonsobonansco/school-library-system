package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.model.dao.CategoriaDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.entity.Categoria;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroLibro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControladorRegistroLibro {

    private final VentanaRegistroLibro ventanaRegistroLibro;
    private final LibroDAO libroDAO;
    private final Libro libroAEditar;
    private final CategoriaDAO categoriaDAO;
    private final ControladorGestionLibros controladorGestionLibros;
    private List<Categoria> listaCategorias = new ArrayList<>();

    public ControladorRegistroLibro(VentanaRegistroLibro ventanaRegistroLibro, LibroDAO libroDAO,
                                    ControladorGestionLibros controladorGestionLibros, Libro libroAEditar) {
        this.ventanaRegistroLibro = ventanaRegistroLibro;
        this.libroDAO = libroDAO;
        this.categoriaDAO = new CategoriaDAO();
        this.controladorGestionLibros = controladorGestionLibros;
        this.libroAEditar = libroAEditar;

        inicializarListeners();
        cargarCategoriasDesdeBD();
    }

    private void inicializarListeners() {
        ventanaRegistroLibro.addGuardarListener(e -> procesarRegistroLibro());
        ventanaRegistroLibro.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (controladorGestionLibros != null) {
                    controladorGestionLibros.cargarLibrosDesdeBD();
                }
            }
        });
        ventanaRegistroLibro.addVolverAtrasListener(e -> ventanaRegistroLibro.cerrarVentana());
    }

    private void cargarCategoriasDesdeBD() {
        AppThreadPool.ejecutarCarga(
                categoriaDAO::read,
                categorias -> {
                    this.listaCategorias = categorias;
                    List<String> nombresCategorias = categorias.stream()
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
                },
                ex -> ventanaRegistroLibro.mostrarMensajeError(
                        "Error al cargar las categorías: " + ex.getMessage())
        );
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
        } catch (NumberFormatException e) {
            ventanaRegistroLibro.mostrarMensajeError("El stock ingresado debe ser un número entero válido.");
            return;
        }

        if (stock <= 0) {
            ventanaRegistroLibro.mostrarMensajeError("El stock inicial debe ser mayor que cero para registrar el libro.");
            return;
        }

        ventanaRegistroLibro.setFormularioHabilitado(false);

        AppThreadPool.ejecutarAccion(
                () -> {
                    Optional<Categoria> categoriaSeleccionada = listaCategorias.stream()
                            .filter(c -> c.getNombre().equalsIgnoreCase(categoria))
                            .findFirst();

                    if (categoriaSeleccionada.isEmpty()) {
                        throw new RuntimeException("La categoría seleccionada no es válida.");
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
                },
                () -> {
                    ventanaRegistroLibro.mostrarMensajeConfirmacion("Libro registrado correctamente.");
                    ventanaRegistroLibro.cerrarVentana();
                },
                ex -> {
                    ventanaRegistroLibro.mostrarMensajeError("Error al registrar el libro: " + ex.getMessage());
                    ventanaRegistroLibro.setFormularioHabilitado(true);
                }
        );
    }
}
