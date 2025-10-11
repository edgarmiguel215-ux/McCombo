
package Vista;

import Modelo.Hamburguesa;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.URL;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;


    public class VistaHamburguesaDialog extends JDialog {
    public VistaHamburguesaDialog(JFrame parent, Hamburguesa burger) {
        super(parent, true);
        setTitle(burger.getNombre());
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        //para mostrar titulo en el jdialog de la ventana 
        


        // Imagen
       
        String rutaImagen = burger.getRutaImagen();

       
        String nombreBase = rutaImagen.replace(" ", ""); // sin espacios
        if (!nombreBase.toLowerCase().endsWith(".png") && !nombreBase.toLowerCase().endsWith(".jpg")) {
            nombreBase += ".png";
        }

        // Intentamos varias rutas posibles
        URL imgURL = getClass().getResource("/" + rutaImagen);


        if (imgURL == null) {
            imgURL = getClass().getResource("/Img/" + rutaImagen.replace(" ", ""));
        }
        if (imgURL == null) {
            imgURL = getClass().getResource("/Img/" + nombreBase);
        }
        if (imgURL == null) {
            imgURL = getClass().getResource("/Img/" + rutaImagen.toLowerCase());
        }
        if (imgURL == null) {
            imgURL = getClass().getResource("/Img/" + rutaImagen.replace(" ", "").toLowerCase());
        }
        if (imgURL == null) {
            imgURL = getClass().getResource("/Img/" + nombreBase.toLowerCase());
        }

        // Si aún no se encontró, tratamos de usar la imagen por defecto
        if (imgURL == null) {
            System.err.println("No se encontró la imagen en ninguna variante: /Img/" + rutaImagen);
            imgURL = getClass().getResource("/Img/default.png");
        }

        // Si no hay ni imagen ni default, evitamos el NullPointerException
        ImageIcon icon;
        if (imgURL != null) {
            icon = new ImageIcon(imgURL);
        } else {
            System.err.println("No se encontró ni la imagen ni el recurso por defecto.");
            icon = new ImageIcon();
        }


        JLabel imagenLabel = new JLabel(icon);
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);


        // Ingredientes
        JTextArea ingredientesArea = new JTextArea();
        ingredientesArea.setEditable(false);
        ingredientesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        ingredientesArea.setText(
            burger.getIngredientes().stream()
                .map(i -> "• " + i)
                .collect(Collectors.joining("\n"))
        );
        
        //Agregar titulo
        

        
        // Precio
        JLabel precioLabel = new JLabel("$" + burger.getPrecio());
        precioLabel.setFont(new Font("Arial", Font.BOLD, 16));
        precioLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Botón
        JButton aceptar = new JButton("Aceptar");
        aceptar.addActionListener(e -> dispose());

        // Paneles
        JPanel centro = new JPanel(new GridLayout(1, 2));
        centro.add(imagenLabel);
        centro.add(new JScrollPane(ingredientesArea));

        add(precioLabel, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(aceptar, BorderLayout.SOUTH);
    }
}
