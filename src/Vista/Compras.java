
package Vista;

import Modelo.CompraDAO;
import Modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Modelo.CompraDetalle;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;



public class Compras extends javax.swing.JFrame {

    CompraDAO dao = new CompraDAO();
    private Inventario inventarioPanel;
    DefaultTableModel modelo = new DefaultTableModel();
    int idCompraSeleccionada = -1;

    
    ArticuloDAO articuloDAO = new ArticuloDAO();
    ProveedorDao proveedorDAO = new ProveedorDao();
    
    public Compras(Inventario inventarioPanel) {
        this.inventarioPanel = inventarioPanel;
        initComponents();
        cargarCombos();
        configurarTablaCompras();
        listarDetalleCompras();
        configurarListeners();
        txtTotal.setEditable(false);
        txtIdCompras.setEditable(false);
        txtIdCompras.setVisible(true);
    }

    private void actualizarInventario() {
        System.out.println("=== ACTUALIZAR INVENTARIO ===");
        System.out.println("inventarioPanel: " + (inventarioPanel != null ? "NO null" : "NULL"));
        System.out.println("Filas en modelo: " + modelo.getRowCount());

        if (inventarioPanel != null) {
            inventarioPanel.cargarDesdeCompras(modelo);
        } else {
            System.out.println("ERROR: inventarioPanel es null - no se puede actualizar inventario");
            if (inventarioVentana != null) {
                System.out.println("Usando inventarioVentana como respaldo");
                inventarioVentana.cargarDesdeCompras(modelo);
            }
        }
    }

    private Inventario inventarioVentana;
    public void setInventarioVentana(Inventario inventarioVentana) {
        this.inventarioVentana = inventarioVentana;
    }


    private void cargarCombos() {
    // Cargar proveedores
    List<Proveedor> proveedores = proveedorDAO.listar();
    comboProveedor.removeAllItems();
    for (Proveedor p : proveedores) {
        comboProveedor.addItem(p);
    }

    // Cargar artículos (no productos)
    List<Articulo> articulos = articuloDAO.listar();
    comboArticulo.removeAllItems();
    for (Articulo a : articulos) {
        comboArticulo.addItem(a);
    }
}


    // MÉTODO PÚBLICO PARA ACTUALIZAR SOLO LOS ARTÍCULOS
    public void recargarArticulos() {
    List<Articulo> articulos = articuloDAO.listar();
    comboArticulo.removeAllItems();
    for (Articulo a : articulos) {
        comboArticulo.addItem(a);
    }
    System.out.println("✅ Combo de artículos actualizado desde Administración.");
}

    

    private void configurarTablaCompras() {
        modelo = new DefaultTableModel();
        modelo.addColumn("Artículo");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Unidad");
        modelo.addColumn("Precio Unitario");
        modelo.addColumn("Total");
        modelo.addColumn("Numero");
        modelo.addColumn("idProducto");
        modelo.addColumn("Proveedor");
        modelo.addColumn("Comprobante");
        modelo.addColumn("Metodo de Pago");
        modelo.addColumn("Fecha");
        modelo.addColumn("Estado");
        modelo.addColumn("idCompra");

        tablaCompras.setModel(modelo);

        int[] columnasOcultas = {6, 12};
        for (int col : columnasOcultas) {
            tablaCompras.getColumnModel().getColumn(col).setMinWidth(0);
            tablaCompras.getColumnModel().getColumn(col).setMaxWidth(0);
            tablaCompras.getColumnModel().getColumn(col).setWidth(0);
        }
    }

