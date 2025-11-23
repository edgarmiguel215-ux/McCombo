
package Vista;

import Modelo.UsuariosDao;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author salga
 */
public class Usuarios extends javax.swing.JDialog {

   
    public Usuarios(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        listarUsuarios();
        this.setLocationRelativeTo(null); // Centrar la ventana
        
    }
    private final String CODIGO_VALIDO = "MCD2025";
    private final UsuariosDao dao = new UsuariosDao();
    private int idUsuarioSeleccionado = -1;

    
    
    private void listarUsuarios() {
    DefaultTableModel modelo = (DefaultTableModel) TablaUsuario.getModel();
    modelo.setRowCount(0); // limpia la tabla

    for (Modelo.Usuarios u : dao.listarUsuarios()) {
        modelo.addRow(new Object[]{
            u.getId(), u.getCorreo(), u.getPass(), u.getNombre(), u.getRol()
        });
    }
}

    private void registrarUsuario() {
    String correo = txtCorreoUsuario.getText();
    String pass = txtContraseñaUsuario.getText();
    String nombre = txtNombreUsuario.getText();
    String rol = ComboBoxRolUsuario.getSelectedItem().toString();
    String codigo = txtCodigoRegistro.getText();

    if (correo.isEmpty() || pass.isEmpty() || nombre.isEmpty() || codigo.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
        return;
    }

    if (!codigo.equals(CODIGO_VALIDO)) {
        JOptionPane.showMessageDialog(null, "Código de registro inválido");
        return;
    }

    if (dao.existeUsuarioConNombreYPass(nombre, pass)) {
    JOptionPane.showMessageDialog(null, "Ya existe un usuario con ese nombre y contraseña");
    return;
}

    
    Modelo.Usuarios nuevo = new Modelo.Usuarios(nombre, correo, pass, rol);
    if (dao.registrarUsuario(nuevo)) {
        JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente");
        limpiarCampos();
        listarUsuarios();
    } else {
        JOptionPane.showMessageDialog(null, "Error al registrar usuario");
    }
}


   private void editarUsuario() {
    if (idUsuarioSeleccionado == -1) {
        JOptionPane.showMessageDialog(null, "Selecciona un usuario de la tabla");
        return;
    }

    String correo = txtCorreoUsuario.getText().trim();
    String pass = txtContraseñaUsuario.getText().trim();
    String nombre = txtNombreUsuario.getText().trim();
    String rol = ComboBoxRolUsuario.getSelectedItem().toString();
    String codigo = txtCodigoRegistro.getText().trim();

    if (correo.isEmpty() || pass.isEmpty() || nombre.isEmpty() || codigo.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
        return;
    }

    if (!codigo.equals(CODIGO_VALIDO)) {
        JOptionPane.showMessageDialog(null, "Código de edición inválido");
        return;
    }

    // Validación mejorada: excluye al usuario actual
    if (dao.existeUsuarioConNombreYCorreoExcluyendoId(nombre, correo, idUsuarioSeleccionado)) {
        JOptionPane.showMessageDialog(null, "Ya existe otro usuario con ese nombre y correo");
        return;
    }

    Modelo.Usuarios actualizado = new Modelo.Usuarios(idUsuarioSeleccionado, nombre, correo, pass, rol);
    if (dao.actualizarUsuario(actualizado)) {
        JOptionPane.showMessageDialog(null, "Usuario actualizado. Debes iniciar sesión nuevamente.");
        // Cerrar todas las ventanas abiertas
        java.awt.Window[] ventanas = java.awt.Window.getWindows();
        for (java.awt.Window w : ventanas) {
            w.dispose();
        }

        // Volver a la ventana de login
        new Login().setVisible(true);
    } else {
        JOptionPane.showMessageDialog(null, "Error al actualizar usuario");
    }
}




    private void eliminarUsuario() {
    if (idUsuarioSeleccionado == -1) {
        JOptionPane.showMessageDialog(null, "Selecciona un usuario de la tabla");
        return;
    }

    String codigo = txtCodigoRegistro.getText(); // reutilizamos este campo

    if (codigo.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Debes ingresar el código de autorización para eliminar");
        return;
    }

    if (!codigo.equals("DELETE USER101")) {
        JOptionPane.showMessageDialog(null, "Código de eliminación inválido");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(null, "¿Eliminar usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        if (dao.eliminarUsuario(idUsuarioSeleccionado)) {
            JOptionPane.showMessageDialog(null, "Usuario eliminado correctamente");
            limpiarCampos();
            listarUsuarios();
            idUsuarioSeleccionado = -1;
        } else {
            JOptionPane.showMessageDialog(null, "Error al eliminar usuario");
        }
    }
}


