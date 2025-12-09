
package Vista;

import Modelo.CatalogosProductos;
import Modelo.DetalleProducto;
import Modelo.DetalleProductoDAO;
import Modelo.InventarioDAO;
import Modelo.ProductoDAO;
import Modelo.login;
import Reportes.TicketVenta;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class Carrito extends javax.swing.JPanel {

    /**
     * Creates new form Carrito
     */
    
    private int idUsuario;

    public Carrito(int idUsuario) {
        
        this.idUsuario = idUsuario;
        initComponents();
        configurarModeloCarrito(TablaCarritoCompras);
        configurarPlaceholder(txtCodigoPromocional, "Ingresa Código Promocional");
        txtCodigoPromocional.addActionListener(e -> actualizarResumen());
        this.idUsuario = idUsuario;
          
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    
    
    
    private login usuario;

    public void setUsuario(login usuario) {
        this.usuario = usuario;
    }

    private String nombreCajero;

//    public Carrito() {
//        initComponents();
//        configurarModeloCarrito(TablaCarritoCompras);
//        // Llamar al método que configura el placeholder
//        configurarPlaceholder(txtCodigoPromocional, "Ingresa Código Promocional");
//        txtCodigoPromocional.addActionListener(e -> actualizarResumen());
//
//    }

    private void configurarModeloCarrito(JTable tabla) {
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "ID", "PRODUCTO", "CANTIDAD", "+", "-", "PRECIO UNITARIO", "SUBTOTAL"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo las columnas de los botones (+ y -) son editables
                return column == 3 || column == 4;
            }
        };

        tabla.setModel(modelo);
    }


    // ===== MÉTODOS PARA RECIBIR PRODUCTOS =====

    public void agregarProductoAlCarrito(int idProducto, String nombre, int cantidad, double precioUnitario) {
    javax.swing.table.DefaultTableModel modelo =
        (javax.swing.table.DefaultTableModel) TablaCarritoCompras.getModel();

    // Verificar si el producto ya existe en el carrito para sumar cantidad
    for (int i = 0; i < modelo.getRowCount(); i++) {
        if ((int) modelo.getValueAt(i, 0) == idProducto) { // columna 0 = ID
            int cantidadActual = (int) modelo.getValueAt(i, 2) + cantidad; // columna 2 = CANTIDAD
            double subtotal = cantidadActual * precioUnitario;
            modelo.setValueAt(cantidadActual, i, 2);
            modelo.setValueAt(subtotal, i, 6);
            actualizarResumen();
            return;
        }
    }

    
    double subtotal = cantidad * precioUnitario;
    modelo.addRow(new Object[]{idProducto, nombre, cantidad, "+", "-", precioUnitario, subtotal});

    actualizarResumen();
}

    public void actualizarResumen() {
    DefaultTableModel modelo = (DefaultTableModel) TablaCarritoCompras.getModel();

    double subtotal = 0;
    int totalProductos = 0;

    for (int i = 0; i < modelo.getRowCount(); i++) {
        totalProductos += (int) modelo.getValueAt(i, 2);
        subtotal += (double) modelo.getValueAt(i, 6);
    }

    txtProductosCantidad.setText("Productos (" + totalProductos + ")");
    txtSubtotal.setText(String.format("%.2f", subtotal));

    // --- Validar código promocional ---
    String codigo = txtCodigoPromocional.getText().trim();
    double porcentajeDescuento = obtenerDescuento(codigo);

    // si el placeholder se mantiene, no validar todavía
    if (codigo.equals("Ingresa Código Promocional") || codigo.isEmpty()) {
        txtDescuentoAplicado.setText("0.00");
        txtTotal.setText(String.format("%.2f", subtotal));
        return;
    }

    // Código inválido
    if (porcentajeDescuento == 0) {
        javax.swing.JOptionPane.showMessageDialog(
            this,
            "El código promocional ingresado no existe o no es válido.",
            "Código inválido",
            javax.swing.JOptionPane.WARNING_MESSAGE
        );
        txtDescuentoAplicado.setText("0.00");
        txtTotal.setText(String.format("%.2f", subtotal));
        return;
    }

    // Código válido
    double descuento = subtotal * porcentajeDescuento;
    txtDescuentoAplicado.setText(String.format("- %.2f", descuento));
    txtTotal.setText(String.format("%.2f", subtotal - descuento));
}


    private void configurarPlaceholder(JTextField campo, String textoPlaceholder) {
        campo.setText(textoPlaceholder);
        campo.setForeground(Color.GRAY);

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (campo.getText().equals(textoPlaceholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (campo.getText().isEmpty()) {
                    campo.setText(textoPlaceholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
    }
    
    private double obtenerDescuento(String codigo) {
    switch (codigo.toUpperCase()) {
        case "DESCUENTO10": return 0.10;   // 10% de descuento
        case "DESCUENTO20": return 0.20;   // 20% de descuento
        case "DESCUENTO50": return 0.50;   // 50% de descuento
        default: return 0.0;               // código inválido
    }
}
    
    public void generarTicket() {
    // Crear lista de productos desde la tabla del carrito
    List<CatalogosProductos> productosParaTicket = new ArrayList<>();
    DefaultTableModel modelo = (DefaultTableModel) TablaCarritoCompras.getModel();

    for (int i = 0; i < modelo.getRowCount(); i++) {
        CatalogosProductos p = new CatalogosProductos();
        p.setNombre(modelo.getValueAt(i, 1).toString());
        p.setCantidadSeleccionada((int) modelo.getValueAt(i, 2));
        p.setPrecio((double) modelo.getValueAt(i, 5));
        productosParaTicket.add(p);
    }

    // Totales
    double subtotal = Double.parseDouble(txtSubtotal.getText());

    // Validar descuento para evitar NumberFormatException
    double descuento = 0.0;
    String descText = txtDescuentoAplicado.getText().replace("-", "").trim();
    if (!descText.isEmpty()) {
        try {
            descuento = Double.parseDouble(descText);
        } catch (NumberFormatException ex) {
            descuento = 0.0;
        }
    }

    double totalPagar = Double.parseDouble(txtTotal.getText());
    String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());

    // Crear ticket con número único
    TicketVenta ticket = new TicketVenta(this.idUsuario); // usa el ID del usuario logueado
    ticket.setCarrito(this);
    String numeroTicket = ticket.generarNoTicket();



    ticket.llenarTicket(
        nombreCajero != null ? nombreCajero : "Desconocido",
        fecha,
        numeroTicket,
        productosParaTicket,
        subtotal,
        subtotal * 0.16,
        descuento,
        totalPagar
    );

    // Mostrar ticket en ventana
    javax.swing.JFrame ventana = new javax.swing.JFrame("Ticket de Venta");
    javax.swing.JScrollPane scrollTicket = new javax.swing.JScrollPane(ticket);
    scrollTicket.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollTicket.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    ventana.add(scrollTicket);
    ventana.setSize(700, 500);
    ventana.setLocationRelativeTo(null);
    ventana.setVisible(true);

    
}


    public void setNombreCajero(String nombre) {
    this.nombreCajero = nombre;
}

    public void limpiarCarrito() {
    DefaultTableModel modelo = (DefaultTableModel) TablaCarritoCompras.getModel();
    modelo.setRowCount(0);

    txtProductosCantidad.setText("Productos (0)");
    txtSubtotal.setText("0.00");
    txtDescuentoAplicado.setText("0.00");
    txtTotal.setText("0.00");
    txtCodigoPromocional.setText("Ingresa Código Promocional");
    txtCodigoPromocional.setForeground(Color.GRAY);
}

    // Método para validar antes de generar el ticket
    private boolean validarCarrito() {
    DefaultTableModel modelo = (DefaultTableModel) TablaCarritoCompras.getModel();

    // Validar que haya al menos un producto
    if (modelo.getRowCount() == 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Debes agregar al menos un producto al carrito antes de continuar.",
            "Carrito vacío",
            javax.swing.JOptionPane.WARNING_MESSAGE
        );
        return false;
    }

    // Validar subtotal
    try {
        double subtotal = Double.parseDouble(txtSubtotal.getText());
        if (subtotal <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "El subtotal debe ser mayor a 0.",
                "Subtotal inválido",
                javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
    } catch (NumberFormatException e) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El subtotal no es válido.",
            "Error de formato",
            javax.swing.JOptionPane.WARNING_MESSAGE
        );
        return false;
    }

    // Validar total
    try {
        double total = Double.parseDouble(txtTotal.getText());
        if (total <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "El total debe ser mayor a 0.",
                "Total inválido",
                javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
    } catch (NumberFormatException e) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El total no es válido.",
            "Error de formato",
            javax.swing.JOptionPane.WARNING_MESSAGE
        );
        return false;
    }

    // Si todo está correcto
    return true;
}



    private boolean verificarStockAntesDeVender() {

    DetalleProductoDAO detalleDAO = new DetalleProductoDAO();
    InventarioDAO inventarioDAO = new InventarioDAO();
    ProductoDAO productoDAO = new ProductoDAO();

    DefaultTableModel modelo = (DefaultTableModel) TablaCarritoCompras.getModel();
    
    
    for (int i = 0; i < modelo.getRowCount(); i++) {

        int idProducto = (int) modelo.getValueAt(i, 0);     // Columna ID del producto
        int cantidadProducto = (int) modelo.getValueAt(i, 2); // Columna CANTIDAD
        String nombreProducto = modelo.getValueAt(i, 1).toString();

        // Si no sacas el ID desde la tabla, entonces usa:
        // int idProducto = productoDAO.obtenerIdPorNombre(nombreProducto);
        // y valida -1

        System.out.println("=== Verificación de stock ===");
        System.out.println("Producto: " + nombreProducto + ", ID=" + idProducto + ", Cantidad=" + cantidadProducto);

        if (idProducto <= 0) {
            JOptionPane.showMessageDialog(this,
                "El producto '" + nombreProducto + "' no tiene ID válido.",
                "Error de producto",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        // Lista de artículos que componen el producto
        List<DetalleProducto> lista = detalleDAO.obtenerArticulosPorProducto(idProducto);

        for (DetalleProducto dp : lista) {
        int idArticulo = dp.getIdArticulo();
        int cantidadNecesaria = dp.getCantidad() * cantidadProducto;

        int stockActual = inventarioDAO.obtenerStock(idArticulo);

        // Debug en consola
        System.out.println("Artículo ID=" + idArticulo +
                           ", Necesario=" + cantidadNecesaria +
                           ", Stock=" + stockActual);

        if (stockActual < cantidadNecesaria) {
            JOptionPane.showMessageDialog(this,
                "No hay suficiente stock del artículo ID: " + idArticulo +
                "\nRequerido: " + cantidadNecesaria +
                "\nDisponible: " + stockActual,
                "Stock insuficiente",
                JOptionPane.WARNING_MESSAGE
            );
            return false;  // Detener la venta
            }
        }
    }

    return true;  // Todo OK
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaCarritoCompras = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtProductosCantidad = new javax.swing.JTextField();
        txtCodigoPromocional = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        btnContinuarCompra = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDescuentoAplicado = new javax.swing.JTextField();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        TablaCarritoCompras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID PRODUCTO", "PRODUCTO", "CANTIDAD", "PRECIO UNITARIO", "SUBTOTAL"
            }
        ));
        TablaCarritoCompras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaCarritoComprasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TablaCarritoCompras);
        if (TablaCarritoCompras.getColumnModel().getColumnCount() > 0) {
            TablaCarritoCompras.getColumnModel().getColumn(0).setPreferredWidth(60);
            TablaCarritoCompras.getColumnModel().getColumn(1).setPreferredWidth(50);
            TablaCarritoCompras.getColumnModel().getColumn(2).setPreferredWidth(60);
            TablaCarritoCompras.getColumnModel().getColumn(3).setPreferredWidth(60);
        }

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Resumen");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Total:");

        btnContinuarCompra.setBackground(new java.awt.Color(255, 255, 255));
        btnContinuarCompra.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnContinuarCompra.setForeground(new java.awt.Color(255, 153, 0));
        btnContinuarCompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/continuar.png"))); // NOI18N
        btnContinuarCompra.setText("CONTINUAR CON LA COMPRA");
        btnContinuarCompra.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnContinuarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarCompraActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Subtotal:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Descuento:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(165, 165, 165)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel2)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtProductosCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtCodigoPromocional, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtDescuentoAplicado, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(btnContinuarCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                    .addComponent(txtSubtotal))))
                        .addGap(0, 227, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProductosCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigoPromocional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDescuentoAplicado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(btnContinuarCompra)
                .addGap(28, 28, 28))
        );

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 530));
    }// </editor-fold>//GEN-END:initComponents

    private void TablaCarritoComprasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaCarritoComprasMouseClicked
        // TODO add your handling code here:
        int fila = TablaCarritoCompras.getSelectedRow();
    int columna = TablaCarritoCompras.getSelectedColumn();

    javax.swing.table.DefaultTableModel modelo =
        (javax.swing.table.DefaultTableModel) TablaCarritoCompras.getModel();

    if (fila >= 0) {
        int cantidad = (int) modelo.getValueAt(fila, 2);
        double precio = (double) modelo.getValueAt(fila, 5);

        // boton +
        if (columna == 3) {
            cantidad++;
        }

        // boton -
        if (columna == 4) {
            if (cantidad > 1) {
                cantidad--;
            } else {
                modelo.removeRow(fila);
                actualizarResumen();
                return;
            }
        }

        double subtotal = cantidad * precio;
        modelo.setValueAt(cantidad, fila, 2);
        modelo.setValueAt(subtotal, fila, 6);

        actualizarResumen();
    }
    }//GEN-LAST:event_TablaCarritoComprasMouseClicked

    private void btnContinuarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuarCompraActionPerformed
        // TODO add your handling code here:
        // Validar antes de generar ticket
    if (!validarCarrito()) {
        return; // Si falla alguna validación, no continúa
    }
    
    if (!verificarStockAntesDeVender()) {
        return;  // No continúa
    }
    
    // Si pasa las validaciones, generar ticket
        generarTicket();
    }//GEN-LAST:event_btnContinuarCompraActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TablaCarritoCompras;
    private javax.swing.JButton btnContinuarCompra;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCodigoPromocional;
    private javax.swing.JTextField txtDescuentoAplicado;
    private javax.swing.JTextField txtProductosCantidad;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
