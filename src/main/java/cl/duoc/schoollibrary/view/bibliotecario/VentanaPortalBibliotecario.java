package cl.duoc.schoollibrary.view.bibliotecario;

import cl.duoc.schoollibrary.view.VentanaBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Consola raíz de navegación global para el perfil Bibliotecario.
 * Actúa como el panel de control maestro e implementa una barra de menús integrada (JMenuBar)
 * vinculada a métodos semánticos explícitos encargados de congelar de forma atómica los ganchos
 * de enrutamiento, impidiendo la inflación redundante de múltiples ventanas de gestión hijas.
 */
public class VentanaPortalBibliotecario extends VentanaBase {

    private JLabel tituloLabel;
    private JMenuItem itemLibros;
    private JMenuItem itemEstudiantes;
    private JMenuItem itemPrestamos;
    private JMenuItem itemReportes;
    private JMenuItem itemCerrarSesion;
    private JLabel textoFondoLabel;

    public VentanaPortalBibliotecario() {
        super("Panel de Control Administrador - Sistema de Biblioteca", 850, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        inicializarComponentes();
        inicializarMenuBar();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Consola de Gestión Biblioteca Escolar", SwingConstants.CENTER);
        tituloLabel.setFont(FUENTE_TITULO.deriveFont(22f));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
    }

    private void inicializarMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        itemLibros = new JMenuItem("Catálogo de Libros");
        itemEstudiantes = new JMenuItem("Gestión de Estudiantes");
        itemPrestamos = new JMenuItem("Control de Préstamos");
        itemReportes = new JMenuItem("Reportes y Estadísticas");
        itemCerrarSesion = new JMenuItem("Cerrar Sesión");

        Font fuenteMenu = FUENTE_CAMPO.deriveFont(13f);
        itemLibros.setFont(fuenteMenu);
        itemEstudiantes.setFont(fuenteMenu);
        itemPrestamos.setFont(fuenteMenu);
        itemReportes.setFont(fuenteMenu);
        itemCerrarSesion.setFont(fuenteMenu);

        menuBar.add(itemLibros);
        menuBar.add(itemEstudiantes);
        menuBar.add(itemPrestamos);
        menuBar.add(itemReportes);
        menuBar.add(itemCerrarSesion);

        setJMenuBar(menuBar);
    }

    private void construirLayout() {
        setLayout(new BorderLayout());
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        textoFondoLabel = new JLabel("", SwingConstants.CENTER);
        textoFondoLabel.setFont(FUENTE_CAMPO.deriveFont(Font.ITALIC, 14f));
        textoFondoLabel.setForeground(Color.GRAY);
        panelCentral.add(textoFondoLabel, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);
    }

    public void establecerMensajeFondo(String nombreAdmin) {
        textoFondoLabel.setText(
                "Bienvenido/a al sistema, " + nombreAdmin + ". " +
                        "Seleccione una opción del menú superior para comenzar a operar.");
    }

    public void addLibrosMenuListener(ActionListener listener) {
        itemLibros.addActionListener(listener);
    }

    public void addEstudiantesMenuListener(ActionListener listener) {
        itemEstudiantes.addActionListener(listener);
    }

    public void addPrestamosMenuListener(ActionListener listener) {
        itemPrestamos.addActionListener(listener);
    }

    public void addReportesMenuListener(ActionListener listener) {
        itemReportes.addActionListener(listener);
    }

    public void addCerrarSesionMenuListener(ActionListener listener) {
        itemCerrarSesion.addActionListener(listener);
    }

    public void setMenuItemsHabilitados(boolean habilitado) {
        itemLibros.setEnabled(habilitado);
        itemEstudiantes.setEnabled(habilitado);
        itemPrestamos.setEnabled(habilitado);
        itemReportes.setEnabled(habilitado);
        itemCerrarSesion.setEnabled(habilitado);
    }
}
