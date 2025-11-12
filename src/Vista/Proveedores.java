
package Vista;

import Modelo.Proveedor;
import Modelo.ProveedorDao;
import Reportes.Excel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Proveedores extends javax.swing.JFrame {

    /**
     * Creates new form Proveedores
     */
    public Proveedores() {
        initComponents();
        configurarTabla();
        cargarProveedoresEnTabla();
        txtIdProveedor.setVisible(false);

    }
    

    private void configurarTabla() {
    DefaultTableModel modelo = new DefaultTableModel(
        new Object[][]{},
        new String[]{
            "ID", "No. de Documento", "Tipo", "Nombre - Razon Social", "Telefono", "Dirección", "Razon"
        }
    );
    tablaProveedores.setModel(modelo);

    // Ocultar columna ID
    tablaProveedores.getColumnModel().getColumn(0).setMinWidth(0);
    tablaProveedores.getColumnModel().getColumn(0).setMaxWidth(0);
    tablaProveedores.getColumnModel().getColumn(0).setWidth(0);
}

    
    private void guardarProveedor() {
        if (!validarCampos()) {
        return; // si hay algún campo vacío, no guarda
    }
    String nombre = txtNombreProveedor.getText().trim();
    String tipo = comboTipoDocumento.getSelectedItem().toString();
    String numero = txtNumeroDocumento.getText().trim();
    String telefono = txtTelefono.getText().trim();
    String direccion = txtDireccionProveedor.getText().trim();
    String razon = txtRazonProveedor.getText().trim();


    if (nombre.isEmpty() || numero.isEmpty() || telefono.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Completa todos los campos.");
        return;
    }

    Proveedor p = new Proveedor(0, nombre, tipo, numero, telefono, direccion, razon);
    ProveedorDao dao = new ProveedorDao();
    if (dao.insertar(p)) {
        cargarProveedoresEnTabla();
        limpiarFormularioProveedor();
        JOptionPane.showMessageDialog(this, "Proveedor guardado correctamente.");
    } else {
        JOptionPane.showMessageDialog(this, "Error al guardar proveedor.");
    }
}

    private void actualizarProveedor() {
    if (txtIdProveedor.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Selecciona un proveedor para actualizar.");
        return;
    }

    if (!validarCampos()) {
        return; // si hay algún campo vacío, no actualiza
    }
    
    int id = Integer.parseInt(txtIdProveedor.getText());
    String nombre = txtNombreProveedor.getText().trim();
    String tipo = comboTipoDocumento.getSelectedItem().toString();
    String numero = txtNumeroDocumento.getText().trim();
    String telefono = txtTelefono.getText().trim();
    String direccion = txtDireccionProveedor.getText().trim();
    String razon = txtRazonProveedor.getText().trim();

    Proveedor p = new Proveedor(id, nombre, tipo, numero, telefono, direccion, razon);
    ProveedorDao dao = new ProveedorDao();
    if (dao.actualizar(p)) {
        cargarProveedoresEnTabla();
        limpiarFormularioProveedor();
        JOptionPane.showMessageDialog(this, "Proveedor actualizado correctamente.");
    } else {
        JOptionPane.showMessageDialog(this, "Error al actualizar proveedor.");
    }
}


    private void eliminarProveedor() {
    if (txtIdProveedor.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Selecciona un proveedor para eliminar.");
        return;
    }

    int id = Integer.parseInt(txtIdProveedor.getText());
    int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        ProveedorDao dao = new ProveedorDao();
        if (dao.eliminar(id)) {
            cargarProveedoresEnTabla();
            limpiarFormularioProveedor();
            JOptionPane.showMessageDialog(this, "Proveedor eliminado correctamente.");
        } else {
            JOptionPane.showMessageDialog(this, "Error al eliminar proveedor.");
        }
    }
}
    
    private void limpiarFormularioProveedor() {
    txtIdProveedor.setText("");
    txtNombreProveedor.setText("");
    comboTipoDocumento.setSelectedIndex(-1);
    txtNumeroDocumento.setText("");
    txtTelefono.setText("");
    txtRazonProveedor.setText("");
    txtDireccionProveedor.setText("");
    btnActualizarProveedor.setEnabled(false);
    btnEliminarProveedor.setEnabled(false);
    tablaProveedores.clearSelection();
}

    private void cargarProveedoresEnTabla() {
    DefaultTableModel modelo = (DefaultTableModel) tablaProveedores.getModel();
    modelo.setRowCount(0); // limpia la tabla

    ProveedorDao dao = new ProveedorDao();
    for (Proveedor p : dao.listar()) {
        modelo.addRow(new Object[]{
            p.getId(),
            p.getNumeroDocumento(),
            p.getTipo(),
            p.getNombre(),
            p.getTelefono(),
            p.getDireccion(),
            p.getRazon()
        });
    }
}

    private boolean validarCampos() {
    if (txtNombreProveedor.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese el nombre o razón social del proveedor.");
        txtNombreProveedor.requestFocus();
        return false;
    }

    if (comboTipoDocumento.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un tipo de documento.");
        comboTipoDocumento.requestFocus();
        return false;
    }

    if (txtNumeroDocumento.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese el número de documento.");
        txtNumeroDocumento.requestFocus();
        return false;
    }

    if (txtTelefono.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese el teléfono del proveedor.");
        txtTelefono.requestFocus();
        return false;
    }

    if (txtDireccionProveedor.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese la dirección del proveedor.");
        txtDireccionProveedor.requestFocus();
        return false;
    }

    if (txtRazonProveedor.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese la razón del proveedor.");
        txtRazonProveedor.requestFocus();
        return false;
    }

    return true; // todos los campos están correctos
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
        txtNombreProveedor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        comboTipoDocumento = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtNumeroDocumento = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaProveedores = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        btnGuardarProveedor = new javax.swing.JButton();
        btnActualizarProveedor = new javax.swing.JButton();
        btnEliminarProveedor = new javax.swing.JButton();
        btnLimipiarFormularioProveedor = new javax.swing.JButton();
        btnExcelProveedor = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtDireccionProveedor = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRazonProveedor = new javax.swing.JTextField();
        txtIdProveedor = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Ingrese información del nuevo cliente");

        jLabel2.setText("Nombre/Razon Social:");

        jLabel3.setText("Tipo de documento:");

        comboTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RFC", "OTROS", " " }));

        jLabel4.setText("Numero de documento:");

        jLabel5.setText("Telefono:");

        tablaProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre - Razon Social", "Tipo", "No. de Documento", "Telefono", "Dirección", "Razon"
            }
        ));
        tablaProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProveedoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaProveedores);
        if (tablaProveedores.getColumnModel().getColumnCount() > 0) {
            tablaProveedores.getColumnModel().getColumn(1).setPreferredWidth(60);
            tablaProveedores.getColumnModel().getColumn(2).setPreferredWidth(60);
            tablaProveedores.getColumnModel().getColumn(3).setPreferredWidth(80);
            tablaProveedores.getColumnModel().getColumn(4).setPreferredWidth(60);
            tablaProveedores.getColumnModel().getColumn(5).setPreferredWidth(50);
        }

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel6.setText("Proveedores");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/Regresar_1.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnGuardarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/GuardarTodo.png"))); // NOI18N
        btnGuardarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProveedorActionPerformed(evt);
            }
        });

        btnActualizarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/Actualizar.jpg"))); // NOI18N
        btnActualizarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarProveedorActionPerformed(evt);
            }
        });

        btnEliminarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/eliminar.png"))); // NOI18N
        btnEliminarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProveedorActionPerformed(evt);
            }
        });

        btnLimipiarFormularioProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/LimpiarDatos.png"))); // NOI18N
        btnLimipiarFormularioProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimipiarFormularioProveedorActionPerformed(evt);
            }
        });

        btnExcelProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/excel.png"))); // NOI18N
        btnExcelProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelProveedorActionPerformed(evt);
            }
        });

        jLabel7.setText("Dirección:");

        jLabel8.setText("Razon:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(230, 230, 230)
                        .addComponent(jLabel6)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtNombreProveedor, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboTipoDocumento, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtNumeroDocumento, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
                                .addComponent(jLabel7))
                            .addComponent(txtDireccionProveedor)
                            .addComponent(jLabel5)
                            .addComponent(txtTelefono)
                            .addComponent(jLabel8)
                            .addComponent(txtRazonProveedor))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnGuardarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnActualizarProveedor)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminarProveedor)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimipiarFormularioProveedor)
                                .addGap(18, 18, 18)
                                .addComponent(btnExcelProveedor)
                                .addGap(293, 293, 293))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jLabel2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNumeroDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDireccionProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRazonProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnLimipiarFormularioProveedor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(btnGuardarProveedor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnActualizarProveedor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEliminarProveedor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExcelProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)))
                .addContainerGap(121, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnEliminarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProveedorActionPerformed
        // TODO add your handling code here:
        eliminarProveedor();
    }//GEN-LAST:event_btnEliminarProveedorActionPerformed

    private void btnGuardarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProveedorActionPerformed
        // TODO add your handling code here:
        guardarProveedor();
        cargarProveedoresEnTabla();
    }//GEN-LAST:event_btnGuardarProveedorActionPerformed

    private void btnActualizarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarProveedorActionPerformed
        // TODO add your handling code here:
        actualizarProveedor();
    }//GEN-LAST:event_btnActualizarProveedorActionPerformed

    private void btnLimipiarFormularioProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimipiarFormularioProveedorActionPerformed
        // TODO add your handling code here:
        limpiarFormularioProveedor();
    }//GEN-LAST:event_btnLimipiarFormularioProveedorActionPerformed

    private void tablaProveedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProveedoresMouseClicked
        // TODO add your handling code here:
                                                   
    int fila = tablaProveedores.getSelectedRow();
    if (fila != -1) {
        txtIdProveedor.setText(tablaProveedores.getValueAt(fila, 0).toString());
        txtNumeroDocumento.setText(tablaProveedores.getValueAt(fila, 1).toString());
        comboTipoDocumento.setSelectedItem(tablaProveedores.getValueAt(fila, 2).toString());
        txtNombreProveedor.setText(tablaProveedores.getValueAt(fila, 3).toString());
        txtTelefono.setText(tablaProveedores.getValueAt(fila, 4).toString());
        txtDireccionProveedor.setText(tablaProveedores.getValueAt(fila, 5).toString());
        txtRazonProveedor.setText(tablaProveedores.getValueAt(fila, 6).toString());

        btnActualizarProveedor.setEnabled(true);
        btnEliminarProveedor.setEnabled(true);
    }

    }//GEN-LAST:event_tablaProveedoresMouseClicked

    private void btnExcelProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelProveedorActionPerformed
        // TODO add your handling code here:
        Excel.reporte();
    }//GEN-LAST:event_btnExcelProveedorActionPerformed

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
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Proveedores().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarProveedor;
    private javax.swing.JButton btnEliminarProveedor;
    private javax.swing.JButton btnExcelProveedor;
    private javax.swing.JButton btnGuardarProveedor;
    private javax.swing.JButton btnLimipiarFormularioProveedor;
    private javax.swing.JComboBox<String> comboTipoDocumento;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaProveedores;
    private javax.swing.JTextField txtDireccionProveedor;
    private javax.swing.JTextField txtIdProveedor;
    private javax.swing.JTextField txtNombreProveedor;
    private javax.swing.JTextField txtNumeroDocumento;
    private javax.swing.JTextField txtRazonProveedor;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
