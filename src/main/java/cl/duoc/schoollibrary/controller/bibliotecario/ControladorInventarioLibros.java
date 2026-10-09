package cl.duoc.schoollibrary.controller.bibliotecario;

import cl.duoc.schoollibrary.exception.DataAccessException;
import cl.duoc.schoollibrary.model.dao.CategoriaDAO;
import cl.duoc.schoollibrary.model.dao.LibroDAO;
import cl.duoc.schoollibrary.model.entity.Categoria;
import cl.duoc.schoollibrary.model.entity.Libro;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaInventarioLibros;
import cl.duoc.schoollibrary.view.bibliotecario.VentanaRegistroLibro;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        ventanaInventarioLibros.addCategoriaComboListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aplicarFiltros();
            }
        });

        ventanaInventarioLibros.addNuevoButtonListener(e -> abrirFormularioRegistro(null));

        ventanaInventarioLibros.addEditarButtonListener(e -> {
            Libro seleccionado = ventanaInventarioLibros.getLibroSeleccionadoEnTabla(listaLibrosCache);
            if (seleccionado == null) {
                ventanaInventarioLibros.mostrarMensajeError("Debe seleccionar un libro de la tabla para modificarlo.");
                return;
            }
            abrirFormularioRegistro(seleccionado);
        });

        ventanaInventarioLibros.addEliminarButtonListener(e -> procesarEliminacionCatalogo());

        ventanaInventarioLibros.addAtrasButtonListener(e -> ventanaInventarioLibros.cerrarVentana());
    }

    public void cargarDatosDesdeBD() {
        new Thread(() -> {
            try {
                listaLibrosCache = libroDAO.read();
                ventanaInventarioLibros.actualizarTabla(listaLibrosCache);
            } catch (DataAccessException ex) {
                ventanaInventarioLibros.mostrarMensajeError("Error al cargar inventario: " + ex.getMessage());
            }
        }).start();
    }

    private void cargarCategoriasDesdeBD() {
        new Thread(() -> {
            try {
                List<Categoria> categorias = categoriaDAO.read();
                List<String> nombres = categorias.stream().map(Categoria::getNombre).toList();
                ventanaInventarioLibros.poblarComboBoxCategorias(nombres);
            } catch (DataAccessException ex) {
                ventanaInventarioLibros.mostrarMensajeError("Error al inicializar filtros: " + ex.getMessage());
            }
        }).start();
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
        SwingUtilities.invokeLater(() -> {
            VentanaRegistroLibro ventanaRegistro = new VentanaRegistroLibro();

            ControladorRegistroLibro controladorRegistroLibro =
                    new ControladorRegistroLibro(ventanaRegistro, libroDAO, this, libroAEditar);



            ventanaRegistro.setVisible(true);
        });
    }

    private void procesarEliminacionCatalogo() {
        Integer idLibro = ventanaInventarioLibros.getIdLibroSeleccionado();
        if (idLibro == null) {
            ventanaInventarioLibros.mostrarMensajeError("Por favor, seleccione un título de la tabla para eliminarlo del catálogo.");
            return;
        }

        boolean confirmacion = ventanaInventarioLibros.confirmarEliminacion("catálogo");

        if (confirmacion) {
            new Thread(() -> {
                try {
                    libroDAO.delete(idLibro);

                    SwingUtilities.invokeLater(() -> {
                        ventanaInventarioLibros.mostrarMensajeConfirmacion("El título ha sido removido del catálogo exitosamente.");
                        cargarDatosDesdeBD();
                    });
                } catch (DataAccessException ex) {
                    ventanaInventarioLibros.mostrarMensajeError("Error al eliminar: " + ex.getMessage());
                }
            }).start();
        }
    }
}