    private void limpiarCampos() {
    txtCorreoUsuario.setText("");
    txtContraseñaUsuario.setText("");
    txtNombreUsuario.setText("");
    ComboBoxRolUsuario.setSelectedIndex(0);
    txtCodigoRegistro.setText(""); // limpia el código
    idUsuarioSeleccionado = -1;
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
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCorreoUsuario = new javax.swing.JTextField();
        txtContraseñaUsuario = new javax.swing.JTextField();
        txtNombreUsuario = new javax.swing.JTextField();
        txtCodigoRegistro = new javax.swing.JTextField();
        ComboBoxRolUsuario = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaUsuario = new javax.swing.JTable();
        btnRegistrarUsuario = new javax.swing.JButton();
        btnEditarUsuario = new javax.swing.JButton();
        btnEliminarUsuario = new javax.swing.JButton();
        btnLimpiarUsuarios = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Correo:");

        jLabel2.setText("Contraseña:");

        jLabel3.setText("Nombre:");

        jLabel4.setText("Rol de Usuario:");

        jLabel5.setText("Codigo de Autorización:");

        ComboBoxRolUsuario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Supervisor", "Vendedor de Caja" }));

        TablaUsuario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CORREO", "CONTRASEÑA", "NOMBRE", "ROL"
            }
        ));
        TablaUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaUsuarioMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TablaUsuario);
        if (TablaUsuario.getColumnModel().getColumnCount() > 0) {
            TablaUsuario.getColumnModel().getColumn(0).setPreferredWidth(60);
            TablaUsuario.getColumnModel().getColumn(2).setPreferredWidth(65);
            TablaUsuario.getColumnModel().getColumn(3).setPreferredWidth(65);
            TablaUsuario.getColumnModel().getColumn(4).setPreferredWidth(65);
        }

        btnRegistrarUsuario.setText("Registrar");
        btnRegistrarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarUsuarioActionPerformed(evt);
            }
        });

        btnEditarUsuario.setText("Editar");
        btnEditarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarUsuarioActionPerformed(evt);
            }
        });

        btnEliminarUsuario.setText("Eliminar");
        btnEliminarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarUsuarioActionPerformed(evt);
            }
        });

        btnLimpiarUsuarios.setText("Limpiar");
        btnLimpiarUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarUsuariosActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel6.setText("Usuarios ");

        jButton1.setText("Regresar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(216, 216, 216))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCodigoRegistro))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtContraseñaUsuario))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNombreUsuario))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBoxRolUsuario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCorreoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnEliminarUsuario)
                                    .addComponent(btnRegistrarUsuario))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnEditarUsuario, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnLimpiarUsuarios, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(126, 126, 126)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtCorreoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtContraseñaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(ComboBoxRolUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtCodigoRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRegistrarUsuario)
                    .addComponent(btnEditarUsuario))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminarUsuario)
                    .addComponent(btnLimpiarUsuarios))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 490));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TablaUsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaUsuarioMouseClicked
        // TODO add your handling code here:
    int fila = TablaUsuario.getSelectedRow();
    if (fila >= 0) {
        idUsuarioSeleccionado = Integer.parseInt(TablaUsuario.getValueAt(fila, 0).toString()); // ID interno
        txtCorreoUsuario.setText(TablaUsuario.getValueAt(fila, 1).toString());
        txtContraseñaUsuario.setText(TablaUsuario.getValueAt(fila, 2).toString());
        txtNombreUsuario.setText(TablaUsuario.getValueAt(fila, 3).toString());
        String rol = TablaUsuario.getValueAt(fila, 4).toString().trim();
            for (int i = 0; i < ComboBoxRolUsuario.getItemCount(); i++) {
                if (ComboBoxRolUsuario.getItemAt(i).equalsIgnoreCase(rol)) {
                    ComboBoxRolUsuario.setSelectedIndex(i);
                    break;
                }
            }


    }
    }//GEN-LAST:event_TablaUsuarioMouseClicked

    private void btnRegistrarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarUsuarioActionPerformed
        // TODO add your handling code here:
        registrarUsuario();
    }//GEN-LAST:event_btnRegistrarUsuarioActionPerformed

    private void btnEditarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarUsuarioActionPerformed
        // TODO add your handling code here:
        editarUsuario();
    }//GEN-LAST:event_btnEditarUsuarioActionPerformed

    private void btnEliminarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarUsuarioActionPerformed
        // TODO add your handling code here:
        eliminarUsuario();
    }//GEN-LAST:event_btnEliminarUsuarioActionPerformed

    private void btnLimpiarUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarUsuariosActionPerformed
        // TODO add your handling code here:
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarUsuariosActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        SistemaPrincipal sis = new SistemaPrincipal();
        sis.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(Usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Usuarios dialog = new Usuarios(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBoxRolUsuario;
    private javax.swing.JTable TablaUsuario;
    private javax.swing.JButton btnEditarUsuario;
    private javax.swing.JButton btnEliminarUsuario;
    private javax.swing.JButton btnLimpiarUsuarios;
    private javax.swing.JButton btnRegistrarUsuario;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCodigoRegistro;
    private javax.swing.JTextField txtContraseñaUsuario;
    private javax.swing.JTextField txtCorreoUsuario;
    private javax.swing.JTextField txtNombreUsuario;
    // End of variables declaration//GEN-END:variables
}
