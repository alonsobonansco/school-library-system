package cl.duoc.schoollibrary.view;

import javax.swing.*;

public abstract class VentanaBase extends JFrame {

    public VentanaBase(String titulo, int ancho, int alto) {
        setTitle(titulo);
        setSize(ancho, alto);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
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

    public void cerrarVentana() {
        SwingUtilities.invokeLater(this::dispose);
    }
}
