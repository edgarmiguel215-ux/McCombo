
package Vista;

import Modelo.ReporteDAO;
import Modelo.login;
import Reportes.ExcelProductosVendidos;
import Reportes.ExcelVentasPorUsuario;
import java.awt.Color;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class Reporte extends javax.swing.JFrame {

    private login usuario;

//public void setUsuario(login usuario) {
//    this.usuario = usuario;
//}

    public Reporte(login usuario) {
        this.usuario = usuario;
        initComponents();
        aplicarPlaceholder(txtBuscarProducto, "Buscar Productos");
        aplicarPlaceholder(txtBuscarUsuario, "Buscar por Usuarios");
        cargarVentasPorUsuario();
        cargarProductosMasVendidos();
        inicializarReportes();
        this.setLocationRelativeTo(null);
    }

    ////////// CARGAR VENTAS POR RANGO DE FECHAS/////////////
    private void cargarVentasPorRango() {
    Date desde = jDateChooser1.getDate();
    Date hasta = jDateChooser2.getDate();

    if (desde == null || hasta == null) {
        JOptionPane.showMessageDialog(this, "Selecciona ambas fechas.");
        return;
    }

    ReporteDAO dao = new ReporteDAO();
    List<Object[]> datos = dao.ventasPorRango(desde, hasta);

    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("Fecha");
    modelo.addColumn("Total Ventas");
    modelo.addColumn("Cantidad Tickets");

    for (Object[] fila : datos) {
        modelo.addRow(fila);
    }

    tablaVentas.setModel(modelo);
}


    ///////////// CARGAR VENTAS POR USUARIO //////////////
    private void cargarVentasPorUsuario() {
    ReporteDAO dao = new ReporteDAO();
    List<Object[]> datos = dao.ventasPorUsuario();

    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("Usuario");
    modelo.addColumn("Total Ventas");
    modelo.addColumn("Cantidad Tickets");

    for (Object[] fila : datos) {
        modelo.addRow(fila);
    }

    tablaUsuarios.setModel(modelo);
}

    
    //////// REPORTE POR PRODUCTOS MAS VENDIDOS/////////////
    
    private void cargarProductosMasVendidos() {
    ReporteDAO dao = new ReporteDAO();
    List<Object[]> datos = dao.productosMasVendidos();

    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("Producto");
    modelo.addColumn("Cantidad Vendida");
    modelo.addColumn("Ingresos Generados");

    for (Object[] fila : datos) {
        modelo.addRow(fila);
    }

    tablaProductos.setModel(modelo);
}

    /////Busqueda /////
    private void inicializarReportes() {
    configurarBusquedasEnVivo();
}

    private void configurarBusquedasEnVivo() {
    txtBuscarUsuario.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            buscarVentasPorUsuario();
        }
    });

    txtBuscarProducto.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            buscarProductosMasVendidos();
        }
    });
}

    
    private void buscarVentasPorUsuario() {
    String nombre = txtBuscarUsuario.getText().trim();
    ReporteDAO dao = new ReporteDAO();
    List<Object[]> datos = nombre.isEmpty()
        ? dao.ventasPorUsuarioBusqueda()
        : dao.ventasPorUsuarioFiltro(nombre);

    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("Usuario");
    modelo.addColumn("Total Ventas");
    modelo.addColumn("Cantidad Tickets");

    for (Object[] fila : datos) {
        modelo.addRow(fila);
    }

    tablaUsuarios.setModel(modelo);
}

    private void buscarProductosMasVendidos() {
    String criterio = txtBuscarProducto.getText().trim();
    ReporteDAO dao = new ReporteDAO();
    List<Object[]> datos = criterio.isEmpty()
        ? dao.productosMasVendidosBusqueda()
        : dao.productosMasVendidosFiltro(criterio);

    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("Producto");
    modelo.addColumn("Cantidad Vendida");
    modelo.addColumn("Ingresos Generados");

    for (Object[] fila : datos) {
        modelo.addRow(fila);
    }

    tablaProductos.setModel(modelo);
}

    
    public static void aplicarPlaceholder(JTextField campo, String placeholder) {
    campo.setText(placeholder);
    campo.setForeground(Color.GRAY);

    campo.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (campo.getText().equals(placeholder)) {
                campo.setText("");
                campo.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (campo.getText().isEmpty()) {
                campo.setText(placeholder);
                campo.setForeground(Color.GRAY);
            }
        }
    });
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
        jButton1 = new javax.swing.JButton();
        tabbedReportes = new javax.swing.JTabbedPane();
        Ventas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaVentas = new javax.swing.JTable();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnFiltrarVentas = new javax.swing.JButton();
        Usuarios = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaUsuarios = new javax.swing.JTable();
        btnCargarVentasUsuario = new javax.swing.JButton();
        txtBuscarUsuario = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        Productos = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        txtBuscarProducto = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("Historial de Ventas");

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/regresar_2.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tablaVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Total Ventas", "Cantidad Tickets"
            }
        ));
        jScrollPane1.setViewportView(tablaVentas);
        if (tablaVentas.getColumnModel().getColumnCount() > 0) {
            tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(60);
            tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(60);
        }

        jLabel2.setText("Filtrar Desde:");

        jLabel3.setText("Hasta:");

        btnFiltrarVentas.setBackground(new java.awt.Color(255, 255, 255));
        btnFiltrarVentas.setForeground(new java.awt.Color(0, 0, 0));
        btnFiltrarVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/buscar.PNG"))); // NOI18N
        btnFiltrarVentas.setText("Filtrar");
        btnFiltrarVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarVentasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout VentasLayout = new javax.swing.GroupLayout(Ventas);
        Ventas.setLayout(VentasLayout);
        VentasLayout.setHorizontalGroup(
            VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VentasLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(VentasLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFiltrarVentas))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 709, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        VentasLayout.setVerticalGroup(
            VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VentasLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(btnFiltrarVentas)
                    .addGroup(VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(101, 101, 101))
        );

        tabbedReportes.addTab("Ventas", Ventas);

        tablaUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Usuarios", "Total Ventas", "Cantidad Tickets"
            }
        ));
        jScrollPane2.setViewportView(tablaUsuarios);
        if (tablaUsuarios.getColumnModel().getColumnCount() > 0) {
            tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(60);
            tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(60);
        }

        btnCargarVentasUsuario.setBackground(new java.awt.Color(255, 255, 255));
        btnCargarVentasUsuario.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnCargarVentasUsuario.setForeground(new java.awt.Color(0, 0, 0));
        btnCargarVentasUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/cargar.png"))); // NOI18N
        btnCargarVentasUsuario.setText("Cargar");
        btnCargarVentasUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarVentasUsuarioActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/excel.png"))); // NOI18N
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout UsuariosLayout = new javax.swing.GroupLayout(Usuarios);
        Usuarios.setLayout(UsuariosLayout);
        UsuariosLayout.setHorizontalGroup(
            UsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UsuariosLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(UsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(UsuariosLayout.createSequentialGroup()
                        .addComponent(txtBuscarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCargarVentasUsuario))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        UsuariosLayout.setVerticalGroup(
            UsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UsuariosLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(UsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addGroup(UsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCargarVentasUsuario)
                        .addComponent(txtBuscarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        tabbedReportes.addTab("Usuarios", Usuarios);

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Cantidad Vendida", "Ingresos Generados"
            }
        ));
        jScrollPane3.setViewportView(tablaProductos);
        if (tablaProductos.getColumnModel().getColumnCount() > 0) {
            tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(60);
            tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(60);
        }

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/excel.png"))); // NOI18N
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ProductosLayout = new javax.swing.GroupLayout(Productos);
        Productos.setLayout(ProductosLayout);
        ProductosLayout.setHorizontalGroup(
            ProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductosLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(ProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 677, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(ProductosLayout.createSequentialGroup()
                        .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jButton2)))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        ProductosLayout.setVerticalGroup(
            ProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductosLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(ProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        tabbedReportes.addTab("Productos", Productos);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 773, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(215, 215, 215)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(tabbedReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 840, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
       System.out.println("Usuario en boton Regresar: " + (usuario != null ? usuario.getNombre() : "null"));
        System.out.println("Rol en boton Regresar: " + (usuario != null ? usuario.getRol() : "null"));
        System.out.println("ID en boton Regresar: " + (usuario != null ? usuario.getId() : "null"));

        if (usuario != null) {
            SistemaPrincipal sp = new SistemaPrincipal(usuario); // ✅ usa el mismo objeto login
            sp.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error: usuario no inicializado.");
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnFiltrarVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarVentasActionPerformed
        // TODO add your handling code here:
        cargarVentasPorRango();
    }//GEN-LAST:event_btnFiltrarVentasActionPerformed

    private void btnCargarVentasUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarVentasUsuarioActionPerformed
        // TODO add your handling code here:
        cargarVentasPorUsuario();
    }//GEN-LAST:event_btnCargarVentasUsuarioActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        ExcelProductosVendidos.reporte();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        ExcelVentasPorUsuario.reporte();
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new Reporte().setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Productos;
    private javax.swing.JPanel Usuarios;
    private javax.swing.JPanel Ventas;
    private javax.swing.JButton btnCargarVentasUsuario;
    private javax.swing.JButton btnFiltrarVentas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane tabbedReportes;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTable tablaUsuarios;
    private javax.swing.JTable tablaVentas;
    private javax.swing.JTextField txtBuscarProducto;
    private javax.swing.JTextField txtBuscarUsuario;
    // End of variables declaration//GEN-END:variables
}
