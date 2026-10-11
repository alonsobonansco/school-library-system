package cl.duoc.schoollibrary.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Plantilla maestra y componente base de renderizado gráfico (Template Window Pattern).
 * Centraliza de forma unificada los tokens de diseño estético (fuentes y cursores)
 * y encapsula los métodos polimórficos de mensajería modal y confirmaciones binarias
 * para asegurar la consistencia visual y operativa de toda la suite.
 */
public abstract class VentanaBase extends JFrame {

    protected static final Font FUENTE_TITULO = new Font("Arial", Font.BOLD, 18);
    protected static final Font FUENTE_LABEL = new Font("Arial", Font.BOLD, 12);
    protected static final Font FUENTE_CAMPO = new Font("Arial", Font.PLAIN, 12);
    protected static final Font FUENTE_BOTON = new Font("Arial", Font.BOLD, 12);
    protected static final Cursor CURSOR_CLICK = new Cursor(Cursor.HAND_CURSOR);

    public VentanaBase(String titulo, int ancho, int alto) {
        setTitle(titulo);
        setSize(ancho, alto);
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Instancia un modelo de datos estructurado para JTable.
     * Sobreescribe el comportamiento nativo del motor de Swing mediante clases anónimas
     * para bloquear la edición directa sobre las celdas, garantizando que la mutación
     * de registros ocurra exclusivamente a través de los flujos de control autorizados.
     *
     * @param columnas Matriz de cadenas de texto con las cabeceras de la cuadrícula.
     * @return Una instancia optimizada e inmutable de DefaultTableModel.
     */
    protected DefaultTableModel crearModeloTablaNoEditable(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public void mostrarMensajeConfirmacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Proceso Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmarDevolucion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro/a de que desea registrar la devolución?",
                "Confirmar Devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        return opcion == JOptionPane.YES_OPTION;
    }

    public boolean confirmarEliminacion(String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro/a de que desea eliminar el " + mensaje + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        return opcion == JOptionPane.YES_OPTION;
    }

    public void cerrarVentana() {
        this.dispose();
    }
}
