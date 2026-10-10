package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.model.dao.CategoriaDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.entity.Categoria;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaInventarioLibros;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroLibro;

import java.util.ArrayList;
import java.util.List;

/**
 * Cerebro administrativo encargado de coordinar la lectura, filtrado reactivo
 * en memoria y operaciones destructivas del catálogo de inventario de libros.
 */
public class ControladorInventarioLibros {

    private final VentanaInventarioLibros ventanaInventarioLibros;
    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;
    private List<Libro> listaLibrosCache = new ArrayList<>();

    public ControladorInventarioLibros(VentanaInventarioLibros ventanaInventarioLibros, LibroDAO libroDAO) {
        this.ventanaInventarioLibros = ventanaInventarioLibros;
        this.libroDAO = libroDAO;
        this.categoriaDAO = new CategoriaDAO();

        inicializarListeners();
        cargarCategoriasDesdeBD();
        cargarDatosDesdeBD();
    }

    private void inicializarListeners() {
        ventanaInventarioLibros.addBusquedaDocumentListener(this::aplicarFiltros);
        ventanaInventarioLibros.addCategoriaComboListener(e -> aplicarFiltros());
        ventanaInventarioLibros.addAgregarListener(e -> abrirFormularioRegistro(null));
        ventanaInventarioLibros.addEditarListener(e -> {
            Libro seleccionado = ventanaInventarioLibros.getLibroSeleccionadoEnTabla(listaLibrosCache);
            if (seleccionado == null) {
                ventanaInventarioLibros.mostrarMensajeError("Debe seleccionar un libro de la tabla para modificarlo.");
                return;
            }
            abrirFormularioRegistro(seleccionado);
        });
        ventanaInventarioLibros.addEliminarListener(e -> procesarEliminacionCatalogo());
        ventanaInventarioLibros.addVolverAtrasListener(e -> ventanaInventarioLibros.cerrarVentana());
    }

    public void cargarDatosDesdeBD() {
        AppThreadPool.ejecutarCarga(
                libroDAO::read,
                libros -> {
                    listaLibrosCache = libros;
                    ventanaInventarioLibros.actualizarTabla(libros);
                },
                ex -> ventanaInventarioLibros.mostrarMensajeError(
                        "Error al cargar inventario: " + ex.getMessage())
        );
    }

    private void cargarCategoriasDesdeBD() {
        AppThreadPool.ejecutarCarga(
                categoriaDAO::read,
                categorias -> {
                    List<String> nombres = categorias.stream().map(Categoria::getNombre).toList();
                    ventanaInventarioLibros.poblarComboBoxCategorias(nombres);
                },
                ex -> ventanaInventarioLibros.mostrarMensajeError(
                        "Error al inicializar filtros: " + ex.getMessage())
        );
    }

    private void aplicarFiltros() {
        String textoBuscado = ventanaInventarioLibros.getTextoBusqueda().toLowerCase().trim();
        String categoriaSeleccionada = ventanaInventarioLibros.getCategoriaSeleccionada();

        if (categoriaSeleccionada == null) return;

        List<Libro> librosFiltrados = listaLibrosCache.stream()
                .filter(l -> textoBuscado.isBlank() ||
                        l.getTitulo().toLowerCase().contains(textoBuscado) ||
                        l.getIsbn().contains(textoBuscado))
                .filter(l -> categoriaSeleccionada.equals("Todas") ||
                        l.getCategoria().getNombre().equalsIgnoreCase(categoriaSeleccionada))
                .toList();

        ventanaInventarioLibros.actualizarTabla(librosFiltrados);
    }

    private void abrirFormularioRegistro(Libro libroAEditar) {
        VentanaRegistroLibro ventanaRegistro = new VentanaRegistroLibro();
        new ControladorRegistroLibro(ventanaRegistro, libroDAO, this, libroAEditar);
        ventanaRegistro.setVisible(true);
    }

    private void procesarEliminacionCatalogo() {
        // Integer permite retornar null cuando no se selecciona una fila, a diferencia de int (primitivo).
        Integer idLibro = ventanaInventarioLibros.getIdLibroSeleccionado();
        if (idLibro == null) {
            ventanaInventarioLibros.mostrarMensajeError(
                    "Por favor, seleccione un título de la tabla para eliminarlo del catálogo.");
            return;
        }

        if (ventanaInventarioLibros.confirmarEliminacion("catálogo")) {
            AppThreadPool.ejecutarAccion(
                    () -> libroDAO.delete(idLibro),
                    () -> {
                        ventanaInventarioLibros.mostrarMensajeConfirmacion(
                                "El título ha sido removido del catálogo exitosamente.");
                        cargarDatosDesdeBD();
                    },
                    ex -> ventanaInventarioLibros.mostrarMensajeError("Error al eliminar: " + ex.getMessage())
            );
        }
    }
}
