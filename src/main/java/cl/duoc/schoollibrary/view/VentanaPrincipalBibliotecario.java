package cl.duoc.schoollibrary.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Pantalla raíz e interfaz principal para el Administrador / Bibliotecario.
 * Provee la barra de navegación global de la suite mediante JMenuBar.
 */
public class VentanaPrincipalBibliotecario extends VentanaBase {

    private JLabel tituloLabel;
    private JMenuItem itemLibros;
    private JMenuItem itemEstudiantes;
    private JMenuItem itemPrestamos;
    private JMenuItem itemReportes;

    public VentanaPrincipalBibliotecario() {
        super("Panel de Control Administrador - Sistema de Biblioteca", 850, 550);

        inicializarComponentes();
        inicializarMenuBar();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Consola de Gestión Biblioteca Escolar", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 22));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
    }

    private void inicializarMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        itemLibros = new JMenuItem("Catálogo de Libros");
        itemEstudiantes = new JMenuItem("Gestión de Estudiantes");
        itemPrestamos = new JMenuItem("Control de Préstamos");
        itemReportes = new JMenuItem("Reportes y Estadísticas");

        Font fuenteMenu = new Font("Arial", Font.PLAIN, 13);
        itemLibros.setFont(fuenteMenu);
        itemEstudiantes.setFont(fuenteMenu);
        itemPrestamos.setFont(fuenteMenu);
        itemReportes.setFont(fuenteMenu);

        menuBar.add(itemLibros);
        menuBar.add(itemEstudiantes);
        menuBar.add(itemPrestamos);
        menuBar.add(itemReportes);

        setJMenuBar(menuBar);
    }

    private void construirLayout() {
        setLayout(new BorderLayout());
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        JLabel lblPlaceholder = new JLabel("Seleccione una opción del menú superior para comenzar a operar.", SwingConstants.CENTER);
        lblPlaceholder.setFont(new Font("Arial", Font.ITALIC, 14));
        lblPlaceholder.setForeground(Color.GRAY);
        panelCentral.add(lblPlaceholder, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
}
