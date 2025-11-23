
package Vista;
import Modelo.ArticuloDAO;
import Modelo.DetalleProductoDAO;
import Modelo.InventarioDAO;
import Modelo.Producto;
import Modelo.ProductoDAO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class CatalogoProductos extends javax.swing.JFrame {


    private ProductoDAO productoDAO = new ProductoDAO();
    private DetalleProductoDAO detalleDAO = new DetalleProductoDAO();
    private ArticuloDAO articuloDAO = new ArticuloDAO();
    private InventarioDAO inventarioDAO = new InventarioDAO();

    private JPanel panelContenedor;
    private JScrollPane scrollPane;
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private Carrito carrito;
    private JPanel panelCatalogo;
    private JSplitPane split;
    private JScrollPane scrollCarrito;
    private JScrollPane scrollCatalogo;

    // referencia a la ventana principal
    private SistemaPrincipal ventanaPrincipal;

    public CatalogoProductos(Carrito carrito, SistemaPrincipal ventanaPrincipal) {
        this.carrito = carrito;
        this.ventanaPrincipal = ventanaPrincipal;

        setTitle("Catálogo de Productos");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Panel catálogo con scroll ---
        panelCatalogo = new JPanel();
        panelCatalogo.setLayout(new GridLayout(0, 3, 10, 10));
        panelCatalogo.setBackground(Color.WHITE);

        scrollCatalogo = new JScrollPane(panelCatalogo);
        scrollCatalogo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollCatalogo.getViewport().setBackground(Color.WHITE);

        cargarProductos(); // ← llenar catálogo

        // --- Botón regresar fijo abajo ---
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.setBackground(new Color(240, 240, 240));
        btnRegresar.setFocusPainted(false);

        btnRegresar.addActionListener(e -> {
            ventanaPrincipal.setVisible(true); // ← muestra la misma instancia
            this.dispose();                    // ← cierra catálogo
        });

        // Panel izquierdo con catálogo + botón regresar
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(scrollCatalogo, BorderLayout.CENTER);
        panelIzquierdo.add(btnRegresar, BorderLayout.SOUTH);

        // --- Panel derecho: carrito ---
        carrito.setPreferredSize(new Dimension(650, 650));
        scrollCarrito = new JScrollPane(carrito);

        // --- Separador de paneles ---
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, scrollCarrito);
        split.setDividerLocation(520);
        split.setDividerSize(5);

        add(split, BorderLayout.CENTER);
    }

     private void cargarProductos() {
    try {
        List<Producto> lista = productoDAO.listar();
        for (Producto p : lista) {

            JPanel panelProd = new JPanel();
            panelProd.setBackground(Color.WHITE);
            panelProd.setPreferredSize(new Dimension(200, 260));
            panelProd.setLayout(new BoxLayout(panelProd, BoxLayout.Y_AXIS));
            panelProd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            // Imagen
            JLabel lblImagen = new JLabel();
            lblImagen.setPreferredSize(new Dimension(180, 150));
            lblImagen.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblImagen.setOpaque(true); // ← necesario para pintar fondo
            lblImagen.setBackground(Color.WHITE); // ← fondo blanco
//            lblImagen.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // marco sutil

            String ruta = p.getUrlImagen();
            if (ruta != null && !ruta.isEmpty()) {
                File archivo = new File(ruta);
                if (archivo.exists()) {
                    ImageIcon icon = new ImageIcon(ruta);
                    Image img = icon.getImage().getScaledInstance(180, 150, Image.SCALE_SMOOTH);
                    lblImagen.setIcon(new ImageIcon(img));
                } else {
                    lblImagen.setText("Imagen no encontrada");
                    lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
                }
            } else {
                lblImagen.setText("Sin imagen");
                lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
            }

            JLabel lblNombre = new JLabel(p.getNombre());
            lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblNombre.setFont(new Font("Arial", Font.BOLD, 14));

            JLabel lblPrecio = new JLabel("Precio: $" + p.getPrecio());
            lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton btnAgregar = new JButton("Agregar");
            btnAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);

            btnAgregar.addActionListener(e -> {
                String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad a comprar:", "1");
                if (cantidadStr != null && cantidadStr.matches("\\d+") && Integer.parseInt(cantidadStr) > 0) {
                    int cantidad = Integer.parseInt(cantidadStr);
                    carrito.agregarProductoAlCarrito(p.getId(), p.getNombre(), cantidad, p.getPrecio());
                } else {
                    JOptionPane.showMessageDialog(this, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            panelProd.add(Box.createRigidArea(new Dimension(0, 5)));
            panelProd.add(lblImagen);
            panelProd.add(lblNombre);
            panelProd.add(lblPrecio);
            panelProd.add(btnAgregar);

            panelCatalogo.add(panelProd);
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error cargando productos: " + e.getMessage());
    }
}

     

   



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CatalogoProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CatalogoProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CatalogoProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CatalogoProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Carrito carrito = new Carrito();
                SistemaPrincipal menu = new SistemaPrincipal();
                new CatalogoProductos(carrito, menu).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