    private void configurarListeners() {
        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calcularSubtotal(); }
            public void removeUpdate(DocumentEvent e) { calcularSubtotal(); }
            public void changedUpdate(DocumentEvent e) { calcularSubtotal(); }
        };
        txtCantidad.getDocument().addDocumentListener(listener);
        txtPrecio.getDocument().addDocumentListener(listener);
    }

    private void calcularSubtotal() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            double subtotal = cantidad * precio;
            txtTotal.setText(String.format("%.2f", subtotal));
        } catch (NumberFormatException e) {
            txtTotal.setText("0.00");
        }
    }

    void calcularTotalCompra() {
        double total = 0;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            Object subtotalObj = modelo.getValueAt(i, 4);
            if (subtotalObj != null) {
                try {
                    total += Double.parseDouble(subtotalObj.toString());
                } catch (NumberFormatException e) {
                    System.out.println("Error en fila " + i + ": " + e.getMessage());
                }
            }
        }
        txtTotal.setText(String.format("%.2f", total));
    }

    void limpiarCampos() {
        modelo.setRowCount(0);
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtTotal.setText("");
        txtNumero.setText("");
        txtIdCompras.setText("");
    }

    void listarDetalleCompras() {
        List<CompraDetalle> lista = dao.listarDetalles();
        modelo.setRowCount(0);
        for (CompraDetalle c : lista) {
            modelo.addRow(new Object[]{
                c.getArticulo(), c.getCantidad(), c.getUnidad(), c.getPrecio(),
                c.getSubtotal(), c.getNumero(), c.getIdProducto(), c.getProveedor(),
                c.getComprobante(), c.getMetodoPago(), c.getFecha(),
                c.getEstado(), c.getIdCompra()
            });
        }
    }

    private void agregarArticulo() {
        if (comboArticulo.getSelectedItem() != null &&
            !txtCantidad.getText().isEmpty() && !txtPrecio.getText().isEmpty()) {

            try {
                Articulo articulo = (Articulo) comboArticulo.getSelectedItem();
                Proveedor proveedor = (Proveedor) comboProveedor.getSelectedItem();
                int cantidad = Integer.parseInt(txtCantidad.getText());
                double precio = Double.parseDouble(txtPrecio.getText());
                double subtotal = cantidad * precio;

                String idCompraStr = txtIdCompras.getText().trim();
                Object idCompraValue = idCompraStr.isEmpty() ? "" : Integer.parseInt(idCompraStr);

                modelo.addRow(new Object[]{
                    articulo.getNombre(), cantidad, txtUnidadCompra.getText(), precio, subtotal,
                    txtNumero.getText().trim(), articulo.getId(), proveedor.getNombre(),
                    comboComprobante.getSelectedItem(), comboMetodo.getSelectedItem(),
                    jDateChooserFecha.getDate(), comboEstado.getSelectedItem(), idCompraValue
                });

                calcularTotalCompra();
                limpiarCamposArticulo();
                actualizarInventario();

                if (inventarioPanel != null) {
                    inventarioPanel.cargarDesdeCompras(modelo);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Cantidad o precio inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos del artículo.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarCamposArticulo() {
        txtCantidad.setText("");
        txtPrecio.setText("");
    }


    private void cargarCompraPendiente() {
    try {
        Compra compra = dao.obtenerCompraPendiente();
        if (compra != null) {
            // Cargar datos en los campos del formulario
            txtIdCompras.setText(String.valueOf(compra.getId()));
            txtNumero.setText(compra.getNumero()); // Cargar número
            comboComprobante.setSelectedItem(compra.getComprobante());
            
            // Cargar proveedor correctamente
            if (comboProveedor.getItemCount() > 0) {
                for (int i = 0; i < comboProveedor.getItemCount(); i++) {
                    Proveedor prov = (Proveedor) comboProveedor.getItemAt(i);
                    if (prov.getNombre().equals(compra.getProveedor())) {
                        comboProveedor.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            comboMetodo.setSelectedItem(compra.getMetodoPago());
            comboEstado.setSelectedItem(compra.getEstado());
            jDateChooserFecha.setDate(compra.getFechaCompra());

            // Limpiar y cargar la tabla con TODOS los detalles
            modelo.setRowCount(0);
            List<CompraDetalle> detalles = dao.listarDetallesPorCompra(compra.getId());
            
            for (CompraDetalle det : detalles) {
                modelo.addRow(new Object[]{
                    det.getArticulo(),        // 0 - Artículo
                    det.getCantidad(),        // 1 - Cantidad
                    det.getUnidad(),          // 2 - Unidad
                    det.getPrecio(),          // 3 - Precio Unitario
                    det.getSubtotal(),        // 4 - Total
                    det.getNumero(),          // 5 - Número (¡Ahora viene del JOIN!)
                    det.getIdProducto(),      // 6 - idProducto
                    det.getProveedor(),       // 7 - Proveedor
                    det.getComprobante(),     // 8 - Comprobante
                    det.getMetodoPago(),      // 9 - Método de Pago
                    det.getFecha(),           // 10 - Fecha
                    det.getEstado(),          // 11 - Estado
                    det.getIdCompra()         // 12 - idCompra
                });
            }

            calcularTotalCompra();
            
            // Debug: Verificar que se cargaron los datos correctamente
            System.out.println("Compra pendiente cargada - ID: " + compra.getId() + 
                             ", Número: " + compra.getNumero() + 
                             ", Items: " + detalles.size());
            
            // Verificar que los detalles tengan número
            for (CompraDetalle det : detalles) {
                System.out.println("Detalle - Artículo: " + det.getArticulo() + 
                                 ", Número: " + det.getNumero() +
                                 ", Unidad: " + det.getUnidad());
            }
        } else {
            System.out.println("No hay compras pendientes");
            // Limpiar campos si no hay compra pendiente
            limpiarFormularioCompras();
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar la compra pendiente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void limpiarFormularioCompras() {
    int confirmacion = JOptionPane.showConfirmDialog(
        this, 
        "¿Está seguro de que desea limpiar todos los campos del formulario?\nLos datos de la tabla permanecerán intactos.", 
        "Confirmar limpieza", 
        JOptionPane.YES_NO_OPTION, 
        JOptionPane.QUESTION_MESSAGE
    );
    
    if (confirmacion == JOptionPane.YES_OPTION) {
        // Limpiar campos de artículo
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtTotal.setText("");
        txtUnidadCompra.setText("");
        
        // Limpiar campos principales
        txtNumero.setText("");
        txtIdCompras.setText("");
        
        // Resetear combos a valores por defecto
        comboArticulo.setSelectedIndex(-1); // ← Esto es importante
        if (comboProveedor.getItemCount() > 0) comboProveedor.setSelectedIndex(0);
        if (comboComprobante.getItemCount() > 0) comboComprobante.setSelectedIndex(0);
        if (comboMetodo.getItemCount() > 0) comboMetodo.setSelectedIndex(0);
        if (comboEstado.getItemCount() > 0) comboEstado.setSelectedIndex(0);
        
        // Limpiar fecha
        jDateChooserFecha.setDate(null);
        
        JOptionPane.showMessageDialog(this, "Formulario limpiado correctamente.\nLos datos de la tabla se mantienen.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}

    // NUEVO MÉTODO PARA SINCRONIZACIÓN COMPLETA
    public void sincronizarConInventario() {
        System.out.println("=== SINCRONIZANDO COMPRA CON INVENTARIO ===");
        
        if (inventarioPanel != null) {
            // Pasar TODAS las filas del modelo actual
            inventarioPanel.cargarDesdeCompras(modelo);
            System.out.println("Inventario actualizado con " + modelo.getRowCount() + " artículos");
        } else {
            System.out.println(" inventarioPanel es null - no se puede sincronizar");
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        comboArticulo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        comboComprobante = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        comboProveedor = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        btnComprarCompra = new javax.swing.JButton();
        btnEditarCompra = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jDateChooserFecha = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        comboEstado = new javax.swing.JComboBox<>();
        comboMetodo = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCompras = new javax.swing.JTable();
        txtIdCompras = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtUnidadCompra = new javax.swing.JTextField();
        btnLimiparCamposCompras = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Articulo:");

        jLabel2.setText("Cantidad:");

        txtCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadActionPerformed(evt);
            }
        });

        jLabel3.setText("Precio Unitario:");

        jLabel4.setText("Total:");

        jLabel5.setText("Comprobante:");

        comboComprobante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FACTURA", "RECIBO", "REMISION" }));

        jLabel6.setText("Numero:");

        jLabel7.setText("Metodo de Pago");

        jLabel8.setText("Proveedor:");

        comboProveedor.setEditable(true);

        jLabel9.setText("Ingrese la Informacion del cliente.");

        btnComprarCompra.setText("Comprar");
        btnComprarCompra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnComprarCompraMouseClicked(evt);
            }
        });
        btnComprarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComprarCompraActionPerformed(evt);
            }
        });

        btnEditarCompra.setText("Editar");
        btnEditarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarCompraActionPerformed(evt);
            }
        });

        jLabel10.setText("Fecha de compra:");

        jLabel11.setText("Estado de Compra:");

        comboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendiente", "Recibido", "Parcial", "Cancelado" }));

        comboMetodo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Transferencia", "Crédito", " " }));

        tablaCompras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Articulo", "Cantidad", "Precio Unitario", "Total", "Numero", "Proveedor", "Comprobante", "Metodo de Pago", "Fecha", "Estado"
            }
        ));
        tablaCompras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaComprasMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaCompras);
        if (tablaCompras.getColumnModel().getColumnCount() > 0) {
            tablaCompras.getColumnModel().getColumn(0).setPreferredWidth(60);
            tablaCompras.getColumnModel().getColumn(1).setPreferredWidth(60);
            tablaCompras.getColumnModel().getColumn(2).setPreferredWidth(60);
            tablaCompras.getColumnModel().getColumn(3).setPreferredWidth(60);
            tablaCompras.getColumnModel().getColumn(4).setPreferredWidth(60);
            tablaCompras.getColumnModel().getColumn(5).setPreferredWidth(60);
            tablaCompras.getColumnModel().getColumn(6).setPreferredWidth(60);
            tablaCompras.getColumnModel().getColumn(7).setPreferredWidth(60);
            tablaCompras.getColumnModel().getColumn(9).setPreferredWidth(60);
        }

        jButton1.setText("Regresar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel12.setText("Unidad:");

        btnLimiparCamposCompras.setText("Limpiar");
        btnLimiparCamposCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimiparCamposComprasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboComprobante, 0, 177, Short.MAX_VALUE)
                        .addGap(32, 32, 32)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNumero, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(comboMetodo, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(comboArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtUnidadCompra)
                        .addGap(42, 42, 42)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(192, 192, 192))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addGap(208, 208, 208)
                                .addComponent(jLabel9))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(jDateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(comboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimiparCamposCompras)
                                .addGap(18, 18, 18)
                                .addComponent(btnComprarCompra)
                                .addGap(18, 18, 18)
                                .addComponent(btnEditarCompra)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtIdCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jButton1)))
                .addGap(8, 8, 8)
                .addComponent(txtIdCompras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(comboArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtUnidadCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(comboComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(comboProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboMetodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(comboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnComprarCompra)
                        .addComponent(btnEditarCompra)
                        .addComponent(btnLimiparCamposCompras))
                    .addComponent(jDateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1100, 550));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadActionPerformed

    private void btnComprarCompraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnComprarCompraMouseClicked
        // TODO add your handling code here:
                                                    
    }//GEN-LAST:event_btnComprarCompraMouseClicked

    private void btnEditarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarCompraActionPerformed
        // TODO add your handling code here:  
    try {
    // Validar que hay un ID de compra
    if (txtIdCompras.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "No hay una compra seleccionada para editar.");
        return;
    }

    int idCompra = Integer.parseInt(txtIdCompras.getText().trim());

    // Obtener la fila seleccionada
    int filaSeleccionada = tablaCompras.getSelectedRow();
    if (filaSeleccionada < 0) {
        JOptionPane.showMessageDialog(this, "Seleccione una fila de la tabla para editar.");
        return;
    }

    // Obtener datos del formulario principal
    String numero = txtNumero.getText().trim();
    String comprobante = comboComprobante.getSelectedItem().toString();
    String metodoPago = comboMetodo.getSelectedItem().toString();
    String proveedor = comboProveedor.getSelectedItem().toString();
    String estado = comboEstado.getSelectedItem().toString();
    Date fecha = jDateChooserFecha.getDate();

    if (fecha == null) {
        JOptionPane.showMessageDialog(this, "Seleccione una fecha válida.");
        return;
    }

    // ✅ Obtener el artículo seleccionado (ya contiene el ID real del producto)
    Articulo articuloSeleccionado = (Articulo) comboArticulo.getSelectedItem();

    int cantidad = Integer.parseInt(txtCantidad.getText().trim());
    double precio = Double.parseDouble(txtPrecio.getText().trim());
    double subtotal = cantidad * precio;
    String unidad = txtUnidadCompra.getText().trim();

    // Obtener el idProducto ORIGINAL de la fila seleccionada
    Object idProductoOriginalObj = modelo.getValueAt(filaSeleccionada, 6);
    if (idProductoOriginalObj == null) {
        JOptionPane.showMessageDialog(this, "Error: No se pudo obtener el ID del producto original.");
        return;
    }
    int idProductoOriginal = Integer.parseInt(idProductoOriginalObj.toString());

    System.out.println("Editando artículo - Original ID: " + idProductoOriginal + ", Nuevo ID: " + articuloSeleccionado.getId());

    // Recalcular el total - actualizando solo el artículo editado
    double nuevoTotal = 0;
    for (int i = 0; i < modelo.getRowCount(); i++) {
        Object idCompraObj = modelo.getValueAt(i, 12);
        Object subtotalObj = modelo.getValueAt(i, 4);

        if (idCompraObj != null && idCompraObj.toString().equals(String.valueOf(idCompra)) && subtotalObj != null) {
            if (i == filaSeleccionada) {
                nuevoTotal += subtotal; // nuevo subtotal editado
            } else {
                nuevoTotal += Double.parseDouble(subtotalObj.toString());
            }
        }
    }

    // Crear objeto Compra
    Compra compra = new Compra();
    compra.setId(idCompra);
    compra.setNumero(numero);
    compra.setComprobante(comprobante);
    compra.setMetodoPago(metodoPago);
    compra.setProveedor(proveedor);
    compra.setEstado(estado);
    compra.setFechaCompra(fecha);
    compra.setTotal(nuevoTotal);

    // Crear lista de detalles
    List<CompraDetalle> detalles = new ArrayList<>();
    for (int i = 0; i < modelo.getRowCount(); i++) {
        Object idCompraObj = modelo.getValueAt(i, 12);

        if (idCompraObj != null && idCompraObj.toString().equals(String.valueOf(idCompra))) {
            CompraDetalle detalle = new CompraDetalle();
            detalle.setIdCompra(idCompra);

            if (i == filaSeleccionada) {
                detalle.setIdProducto(articuloSeleccionado.getId());
                detalle.setArticulo(articuloSeleccionado.getNombre());
                detalle.setCantidad(cantidad);
                detalle.setUnidad(unidad);
                detalle.setPrecio(precio);
                detalle.setSubtotal(subtotal);
            } else {
                Object idProductoObj = modelo.getValueAt(i, 6);
                detalle.setIdProducto(Integer.parseInt(idProductoObj.toString().trim()));

                detalle.setArticulo(modelo.getValueAt(i, 0).toString().trim());
                detalle.setCantidad(Integer.parseInt(modelo.getValueAt(i, 1).toString().trim()));
                detalle.setUnidad(modelo.getValueAt(i, 2).toString().trim());
                detalle.setPrecio(Double.parseDouble(modelo.getValueAt(i, 3).toString().trim()));
                detalle.setSubtotal(Double.parseDouble(modelo.getValueAt(i, 4).toString().trim()));
            }

            detalle.setProveedor(proveedor);
            detalle.setComprobante(comprobante);
            detalle.setMetodoPago(metodoPago);
            detalle.setFecha(fecha);
            detalle.setEstado(estado);
            detalles.add(detalle);
        }
    }

    System.out.println("Total de detalles a actualizar: " + detalles.size());
    System.out.println("Total de la compra: " + nuevoTotal);

    // Actualizar en la base de datos
    boolean exito = dao.actualizarCompraCompleta(compra, detalles);

    if (exito) {
        JOptionPane.showMessageDialog(this, "Artículo actualizado exitosamente en la compra.");
        
        // ✅ SOLO UNA SINCRONIZACIÓN
        sincronizarConInventario();
        
        listarDetalleCompras();
        limpiarCamposArticulo();
        txtIdCompras.setText("");
        txtNumero.setText("");
        comboComprobante.setSelectedIndex(0);
        comboMetodo.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);
        jDateChooserFecha.setDate(null);
    } else {
        JOptionPane.showMessageDialog(this, "Error al actualizar la compra.");
    }

} catch (NumberFormatException ex) {
    JOptionPane.showMessageDialog(this, "Error en formato de números: " + ex.getMessage());
    ex.printStackTrace();
} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
}

    }//GEN-LAST:event_btnEditarCompraActionPerformed

    private void btnComprarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComprarCompraActionPerformed
                                                     
    if (comboArticulo.getSelectedIndex() == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione un artículo.");
        return;
    }

    if (txtCantidad.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese cantidad y precio.");
        return;
    }

    int cantidad;
    double precio;

    try {
        cantidad = Integer.parseInt(txtCantidad.getText().trim());
        precio = Double.parseDouble(txtPrecio.getText().trim());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Cantidad y precio deben ser numéricos.");
        return;
    }

    double total = cantidad * precio;

    Articulo articuloSeleccionado = (Articulo) comboArticulo.getSelectedItem();
    String unidad = txtUnidadCompra.getText().trim();
    String numero = txtNumero.getText().trim();
    String proveedor = comboProveedor.getSelectedItem().toString();
    String comprobante = comboComprobante.getSelectedItem().toString();
    String metodoPago = comboMetodo.getSelectedItem().toString();
    String estado = comboEstado.getSelectedItem().toString();
    Date fecha = jDateChooserFecha.getDate();

    Compra compra = new Compra();
    compra.setComprobante(comprobante);
    compra.setNumero(numero);
    compra.setMetodoPago(metodoPago);
    compra.setProveedor(proveedor);
    compra.setFechaCompra(fecha);
    compra.setEstado(estado);
    compra.setTotal(total);

    CompraDAO dao = new CompraDAO();
    int idCompraGenerada = dao.registrarCompra(compra);
    txtIdCompras.setText(String.valueOf(idCompraGenerada));

    CompraDetalle detalle = new CompraDetalle();
    detalle.setIdCompra(idCompraGenerada);
    detalle.setIdProducto(articuloSeleccionado.getId());
    detalle.setArticulo(articuloSeleccionado.getNombre());
    detalle.setCantidad(cantidad);
    detalle.setPrecio(precio);
    detalle.setSubtotal(total);
    detalle.setProveedor(proveedor);
    detalle.setComprobante(comprobante);
    detalle.setMetodoPago(metodoPago);
    detalle.setFecha(fecha);
    detalle.setEstado(estado);
    detalle.setUnidad(unidad);

    dao.registrarDetalle(detalle);

    Object[] fila = new Object[]{
        articuloSeleccionado.getNombre(), cantidad, unidad, precio, total,
        numero, articuloSeleccionado.getId(), proveedor, comprobante,
        metodoPago, fecha, estado, idCompraGenerada
    };

    modelo.addRow(fila);
    
    // ✅ SOLO UNA SINCRONIZACIÓN - ELIMINA actualizarInventario()
    sincronizarConInventario();
    
    JOptionPane.showMessageDialog(this, "Compra registrada exitosamente.");

    }//GEN-LAST:event_btnComprarCompraActionPerformed

    private void tablaComprasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaComprasMouseClicked
        // TODO add your handling code here:                                          
    
    // TODO add your handling code here:                                          
                                       
    int fila = tablaCompras.getSelectedRow();
    if (fila < 0) return;

    try {
        System.out.println("=== Cargando datos de fila " + fila + " ===");
        
        // --- Diagnosticar TODAS las columnas
        for (int i = 0; i < modelo.getColumnCount(); i++) {
            Object valor = modelo.getValueAt(fila, i);
            System.out.println("Columna " + i + " (" + tablaCompras.getColumnName(i) + "): " + 
                              (valor != null ? "'" + valor.toString() + "'" : "NULL"));
        }

        // --- Cargar ID de compra desde la columna 12
        Object idCompraObj = modelo.getValueAt(fila, 12);
        if (idCompraObj != null && !idCompraObj.toString().trim().isEmpty()) {
            String idCompraStr = idCompraObj.toString().trim();
            txtIdCompras.setText(idCompraStr);
            System.out.println("ID Compra cargado: " + idCompraStr);

            // ✅ Verificar datos de la compra en DAO
            try {
                int idCompra = Integer.parseInt(idCompraStr);
                dao.verificarDatosCompra(idCompra); // ← Aquí se invoca tu método
            } catch (NumberFormatException e) {
                System.err.println("ID Compra inválido: " + idCompraStr);
            }

        } else {
            txtIdCompras.setText("");
            System.out.println("ID Compra: NO ENCONTRADO");
        }

        // --- Cargar campos específicos con validación EXTRA
        // Unidad (columna 2)
        Object unidadObj = modelo.getValueAt(fila, 2);
        if (unidadObj != null && !unidadObj.toString().trim().isEmpty()) {
            txtUnidadCompra.setText(unidadObj.toString().trim());
            System.out.println("Unidad cargada: " + unidadObj.toString());
        } else {
            txtUnidadCompra.setText("");
            System.out.println("Unidad: NULL o vacía");
        }

        // Número (columna 5)  
        Object numeroObj = modelo.getValueAt(fila, 5);
        if (numeroObj != null && !numeroObj.toString().trim().isEmpty()) {
            txtNumero.setText(numeroObj.toString().trim());
            System.out.println("Número cargado: " + numeroObj.toString());
        } else {
            txtNumero.setText("");
            System.out.println("Número: NULL o vacío");
        }

        // --- Cargar el resto de campos con validaciones directas
        // Cantidad (columna 1)
        Object cantidadObj = modelo.getValueAt(fila, 1);
        if (cantidadObj != null && !cantidadObj.toString().trim().isEmpty()) {
            txtCantidad.setText(cantidadObj.toString().trim());
        } else {
            txtCantidad.setText("");
        }

        // Precio (columna 3)
        Object precioObj = modelo.getValueAt(fila, 3);
        if (precioObj != null && !precioObj.toString().trim().isEmpty()) {
            txtPrecio.setText(precioObj.toString().trim());
        } else {
            txtPrecio.setText("");
        }

        // Total (columna 4)
        Object totalObj = modelo.getValueAt(fila, 4);
        if (totalObj != null && !totalObj.toString().trim().isEmpty()) {
            txtTotal.setText(totalObj.toString().trim());
        } else {
            txtTotal.setText("");
        }

        // --- SOLUCIÓN: Cargar artículo por NOMBRE (problema con ID = 0)
        Object nombreArticuloObj = modelo.getValueAt(fila, 0);
        if (nombreArticuloObj != null && !nombreArticuloObj.toString().trim().isEmpty()) {
            String nombreArticulo = nombreArticuloObj.toString().trim();
            boolean encontrado = false;
            
            System.out.println("Buscando artículo por nombre: '" + nombreArticulo + "'");
            
            // Buscar por nombre exacto en el combo
            for (int i = 0; i < comboArticulo.getItemCount(); i++) {
                Articulo art = (Articulo) comboArticulo.getItemAt(i);
                System.out.println("Combo item " + i + ": " + art.getNombre() + " (ID: " + art.getId() + ")");
                
                if (art.getNombre().equalsIgnoreCase(nombreArticulo)) {
                    comboArticulo.setSelectedIndex(i);
                    encontrado = true;
                    System.out.println("✓ Artículo encontrado: " + art.getNombre() + " en posición " + i);
                    break;
                }
            }
            
            if (!encontrado) {
                System.out.println("✗ Artículo NO encontrado: " + nombreArticulo);
                comboArticulo.setSelectedIndex(-1); // Limpiar selección
            }
        } else {
            System.out.println("Nombre de artículo vacío o nulo");
            comboArticulo.setSelectedIndex(-1); // Limpiar si no hay nombre
        }

        // --- Seleccionar proveedor por nombre (columna 7)
        Object proveedorObj = modelo.getValueAt(fila, 7);
        if (proveedorObj != null && !proveedorObj.toString().trim().isEmpty()) {
            String nombreProveedor = proveedorObj.toString().trim();
            boolean encontrado = false;
            for (int i = 0; i < comboProveedor.getItemCount(); i++) {
                Proveedor prov = (Proveedor) comboProveedor.getItemAt(i);
                if (prov.getNombre().equals(nombreProveedor)) {
                    comboProveedor.setSelectedIndex(i);
                    encontrado = true;
                    System.out.println("Proveedor encontrado: " + nombreProveedor);
                    break;
                }
            }
            if (!encontrado) {
                System.out.println("Proveedor no encontrado en combo: " + nombreProveedor);
            }
        } else {
            System.out.println("Proveedor: NULL o vacío");
        }

        // --- Comprobante, método de pago, estado
        Object comprobanteObj = modelo.getValueAt(fila, 8);
        if (comprobanteObj != null && !comprobanteObj.toString().trim().isEmpty()) {
            comboComprobante.setSelectedItem(comprobanteObj.toString().trim());
        } else {
            comboComprobante.setSelectedIndex(0);
        }

        Object metodoPagoObj = modelo.getValueAt(fila, 9);
        if (metodoPagoObj != null && !metodoPagoObj.toString().trim().isEmpty()) {
            comboMetodo.setSelectedItem(metodoPagoObj.toString().trim());
        } else {
            comboMetodo.setSelectedIndex(0);
        }

        Object estadoObj = modelo.getValueAt(fila, 11);
        if (estadoObj != null && !estadoObj.toString().trim().isEmpty()) {
            comboEstado.setSelectedItem(estadoObj.toString().trim());
        } else {
            comboEstado.setSelectedIndex(0);
        }

        // --- Fecha (columna 10)
        Object fechaObj = modelo.getValueAt(fila, 10);
        if (fechaObj instanceof Date) {
            jDateChooserFecha.setDate((Date) fechaObj);
            System.out.println("Fecha cargada (Date): " + fechaObj);
        } else if (fechaObj instanceof String && !((String) fechaObj).trim().isEmpty()) {
            try {
                String fechaStr = ((String) fechaObj).trim();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                jDateChooserFecha.setDate(sdf.parse(fechaStr));
                System.out.println("Fecha cargada (String): " + fechaStr);
            } catch (ParseException e) {
                jDateChooserFecha.setDate(null);
                System.out.println("Error parseando fecha: " + fechaObj);
            }
        } else {
            jDateChooserFecha.setDate(null);
            System.out.println("Fecha: NULL o vacía");
        }

        System.out.println("=== Carga completada ===\n");
        System.out.println("RESUMEN FORMULARIO:");
        System.out.println("Unidad: '" + txtUnidadCompra.getText() + "'");
        System.out.println("Número: '" + txtNumero.getText() + "'");
        System.out.println("Cantidad: '" + txtCantidad.getText() + "'");
        System.out.println("Precio: '" + txtPrecio.getText() + "'");
        System.out.println("Total: '" + txtTotal.getText() + "'");

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar los datos seleccionados: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }


    }//GEN-LAST:event_tablaComprasMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnLimiparCamposComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimiparCamposComprasActionPerformed
        // TODO add your handling code here:
        limpiarFormularioCompras();
    }//GEN-LAST:event_btnLimiparCamposComprasActionPerformed

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
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new Compras().setVisible(true);
                Inventario inventario = new Inventario();
        inventario.setVisible(true);
        
        Compras compras = new Compras(inventario);
        compras.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnComprarCompra;
    private javax.swing.JButton btnEditarCompra;
    private javax.swing.JButton btnLimiparCamposCompras;
    private javax.swing.JComboBox<Modelo.Articulo> comboArticulo;
    private javax.swing.JComboBox<String> comboComprobante;
    private javax.swing.JComboBox<String> comboEstado;
    private javax.swing.JComboBox<String> comboMetodo;
    private javax.swing.JComboBox<Modelo.Proveedor> comboProveedor;
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JDateChooser jDateChooserFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tablaCompras;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtIdCompras;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtUnidadCompra;
    // End of variables declaration//GEN-END:variables
}


