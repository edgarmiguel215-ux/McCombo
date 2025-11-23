
package Vista;

import Modelo.Articulo;
import Modelo.ArticuloDAO;
import Modelo.Compra;
import Modelo.CompraDAO;
import Modelo.InventarioDAO;
import Modelo.Proveedor;
import Modelo.ProveedorDao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Compras extends javax.swing.JFrame {

   
    // Referencia al inventario para actualizarlo en tiempo real
    private Inventarios inventarioVentana;
    
    public void setInventarioVentana(Inventarios inventario) {
    this.inventarioVentana = inventario;
}

    // DAO y modelo de compra
    private CompraDAO dao = new CompraDAO();
    private Compra compra = new Compra();

    // Mapas para ComboBox
    private Map<Integer, Articulo> mapaArticulos = new HashMap<>();
    private Map<Integer, Proveedor> mapaProveedores = new HashMap<>();

    // Constructor principal recibe la ventana de Inventarios
    public Compras(Inventarios inventario) {
        initComponents();
        this.inventarioVentana = inventario;
        this.setLocationRelativeTo(null);

        listeners();
        listarCompras();
        cargarArticulos();
        cargarProveedores();
        compra = new Compra();
    }

    
    // -------------------------- LISTADO --------------------------
    public void listarCompras() {
    List<Compra> lista = dao.listarCompras();
    DefaultTableModel modelo = (DefaultTableModel) TablaCompras.getModel();
    modelo.setRowCount(0);

    for (Compra c : lista) {
        String unidad = c.getUnidad(); //  unidad directamente de la compra
        if (unidad == null || unidad.trim().isEmpty()) {
            unidad = ""; // evitar mostrar null
        }

        modelo.addRow(new Object[]{
            c.getId(),
            c.getNombreArticulo(),
            c.getCantidad(),
            unidad, //  mostrar la unidad correcta
            c.getPrecioUnitario(),
            c.getTotal(),
            c.getDatosPago(),
            c.getProveedor(),
            c.getComprobante(),
            c.getMetodoPago(),
            c.getFecha(),
            c.getEstado()
        });
    }
}


    // -------------------------- CALCULO TOTAL --------------------------
    private void listeners() {
        txtCantidadCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularTotalCompra();
            }
        });
        txtPrecioUnitarioCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularTotalCompra();
            }
        });
    }

    private void calcularTotalCompra() {
        try {
            int cantidad = Integer.parseInt(txtCantidadCompra.getText().trim());
            double precio = Double.parseDouble(txtPrecioUnitarioCompra.getText().trim());
            double total = cantidad * precio;
            txtTotalCompra.setText(String.format("%.2f", total));
        } catch (NumberFormatException e) {
            txtTotalCompra.setText("0.00");
        }
    }

    // -------------------------- CARGAR COMBOS --------------------------
    private void cargarArticulos() {
        ArticuloDAO daoArt = new ArticuloDAO();
        List<Articulo> lista = daoArt.listar();
        ComboArticulo.removeAllItems();
        mapaArticulos.clear();
        int index = 0;
        for (Articulo art : lista) {
            ComboArticulo.addItem(art.getNombre());
            mapaArticulos.put(index, art);
            index++;
        }
    }

    private void cargarProveedores() {
        ProveedorDao daoProv = new ProveedorDao();
        List<Proveedor> lista = daoProv.listarProveedores();
        comboProveedorCompras.removeAllItems();
        mapaProveedores.clear();
        int index = 0;
        for (Proveedor p : lista) {
            comboProveedorCompras.addItem(p.getNombre());
            mapaProveedores.put(index, p);
            index++;
        }
    }

    public void recargarArticulos() {
        cargarArticulos();
    }

    public void recargarProveedores() {
        cargarProveedores();
    }

    // -------------------------- REGISTRAR COMPRA --------------------------
    private void registrarCompra() {
    if (!validarFormulario()) return;

    int indexArticulo = ComboArticulo.getSelectedIndex();
    int indexProveedor = comboProveedorCompras.getSelectedIndex();

    if (indexArticulo < 0 || indexProveedor < 0) {
        JOptionPane.showMessageDialog(this, "Seleccione artículo y proveedor");
        return;
    }

    Articulo art = mapaArticulos.get(indexArticulo);
    Proveedor prov = mapaProveedores.get(indexProveedor);

    Compra nuevaCompra = new Compra();

    // Datos del artículo
    nuevaCompra.setIdArticulo(art.getIdArticulo());
    nuevaCompra.setArticulo(art.getNombre());         // nombre para la compra
    nuevaCompra.setNombreArticulo(art.getNombre());   // nombre histórico para inventario

    // Unidad y cantidad
    nuevaCompra.setUnidad(txtUnidadCompra.getText().trim());
    nuevaCompra.setCantidad(Integer.parseInt(txtCantidadCompra.getText().trim()));

    // Precio y total
    nuevaCompra.setPrecioUnitario(Double.parseDouble(txtPrecioUnitarioCompra.getText().trim()));
    nuevaCompra.setTotal(nuevaCompra.getCantidad() * nuevaCompra.getPrecioUnitario());

    // Datos de pago y comprobante
    nuevaCompra.setComprobante(comboComprobanteCompra.getSelectedItem().toString());
    nuevaCompra.setMetodoPago(comboMetodPagoCompra.getSelectedItem().toString());
    nuevaCompra.setDatosPago(txtDatosPagoCompra.getText().trim());

    // Fecha y estado
    nuevaCompra.setFecha(jDateFechaCompra.getDate());
    nuevaCompra.setEstado(comboEstatusCompra.getSelectedItem().toString());

    // Datos del proveedor
    nuevaCompra.setIdProveedor(prov.getId());
    nuevaCompra.setProveedor(prov.getNombre());

    // Registrar compra en BD y obtener ID
    int id = dao.registrarCompra(nuevaCompra);
    nuevaCompra.setId(id);

    JOptionPane.showMessageDialog(this, "Compra registrada con ID: " + id);

    // Refrescar tabla de compras
    listarCompras();

    // Actualizar inventario correctamente
    InventarioDAO daoInventario = new InventarioDAO();
    daoInventario.registrarInventarioPorCompra(nuevaCompra);

    // Refrescar ventana de inventario si está abierta
    if (Inventarios.instancia != null) {
        Inventarios.instancia.refrescarInventario();
    }

    // Limpiar formulario
    limpiarFormulario();
}


    private boolean validarFormulario() {
        if (txtCantidadCompra.getText().trim().isEmpty() ||
            txtPrecioUnitarioCompra.getText().trim().isEmpty() ||
            ComboArticulo.getSelectedIndex() == -1 ||
            comboProveedorCompras.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return false;
        }
        return true;
    }


    private void actualizarCompra() {
    int fila = TablaCompras.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, "Selecciona una compra para actualizar.");
        return;
    }

    try {
        // 1️⃣ Recupera la compra original desde la BD
        int idCompra = Integer.parseInt(TablaCompras.getValueAt(fila, 0).toString());
        Compra original = dao.obtenerCompraDesdeBD(idCompra);

        // 2️⃣ Crea una copia de la compra con los datos del formulario
        Compra editada = new Compra();
        editada.setId(original.getId()); // importante para UPDATE
        editada.setCantidad(Integer.parseInt(txtCantidadCompra.getText().trim()));
        editada.setUnidad(txtUnidadCompra.getText().trim());
        editada.setPrecioUnitario(Double.parseDouble(txtPrecioUnitarioCompra.getText().trim()));
        editada.setTotal(editada.getCantidad() * editada.getPrecioUnitario());
        editada.setDatosPago(txtDatosPagoCompra.getText().trim());
        editada.setComprobante(comboComprobanteCompra.getSelectedItem().toString());
        editada.setMetodoPago(comboMetodPagoCompra.getSelectedItem().toString());
        editada.setEstado(comboEstatusCompra.getSelectedItem().toString());
        editada.setFecha(jDateFechaCompra.getDate());

        // Artículo y proveedor
        Articulo art = mapaArticulos.get(ComboArticulo.getSelectedIndex());
        Proveedor prov = mapaProveedores.get(comboProveedorCompras.getSelectedIndex());

        editada.setIdArticulo(art.getIdArticulo());
        editada.setArticulo(art.getNombre());        // nombre para la compra
        editada.setNombreArticulo(art.getNombre());  // nombre histórico para inventario
        editada.setIdProveedor(prov.getId());
        editada.setProveedor(prov.getNombre());

        // 3️⃣ Actualiza la compra en la BD
        if (dao.editarCompra(editada)) {

            // 4️⃣ Ajusta inventario según cambios de cantidad y estado
            InventarioDAO inventarioDAO = new InventarioDAO();
            inventarioDAO.actualizarInventarioDespuesDeEditar(original, editada);

            // 5️⃣ Refresca tablas
            listarCompras();
            if (inventarioVentana != null) {
                inventarioVentana.refrescarInventario();
            }

            JOptionPane.showMessageDialog(this, "Compra actualizada correctamente.");
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar la compra.");
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al actualizar la compra: " + e.getMessage());
        e.printStackTrace();
    }
}

    //------------ELIMINAR COMPRA-------------//
    private void eliminarCompra() {
    int fila = TablaCompras.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, "Selecciona una compra para eliminar.");
        return;
    }

    try {
        int idCompra = Integer.parseInt(TablaCompras.getValueAt(fila, 0).toString());

        int opcion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de cancelar la compra seleccionada?", 
            "Confirmar cancelación", 
            JOptionPane.YES_NO_OPTION);

        if (opcion != JOptionPane.YES_OPTION) return;

        // Obtener compra completa desde BD
        Compra compra = dao.obtenerCompraDesdeBD(idCompra);

        // Soft delete: marcar la compra como "Cancelado"
        if (dao.eliminarCompra(idCompra)) { // ahora hace UPDATE SET estado='Cancelado'
            
            // Ajustar inventario
            InventarioDAO inventarioDAO = new InventarioDAO();
            inventarioDAO.actualizarInventarioDespuesDeEliminar(compra);

            // Refrescar tabla de compras
            listarCompras(); // tu método que recarga la tabla
            if (inventarioVentana != null) {
                inventarioVentana.refrescarInventario();
            }

            JOptionPane.showMessageDialog(this, "Compra cancelada correctamente.");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo cancelar la compra.");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al cancelar la compra: " + e.getMessage());
        e.printStackTrace();
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ComboArticulo = new javax.swing.JComboBox<>();
        jDateFechaCompra = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtCantidadCompra = new javax.swing.JTextField();
        comboEstatusCompra = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtUnidadCompra = new javax.swing.JTextField();
        comboProveedorCompras = new javax.swing.JComboBox<>();
        txtDatosPagoCompra = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtPrecioUnitarioCompra = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtTotalCompra = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaCompras = new javax.swing.JTable();
        btnComprarCompra = new javax.swing.JButton();
        btnLimpiarFormularioCompras = new javax.swing.JButton();
        btnEliminarCompra = new javax.swing.JButton();
        btnActualizarCompra = new javax.swing.JButton();
        comboComprobanteCompra = new javax.swing.JComboBox<>();
        comboMetodPagoCompra = new javax.swing.JComboBox<>();
        btnRegresar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Articulo:");

        jLabel2.setText("Comprobante:");

        jLabel3.setText("Fecha de Compra:");

        jLabel4.setText("Cantidad:");

        jLabel5.setText("Metodo de Pago:");

        jLabel6.setText("Estatus de la compra:");

        comboEstatusCompra.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Recibido", "Parcial", "Cancelado" }));

        jLabel7.setText("Unidad");

        jLabel8.setText("Datos para el Pago:");

        jLabel9.setText("Proveedor:");

        jLabel10.setText("Precio Unitario:");

        jLabel11.setText("Total:");

        txtTotalCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalCompraActionPerformed(evt);
            }
        });

        TablaCompras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Artículo", "Cantidad", "Unidad", "Precio Unitario", "Total", "Datos del Pago", "Proveedor", "Comprobante", "Metodo Pago", "Fecha", "Estatus"
            }
        ));
        TablaCompras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaComprasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TablaCompras);
        if (TablaCompras.getColumnModel().getColumnCount() > 0) {
            TablaCompras.getColumnModel().getColumn(1).setPreferredWidth(60);
            TablaCompras.getColumnModel().getColumn(2).setPreferredWidth(60);
            TablaCompras.getColumnModel().getColumn(6).setPreferredWidth(60);
            TablaCompras.getColumnModel().getColumn(8).setPreferredWidth(60);
            TablaCompras.getColumnModel().getColumn(9).setPreferredWidth(60);
            TablaCompras.getColumnModel().getColumn(10).setPreferredWidth(60);
            TablaCompras.getColumnModel().getColumn(11).setPreferredWidth(60);
        }

        btnComprarCompra.setText("Comprar");
        btnComprarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComprarCompraActionPerformed(evt);
            }
        });

        btnLimpiarFormularioCompras.setText("Limpiar");
        btnLimpiarFormularioCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarFormularioComprasActionPerformed(evt);
            }
        });

        btnEliminarCompra.setText("Eliminar");
        btnEliminarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarCompraActionPerformed(evt);
            }
        });

        btnActualizarCompra.setText("Actualizar");
        btnActualizarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarCompraActionPerformed(evt);
            }
        });

        comboComprobanteCompra.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Recibo", "Factura", "Cheque" }));

        comboMetodPagoCompra.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Tarjeta", "Transferencia", "Cheque" }));

        btnRegresar.setText("Regresar");
        btnRegresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegresarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(comboComprobanteCompra, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ComboArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jDateFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(comboMetodPagoCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtDatosPagoCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(18, 18, 18)
                                        .addComponent(comboEstatusCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel9)
                                        .addGap(18, 18, 18)
                                        .addComponent(comboProveedorCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnActualizarCompra)
                                    .addComponent(btnComprarCompra))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnEliminarCompra)
                                    .addComponent(btnLimpiarFormularioCompras)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCantidadCompra, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtUnidadCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtPrecioUnitarioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtTotalCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(59, 59, 59))))
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnRegresar)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(btnRegresar)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ComboArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtCantidadCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtUnidadCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtPrecioUnitarioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtTotalCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8)
                            .addComponent(txtDatosPagoCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboComprobanteCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboMetodPagoCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jDateFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(comboEstatusCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9)
                                .addComponent(comboProveedorCompras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnActualizarCompra)
                            .addComponent(btnEliminarCompra))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnComprarCompra)
                            .addComponent(btnLimpiarFormularioCompras))))
                .addGap(45, 45, 45)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1130, 530));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTotalCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalCompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalCompraActionPerformed

    private void btnComprarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComprarCompraActionPerformed
        // TODO add your handling code here:
    registrarCompra();


    }//GEN-LAST:event_btnComprarCompraActionPerformed

    private void btnLimpiarFormularioComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarFormularioComprasActionPerformed
        // TODO add your handling code here:
        ComboArticulo.setSelectedIndex(-1);
        txtCantidadCompra.setText("");
        txtUnidadCompra.setText("");
        txtPrecioUnitarioCompra.setText("");
        txtTotalCompra.setText("0.00");
        comboComprobanteCompra.setSelectedIndex(-1);
        comboMetodPagoCompra.setSelectedIndex(-1);
        txtDatosPagoCompra.setText("");
        jDateFechaCompra.setDate(null);
        comboEstatusCompra.setSelectedIndex(-1);
        comboProveedorCompras.setSelectedIndex(-1);
    }//GEN-LAST:event_btnLimpiarFormularioComprasActionPerformed

    private void btnEliminarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCompraActionPerformed
        // TODO add your handling code here:
    eliminarCompra();

    }//GEN-LAST:event_btnEliminarCompraActionPerformed

    private void btnActualizarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarCompraActionPerformed
        // TODO add your handling code here:
    actualizarCompra();
    }//GEN-LAST:event_btnActualizarCompraActionPerformed

    private void btnRegresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegresarActionPerformed
        // TODO add your handling code here:
        SistemaPrincipal sis = new SistemaPrincipal();
        sis.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnRegresarActionPerformed

    private void TablaComprasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaComprasMouseClicked
        // TODO add your handling code here:
                                             
                                            
    int fila = TablaCompras.getSelectedRow();
    if (fila < 0) {
        return; // nada seleccionado
    }

    // Validar que la celda no sea null antes de usar toString()
    Object valorId = TablaCompras.getValueAt(fila, 0);
    if (valorId == null) {
        JOptionPane.showMessageDialog(this, "La fila seleccionada no contiene un ID válido.");
        return;
    }

    try {
        int idCompra = Integer.parseInt(valorId.toString());
        Compra compraSeleccionada = dao.obtenerCompraDesdeBD(idCompra);

        // Cargar datos en el formulario
        txtCantidadCompra.setText(String.valueOf(compraSeleccionada.getCantidad()));
        txtUnidadCompra.setText(compraSeleccionada.getUnidad());
        txtPrecioUnitarioCompra.setText(String.valueOf(compraSeleccionada.getPrecioUnitario()));
        txtTotalCompra.setText(String.valueOf(compraSeleccionada.getTotal()));
        txtDatosPagoCompra.setText(compraSeleccionada.getDatosPago());
        jDateFechaCompra.setDate(compraSeleccionada.getFecha());

        // Combos
        ComboArticulo.setSelectedItem(compraSeleccionada.getNombreArticulo());
        comboProveedorCompras.setSelectedItem(compraSeleccionada.getProveedor());
        comboComprobanteCompra.setSelectedItem(compraSeleccionada.getComprobante());
        comboMetodPagoCompra.setSelectedItem(compraSeleccionada.getMetodoPago());
        comboEstatusCompra.setSelectedItem(compraSeleccionada.getEstado());

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al cargar datos de la compra: " + e.getMessage());
        e.printStackTrace();
    }

    }//GEN-LAST:event_TablaComprasMouseClicked

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new Compras().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboArticulo;
    private javax.swing.JTable TablaCompras;
    private javax.swing.JButton btnActualizarCompra;
    private javax.swing.JButton btnComprarCompra;
    private javax.swing.JButton btnEliminarCompra;
    private javax.swing.JButton btnLimpiarFormularioCompras;
    private javax.swing.JButton btnRegresar;
    private javax.swing.JComboBox<String> comboComprobanteCompra;
    private javax.swing.JComboBox<String> comboEstatusCompra;
    private javax.swing.JComboBox<String> comboMetodPagoCompra;
    private javax.swing.JComboBox<String> comboProveedorCompras;
    private com.toedter.calendar.JDateChooser jDateFechaCompra;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCantidadCompra;
    private javax.swing.JTextField txtDatosPagoCompra;
    private javax.swing.JTextField txtPrecioUnitarioCompra;
    private javax.swing.JTextField txtTotalCompra;
    private javax.swing.JTextField txtUnidadCompra;
    // End of variables declaration//GEN-END:variables
  
    private void limpiarFormulario() {
    txtCantidadCompra.setText("");
    txtUnidadCompra.setText("");
    txtPrecioUnitarioCompra.setText("");
    txtTotalCompra.setText("0.00");
    txtDatosPagoCompra.setText("");
    ComboArticulo.setSelectedIndex(-1);
    comboProveedorCompras.setSelectedIndex(-1);
    comboComprobanteCompra.setSelectedIndex(0);
    comboMetodPagoCompra.setSelectedIndex(0);
    comboEstatusCompra.setSelectedIndex(0);
    jDateFechaCompra.setDate(null);
    compra = null; // Reinicia la variable global
}

}
