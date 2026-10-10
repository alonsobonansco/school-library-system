package cl.duoc.schoollibrary.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


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
