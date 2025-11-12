
package Vista;

import Modelo.InventarioDAO;
import Modelo.InventarioItem;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Inventario extends javax.swing.JFrame {

    public Inventario() {
        initComponents();
        configurarTabla();
        refrescarInventario();
//        ajustarAlturaTabla();
    }
    
     // AGREGAR ESTA VARIABLE
    private Compras comprasPanel;
     // ✅ AGREGAR ESTE MÉTODO SETTER
    public void setComprasPanel(Compras comprasPanel) {
        this.comprasPanel = comprasPanel;
        System.out.println("✓ Compras panel conectado al Inventario");
    }
    
    private void configurarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) TablaInventario.getModel();

        // Listener que recalcula el total y el stock al modificar la tabla
        modelo.addTableModelListener(e -> recalcularTotales());
    }


    
    public void refrescarInventario() {
        try {
            InventarioDAO inventarioDAO = new InventarioDAO();
            List<InventarioItem> lista = inventarioDAO.obtenerTodos();

            DefaultTableModel modeloTabla = (DefaultTableModel) TablaInventario.getModel();
            modeloTabla.setRowCount(0); // Limpiar tabla

            for (InventarioItem item : lista) {
                modeloTabla.addRow(new Object[]{
                    item.getIdCompra(),
                    item.getNombre(),
                    item.getUnidad(),          // Unidad (gramos, litros, etc.)
                    item.getStockActual(),     // Sin stock mínimo
                    item.getCostoUnitario(),
                    item.getValorInventario(),
                    item.getEstado()
                });
            }

            // Calcular totales tras refrescar
            recalcularTotales();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al refrescar inventario: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void refrescarManual() {
    System.out.println("=== REFRESCO MANUAL DE INVENTARIO ===");
    refrescarInventario();
}
    
    private void recalcularTotales() {
        DefaultTableModel modelo = (DefaultTableModel) TablaInventario.getModel();
        double totalInventario = 0.0;
        int stockDisponible = 0;

        for (int i = 0; i < modelo.getRowCount(); i++) {
            // Columna 5 → Valor Inventario
            Object valorObj = modelo.getValueAt(i, 5);
            if (valorObj != null) {
                try {
                    totalInventario += Double.parseDouble(valorObj.toString());
                } catch (NumberFormatException ignored) {}
            }

            // Columna 3 → Stock Actual
            // Columna 6 → Estado
            Object estadoObj = modelo.getValueAt(i, 6);
            Object stockObj = modelo.getValueAt(i, 3);
            if (estadoObj != null && stockObj != null &&
                estadoObj.toString().equalsIgnoreCase("Recibido")) {
                try {
                    stockDisponible += Integer.parseInt(stockObj.toString());
                } catch (NumberFormatException ignored) {}
            }
        }

        // Mostrar valores formateados
        txtValorInventario.setText(String.format("Total del inventario: $%.2f", totalInventario));
        txtStockInventario.setText("Stock disponible: $" + stockDisponible);
    }

   // En la clase Inventario
    public void cargarDesdeCompras(DefaultTableModel modeloCompras) {
    System.out.println("=== ACTUALIZANDO INVENTARIO DESDE COMPRAS ===");
    System.out.println("Filas recibidas: " + modeloCompras.getRowCount());
    
    if (modeloCompras.getRowCount() == 0) {
        System.out.println("No hay datos de compras para procesar");
        return;
    }
    
    InventarioDAO inventarioDAO = new InventarioDAO();
    
    try {
        // ✅ SOLO UN PROCESO - eliminar el duplicado
        for (int i = 0; i < modeloCompras.getRowCount(); i++) {
            procesarFilaCompra(modeloCompras, i, inventarioDAO);
        }
        
        // ✅ SOLO UN REFRESCO
        refrescarInventario();
        
        System.out.println("✓ Inventario actualizado correctamente");
        
    } catch (Exception e) {
        System.err.println("Error en cargarDesdeCompras: " + e.getMessage());
        e.printStackTrace();
    }
}
   
    // NUEVO MÉTODO AUXILIAR PARA PROCESAR CADA FILA
    


    private void aplicarColoresEstado() {
    TablaInventario.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Object estado = table.getValueAt(row, 6); // columna Estado
            if (estado != null) {
                String estadoStr = estado.toString().toLowerCase();
                if (estadoStr.contains("recibido")) {
                    c.setBackground(java.awt.Color.GREEN);
                    c.setForeground(java.awt.Color.BLACK);
                } else if (estadoStr.contains("pendiente") || estadoStr.contains("parcial") || estadoStr.contains("cancelado")) {
                    c.setBackground(java.awt.Color.RED);
                    c.setForeground(java.awt.Color.WHITE);
                } else {
                    c.setBackground(java.awt.Color.WHITE);
                    c.setForeground(java.awt.Color.BLACK);
                }
            } else {
                c.setBackground(java.awt.Color.WHITE);
                c.setForeground(java.awt.Color.BLACK);
            }

            return c;
        }
    });
}

    private void verificarTablaVisualmente() {
    DefaultTableModel modelo = (DefaultTableModel) TablaInventario.getModel();
    System.out.println("=== VERIFICACIÓN VISUAL ===");
    System.out.println("Filas en modelo: " + modelo.getRowCount());
    System.out.println("Filas visibles en tabla: " + TablaInventario.getRowCount());
    System.out.println("Altura de tabla: " + TablaInventario.getHeight());
    System.out.println("Visibilidad: " + TablaInventario.isVisible());
    
    // Verificar cada fila visualmente
    for (int i = 0; i < modelo.getRowCount(); i++) {
        boolean filaVisible = TablaInventario.getCellRect(i, 0, false).height > 0;
        System.out.println("Fila " + i + " visible: " + filaVisible);
    }
}
    
   private void procesarFilaCompra(DefaultTableModel modeloCompras, int fila, InventarioDAO inventarioDAO) {
    try {
        Object nombreArticuloObj = modeloCompras.getValueAt(fila, 0);
        Object cantidadObj = modeloCompras.getValueAt(fila, 1);
        Object unidadObj = modeloCompras.getValueAt(fila, 2);
        Object precioObj = modeloCompras.getValueAt(fila, 3);
        Object estadoObj = modeloCompras.getValueAt(fila, 11);
        Object idCompraObj = modeloCompras.getValueAt(fila, 12);
        
        if (nombreArticuloObj == null || cantidadObj == null) {
            System.out.println("Fila " + fila + ": Datos incompletos, saltando...");
            return;
        }
        
        String nombreArticulo = nombreArticuloObj.toString().trim();
        int cantidad = Integer.parseInt(cantidadObj.toString());
        String unidad = unidadObj != null ? unidadObj.toString().trim() : "Unidad";
        double precio = precioObj != null ? Double.parseDouble(precioObj.toString()) : 0.0;
        String estado = estadoObj != null ? estadoObj.toString().trim() : "Pendiente";
        int idCompra = idCompraObj != null ? Integer.parseInt(idCompraObj.toString()) : 0;
        
        // ✅ DEBUG: Mostrar datos de entrada
        System.out.println("📦 PROCESANDO: " + nombreArticulo + 
                          " | Cantidad: " + cantidad + 
                          " | Precio: " + precio + 
                          " | Estado: " + estado +
                          " | ID Compra: " + idCompra);
        
        // ✅ SOLO procesar si el estado es "Recibido"
        if (!"Recibido".equalsIgnoreCase(estado)) {
            System.out.println("⏸️ Artículo " + nombreArticulo + " con estado " + estado + " - No se actualiza inventario");
            return;
        }
        
        // Buscar artículo existente
        InventarioItem existente = inventarioDAO.obtenerPorNombre(nombreArticulo);
        
        if (existente != null) {
            // ✅ DEBUG: Mostrar estado actual
            System.out.println("🔄 Artículo existente: " + existente.getNombre() + 
                              " | Stock actual: " + existente.getStockActual() +
                              " | Valor actual: " + existente.getValorInventario() +
                              " | ID Compra anterior: " + existente.getIdCompra());
            
            // ✅ VERIFICAR si ya fue procesado en esta compra
            if (existente.getIdCompra() == idCompra) {
                System.out.println("⚠️ Artículo " + nombreArticulo + " ya procesado en compra " + idCompra + " - No duplicar");
                return;
            }
            
            // Actualizar existente
            int nuevoStock = existente.getStockActual() + cantidad;
            double nuevoValor = nuevoStock * precio; // ✅ CORREGIDO: nuevoStock × precio
            
            // ✅ DEBUG: Mostrar cálculos
            System.out.println("🧮 CÁLCULO: " + existente.getStockActual() + " + " + cantidad + " = " + nuevoStock);
            System.out.println("💰 VALOR: " + nuevoStock + " × " + precio + " = " + nuevoValor);
            
            existente.setStockActual(nuevoStock);
            existente.setCostoUnitario(precio);
            existente.setValorInventario(nuevoValor);
            existente.setEstado(estado);
            existente.setUnidad(unidad);
            existente.setIdCompra(idCompra);
            
            inventarioDAO.actualizar(existente);
            System.out.println("✅ Actualizado: " + nombreArticulo + " - Stock: " + nuevoStock + " - Valor: " + nuevoValor);
        } else {
            // Crear nuevo
            double nuevoValor = cantidad * precio;
            
            // ✅ DEBUG: Mostrar nuevo artículo
            System.out.println("🆕 NUEVO ARTÍCULO: " + nombreArticulo + 
                              " | Stock: " + cantidad + 
                              " | Valor: " + nuevoValor);
            
            InventarioItem nuevo = new InventarioItem();
            nuevo.setIdCompra(idCompra);
            nuevo.setNombre(nombreArticulo);
            nuevo.setUnidad(unidad);
            nuevo.setStockActual(cantidad);
            nuevo.setCostoUnitario(precio);
            nuevo.setValorInventario(nuevoValor);
            nuevo.setEstado(estado);
            
            inventarioDAO.insertar(nuevo);
            System.out.println("➕ Nuevo: " + nombreArticulo + " - Stock: " + cantidad + " - Valor: " + nuevoValor);
        }
        
    } catch (Exception e) {
        System.err.println("❌ Error procesando fila " + fila + ": " + e.getMessage());
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        txtValorInventario = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaInventario = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        txtStockInventario = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setText("Exportar CVS");

        jButton2.setText("Solo Alertas");

        TablaInventario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Compra", "Nombre", "Unidad", "Stock Actual", "Costo Unitario", "Valor Inventario", "Estado"
            }
        ));
        TablaInventario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaInventarioMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TablaInventario);
        if (TablaInventario.getColumnModel().getColumnCount() > 0) {
            TablaInventario.getColumnModel().getColumn(0).setPreferredWidth(60);
            TablaInventario.getColumnModel().getColumn(1).setPreferredWidth(60);
            TablaInventario.getColumnModel().getColumn(2).setPreferredWidth(60);
            TablaInventario.getColumnModel().getColumn(4).setPreferredWidth(60);
            TablaInventario.getColumnModel().getColumn(6).setPreferredWidth(60);
        }

        jButton3.setText("Regresar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1013, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtValorInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58)
                                .addComponent(txtStockInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jButton3)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorInventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStockInventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1050, 550));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void TablaInventarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaInventarioMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_TablaInventarioMouseClicked

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
            java.util.logging.Logger.getLogger(Inventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Inventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Inventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Inventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Inventario().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TablaInventario;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtStockInventario;
    private javax.swing.JTextField txtValorInventario;
    // End of variables declaration//GEN-END:variables
}

