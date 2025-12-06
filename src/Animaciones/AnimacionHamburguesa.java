
package Animaciones;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;


public class AnimacionHamburguesa extends JPanel {
    
    private Image hamburguesa;
    private Image bolsa;
    private int xHamburguesa = -200; // posición inicial fuera de pantalla
    private int yHamburguesa = 100;
    private Timer timer;

    public AnimacionHamburguesa() {
        hamburguesa = new ImageIcon(getClass().getResource("/Img/hamburguesa.png")).getImage();
        bolsa = new ImageIcon(getClass().getResource("/Img/bolsa.png")).getImage();

        // Timer para mover la hamburguesa
        timer = new Timer(20, e -> {
            if (xHamburguesa < 150) { // posición final
                xHamburguesa += 5;
                repaint();
            } else {
                timer.stop(); // detener animación al llegar
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bolsa, 200, 80, 120, 120, this); // dibuja bolsa fija
        g.drawImage(hamburguesa, xHamburguesa, yHamburguesa, 100, 100, this); // dibuja hamburguesa animada
    }
    
}
