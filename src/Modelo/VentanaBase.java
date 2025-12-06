
package Modelo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class VentanaBase extends JFrame {
  
    public VentanaBase(String titulo) {
        super(titulo);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Listener para redimensionar
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ajustarComponentes();
            }
        });
    }

    // Método que cada ventana concreta puede sobreescribir
    protected void ajustarComponentes() {
        // Aquí puedes aplicar lógica común de escalado
    }

    // Método utilitario para añadir componentes con GridBagLayout
    protected void addComponent(JPanel panel, Component comp,
                                int x, int y, int w, int h,
                                double weightx, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(comp, gbc);
    }
}
