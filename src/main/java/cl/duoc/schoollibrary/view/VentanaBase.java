package cl.duoc.schoollibrary.view;

import javax.swing.*;


public abstract class VentanaBase extends JFrame {

    public VentanaBase(String titulo, int ancho, int alto) {
        setTitle(titulo);
        setSize(ancho, alto);
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void mostrarMensajeConfirmacion(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, mensaje, "Proceso Exitoso", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void mostrarMensajeError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, mensaje, "Error del Sistema", JOptionPane.ERROR_MESSAGE);
        });
    }

    public boolean confirmarEliminacion(String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el " + mensaje + " seleccionado?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        return opcion == JOptionPane.YES_OPTION;
    }

    public void cerrarVentana() {
        SwingUtilities.invokeLater(this::dispose);
    }
}
