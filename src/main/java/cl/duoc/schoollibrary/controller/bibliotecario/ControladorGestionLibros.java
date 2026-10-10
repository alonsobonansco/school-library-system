package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.controller.AppThreadPool;
import cl.duoc.schoollibrary.exception.RestriccionIntegridadException;
import cl.duoc.schoollibrary.model.dao.CategoriaDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.entity.Categoria;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaGestionLibros;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroLibro;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Cerebro administrativo encargado de coordinar la lectura, filtrado reactivo
 * en memoria y operaciones destructivas del catálogo de inventario de libros.
 */
public class ControladorGestionLibros {

    private final VentanaGestionLibros ventanaGestionLibros;
    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;
    private List<Libro> listaLibrosEnMemoria = new ArrayList<>();

    public ControladorGestionLibros(VentanaGestionLibros ventanaGestionLibros, LibroDAO libroDAO) {
        this.ventanaGestionLibros = ventanaGestionLibros;
        this.libroDAO = libroDAO;
        this.categoriaDAO = new CategoriaDAO();

        inicializarListeners();
        cargarCategoriasDesdeBD();
        cargarLibrosDesdeBD();
    }

    private void inicializarListeners() {
        ventanaGestionLibros.addBusquedaDocumentListener(this::aplicarFiltros);
        ventanaGestionLibros.addCategoriaComboListener(e -> aplicarFiltros());
        ventanaGestionLibros.addAgregarListener(e -> {
            ventanaGestionLibros.setAccionesHabilitadas(false);
            abrirFormularioRegistro(null);
        });
        ventanaGestionLibros.addEditarListener(e -> {
            Libro seleccionado = ventanaGestionLibros.getLibroSeleccionadoEnTabla(listaLibrosEnMemoria);
            if (seleccionado == null) {
                ventanaGestionLibros.mostrarMensajeError("Debe seleccionar un libro de la tabla para modificarlo.");
                return;
            }
            ventanaGestionLibros.setAccionesHabilitadas(false);
            abrirFormularioRegistro(seleccionado);
        });
        ventanaGestionLibros.addEliminarListener(e -> procesarEliminacionCatalogo());
        ventanaGestionLibros.addVolverAtrasListener(e -> ventanaGestionLibros.cerrarVentana());
    }

    public void cargarLibrosDesdeBD() {
        AppThreadPool.ejecutarCarga(
                libroDAO::read,
                libros -> {
                    listaLibrosEnMemoria = libros;
                    ventanaGestionLibros.actualizarTabla(libros);
                },
                ex -> ventanaGestionLibros.mostrarMensajeError(
                        "Error al cargar inventario: " + ex.getMessage())
        );
    }

    private void cargarCategoriasDesdeBD() {
        AppThreadPool.ejecutarCarga(
                categoriaDAO::read,
                categorias -> {
                    List<String> nombres = categorias.stream().map(Categoria::getNombre).toList();
                    ventanaGestionLibros.poblarCategorias(nombres);
                },
                ex -> ventanaGestionLibros.mostrarMensajeError(
                        "Error al inicializar filtros: " + ex.getMessage())
        );
    }

    private void aplicarFiltros() {
        String textoBuscado = ventanaGestionLibros.getTextoBusqueda().toLowerCase().trim();
        String categoriaSeleccionada = ventanaGestionLibros.getCategoriaSeleccionada();

        if (categoriaSeleccionada == null) return;

        List<Libro> librosFiltrados = listaLibrosEnMemoria.stream()
                .filter(l -> textoBuscado.isBlank() ||
                        l.getTitulo().toLowerCase().contains(textoBuscado) ||
                        l.getIsbn().contains(textoBuscado))
                .filter(l -> categoriaSeleccionada.equals("Todas") ||
                        l.getCategoria().getNombre().equalsIgnoreCase(categoriaSeleccionada))
                .toList();

        ventanaGestionLibros.actualizarTabla(librosFiltrados);
    }

    private void abrirFormularioRegistro(Libro libroAEditar) {
        VentanaRegistroLibro ventanaRegistro = new VentanaRegistroLibro();
        ventanaRegistro.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaGestionLibros.setAccionesHabilitadas(true);
            }
        });
        new ControladorRegistroLibro(ventanaRegistro, libroDAO, this, libroAEditar);
        ventanaRegistro.setVisible(true);
    }

    private void procesarEliminacionCatalogo() {
        // Integer permite retornar null cuando no se selecciona una fila, a diferencia de int (primitivo).
        Integer idLibro = ventanaGestionLibros.getIdLibroSeleccionado();
        if (idLibro == null) {
            ventanaGestionLibros.mostrarMensajeError(
                    "Debe seleccionar un título de la tabla para eliminarlo del catálogo.");
            return;
        }

        if (!ventanaGestionLibros.confirmarEliminacion("catálogo")) return;

        ventanaGestionLibros.setAccionesHabilitadas(false);

        AppThreadPool.ejecutarAccion(
                () -> libroDAO.delete(idLibro),
                () -> {
                    ventanaGestionLibros.mostrarMensajeConfirmacion(
                            "El título ha sido removido del catálogo exitosamente.");
                    cargarLibrosDesdeBD();
                    ventanaGestionLibros.setAccionesHabilitadas(true);
                },
                ex -> {
                    if (ex instanceof RestriccionIntegridadException) {
                        ventanaGestionLibros.mostrarMensajeError(
                                "No es posible eliminar este título porque " +
                                        "cuenta con un historial de préstamos asociado.");
                    } else {
                        ventanaGestionLibros.mostrarMensajeError("Error al eliminar: " + ex.getMessage());
                    }
                    ventanaGestionLibros.setAccionesHabilitadas(true);
                }
        );

    }
}
