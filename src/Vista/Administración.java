
package Vista;

import Modelo.Articulo;
import Modelo.ArticuloDAO;
import Modelo.Categoria;
import Modelo.CategoriaDAO;
import Modelo.DetalleProducto;
import Modelo.DetalleProductoDAO;
import Modelo.Producto;
import Modelo.ProductoDAO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class Administración extends javax.swing.JFrame {
    

    private ProductoDAO productoDAO = new ProductoDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private DefaultTableModel modeloProducto;
    private DefaultTableModel modeloCategoria;
    private int idProductoSeleccionado = -1;
    private Producto productoActualDetalle;
    private DetalleProductoDAO detalleProductoDAO = new DetalleProductoDAO();
    private ArticuloDAO articuloDAO = new ArticuloDAO();
    private String rutaImagenSeleccionada = "";


    private Compras comprasPanel;
    public void setComprasPanel(Compras comprasPanel) {
        this.comprasPanel = comprasPanel;
    }

   


    /**
     * Creates new form Administración
     */
    public Administración() {
       
        initComponents();
        this.setLocationRelativeTo(null);
        modeloCategoria = (DefaultTableModel) TableCategoria.getModel();
        modeloProducto = (DefaultTableModel) TableProductos.getModel();
        jDateChooser1.setDate(new java.util.Date());
        jDateChooser1.getDateEditor().setEnabled(false);
        jDateChooser1.setEnabled(false);
        cargarTablaCategoria();
        cargarComboBoxCategorias();
        cargarTablaProductos();
        cargarComboArticulos();

        txtIdProductos.setVisible(false);
   


    // Evento de selección de tabla
    TableProductos.getSelectionModel().addListSelectionListener(e -> {
        int fila = TableProductos.getSelectedRow();
        if (fila != -1 && fila < productos.size()) {
            Producto p = productos.get(fila);
            idProductoSeleccionado = p.getId();
            txtIdProductos.setText(String.valueOf(p.getId()));
            txtCodigoProducto.setText(p.getCodigo());
            txtNombreProducto.setText(p.getNombre());
            txtPrecioDeVentaProducto.setText(String.valueOf(p.getPrecio()));
            ComboBoxSelecCategoria.setSelectedItem(p.getCategoria()); // ahora es String
        }
    });


  
    Articulo.addChangeListener(e -> {
    int index = Articulo.getSelectedIndex();
    String titulo = Articulo.getTitleAt(index).trim();
//    System.out.println("→ Pestaña seleccionada: " + titulo);


    if (titulo.equals("Productos")) {
        cargarComboBoxCategorias();
        cargarTablaProductos();
    } else if (titulo.equals("Articulo")) {
        cargarTablaArticulos(); //  Aquí se carga la tabla de artículos
        cargarComboArticulos();
    }

});

    txtCodigoDetalleProducto.addKeyListener(new java.awt.event.KeyAdapter() {
    @Override
    public void keyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            buscarYMostrarDetallesPorCodigo();
        }
    }
});

    
    
    }

    //CODIGO PARA PESTAÑA CATEGORIA
    private void cargarTablaCategoria() {
    modeloCategoria.setRowCount(0);
    for (Modelo.Categoria c : categoriaDAO.listar()) {
        modeloCategoria.addRow(new Object[]{c.getId(), c.getNombre()});
    }
}

    private void limpiarFormularioCategoria() {
    txtNombreCategoria.setText("");
    TableCategoria.clearSelection();
}

  
    //CODIGO PARA PESTAÑA PRODUCTOS
   
    // Cargar ComboBox con nombres de categorías
    private void cargarComboBoxCategorias() {
        ComboBoxSelecCategoria.removeAllItems();
        for (Categoria c : categoriaDAO.listar()) {
            ComboBoxSelecCategoria.addItem(c.getNombre());
        }
    }

    // Lista local de productos
    private List<Producto> productos = new ArrayList<>();

    // Cargar tabla de productos
    private void cargarTablaProductos() {
    modeloProducto.setRowCount(0);
    productos = productoDAO.listar(); // sincroniza la lista
    for (Producto p : productos) {
        modeloProducto.addRow(new Object[]{
            p.getCodigo(),
            p.getNombre(),
            p.getPrecio(),
            p.getCategoria()  // ahora es String directamente
        });
    }
}

    // Guardar nuevo producto
   private void guardarProducto() {
    try {
        String codigo = txtCodigoProducto.getText().trim();
        String nombre = txtNombreProducto.getText().trim();
        String precioStr = txtPrecioDeVentaProducto.getText().trim();
        String nombreCategoria = ComboBoxSelecCategoria.getSelectedItem().toString();

        if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        // Validar duplicados antes de insertar
        if (productoDAO.existeCodigo(codigo)) {
            JOptionPane.showMessageDialog(this, "Ya existe un producto con ese código.");
            return;
        }

        if (productoDAO.existeNombre(nombre)) {
            JOptionPane.showMessageDialog(this, "Ya existe un producto con ese nombre.");
            return;
        }

        double precio = Double.parseDouble(precioStr);
        int idCategoria = categoriaDAO.obtenerIdPorNombre(nombreCategoria);

        if (productoDAO.insertar(codigo, nombre, precio, idCategoria, rutaImagenSeleccionada)) {
            cargarTablaProductos();
            limpiarFormularioProducto();

            int opcion = JOptionPane.showConfirmDialog(
                this,
                "Producto guardado correctamente.\n¿Deseas agregar artículos correspondientes?",
                "Agregar Detalles",
                JOptionPane.YES_NO_OPTION
            );

            if (opcion == JOptionPane.YES_OPTION) {
                Articulo.setSelectedIndex(2); // Cambiar a pestaña Detalle Producto
                txtCodigoDetalleProducto.setText(codigo); // Prellenar campo
                buscarYMostrarDetallesPorCodigo(); // Cargar detalles del producto
            }
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Precio inválido. Usa solo números.", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    // Actualizar producto
    private void actualizarProducto() {
    try {
        if (idProductoSeleccionado == -1) 
            throw new IllegalStateException("Selecciona un producto.");

        String codigo = txtCodigoProducto.getText().trim();
        String nombre = txtNombreProducto.getText().trim();
        String precioStr = txtPrecioDeVentaProducto.getText().trim();
        String nombreCategoria = ComboBoxSelecCategoria.getSelectedItem().toString();

        if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty())
            throw new IllegalArgumentException("Todos los campos son obligatorios.");

        double precio = Double.parseDouble(precioStr);
        int idCategoria = categoriaDAO.obtenerIdPorNombre(nombreCategoria);

        // 🚨 Verificar si no se seleccionó una nueva imagen
        // Si no eligió una nueva, conservar la que ya tiene el producto
        if (rutaImagenSeleccionada == null || rutaImagenSeleccionada.isEmpty()) {
            rutaImagenSeleccionada = productoDAO.obtenerUrlImagenPorId(idProductoSeleccionado);
        }

        if (productoDAO.actualizar(idProductoSeleccionado, codigo, nombre, precio, idCategoria, rutaImagenSeleccionada)) {
            cargarTablaProductos();
            limpiarFormularioProducto();
            JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Precio inválido. Solo números.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
    }
}


    // Eliminar producto
    private void eliminarProducto() {
    try {
        int fila = TableProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto primero.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el producto seleccionado
        int id = productos.get(fila).getId();
        String nombreProducto = productos.get(fila).getNombre();
        String rutaImagen = productos.get(fila).getUrlImagen(); // ← IMPORTANTE

        // Confirmación
        int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Estás seguro de eliminar el producto \"" + nombreProducto + "\"?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Intentar eliminar de la BD
        boolean eliminado = productoDAO.eliminar(id);

        if (eliminado) {

            // Si existe una imagen, eliminarla del disco
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                File archivo = new File(rutaImagen);
                if (archivo.exists()) archivo.delete();
            }

            cargarTablaProductos();
            limpiarFormularioProducto();
            JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se puede eliminar el producto porque tiene registros asociados en otras tablas.",
                    "Error de integridad", JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


    // Limpiar formulario de productos
    private void limpiarFormularioProducto() {
    txtIdProductos.setText("");
    txtCodigoProducto.setText("");
    txtNombreProducto.setText("");
    txtPrecioDeVentaProducto.setText("");
    if (ComboBoxSelecCategoria.getItemCount() > 0)
        ComboBoxSelecCategoria.setSelectedIndex(0);
    TableProductos.clearSelection();
    idProductoSeleccionado = -1;
    txtRutaImagen.setText("");
}

    
    
    //CODIGO PARA PESTAÑA DETALLE PRODUCTO
    private void buscarYMostrarDetallesPorCodigo() {
    String codigo = txtCodigoDetalleProducto.getText().trim();
    if (codigo.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingresa un código de producto.");
        return;
    }

    productoActualDetalle = productoDAO.buscarPorCodigo(codigo);
    if (productoActualDetalle == null) {
        JOptionPane.showMessageDialog(this, "Producto no encontrado.");
        return;
    }

    // Tabla pequeña
    DefaultTableModel modeloMini = (DefaultTableModel) TableCodigoNombreDetalleProducto.getModel();
    modeloMini.setRowCount(0);
    modeloMini.addRow(new Object[]{productoActualDetalle.getCodigo(), productoActualDetalle.getNombre()});

    // Tabla grande
    cargarTablaDetalleProducto(productoActualDetalle.getId());
}

    private void cargarTablaDetalleProducto(int idProducto) {
    DefaultTableModel modelo = (DefaultTableModel) TableDetalleProducto.getModel();
    modelo.setRowCount(0);
    for (DetalleProducto dp : productoDAO.listarPorProducto(idProducto)) {
        modelo.addRow(new Object[]{
            dp.getIdDetalle(),
            dp.getCodigoProducto(),
            dp.getNombreProducto(),
            dp.getNombreArticulo(),
            dp.getCantidad()
        });
    }
}
    
    private void agregarDetalleProducto() {
    if (productoActualDetalle == null) {
        JOptionPane.showMessageDialog(this, "Primero busca un producto.");
        return;
    }

    String nombreArticulo = ComboSeleccArtDetalleProducto.getSelectedItem().toString();
    int cantidad;

    try {
        cantidad = Integer.parseInt(txtCantidadArticulo.getText().trim());
        if (cantidad <= 0) throw new NumberFormatException();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Cantidad inválida.");
        return;
    }

   
    Articulo articulo = articuloDAO.buscarPorNombre(nombreArticulo);

    if (articulo == null) {
        JOptionPane.showMessageDialog(this, "Artículo no encontrado.");
        return;
    }

   boolean insertado = detalleProductoDAO.insertar(
    productoActualDetalle.getId(),   // ID del producto
    articulo.getIdArticulo(),        // ID del artículo
    cantidad                         // cantidad
);

    if (insertado) {
        cargarTablaDetalleProducto(productoActualDetalle.getId());
        
        JOptionPane.showMessageDialog(this, "Detalle agregado correctamente.");
    } else {
        JOptionPane.showMessageDialog(this, "Error al agregar detalle.");
    }
}
    
    private void eliminarDetalleSeleccionado() {
    int fila = TableDetalleProducto.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un detalle para eliminar.");
        return;
    }

    int idDetalle = (int) TableDetalleProducto.getValueAt(fila, 0);
    int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este detalle?", "Confirmar", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        if (productoDAO.eliminarDetalle(idDetalle)) {
            cargarTablaDetalleProducto(productoActualDetalle.getId());
            limpiarFormularioDetalleProducto();
            JOptionPane.showMessageDialog(this, "Detalle eliminado.");
        }
    }
}

    private void limpiarFormularioDetalleProducto() {
    // Limpiar campos de texto
    txtCantidadArticulo.setText("");
    txtCodigoDetalleProducto.setText("");

    // Restablecer ComboBox si tiene elementos
    if (ComboSeleccArtDetalleProducto.getItemCount() > 0) {
        ComboSeleccArtDetalleProducto.setSelectedIndex(0);
    }

    // Limpiar selección de tabla
    TableDetalleProducto.clearSelection();

    // Limpiar tabla pequeña si la usas
    DefaultTableModel modeloMini = (DefaultTableModel) TableCodigoNombreDetalleProducto.getModel();
    modeloMini.setRowCount(0);

    // Resetear variable de producto actual
    productoActualDetalle = null;
}

    private void cargarComboArticulos() {
    ComboSeleccArtDetalleProducto.removeAllItems();

    List<Articulo> lista = articuloDAO.listar();
    for (Articulo a : lista) {
        ComboSeleccArtDetalleProducto.addItem(a.getNombre()); // Solo el nombre
    }
}

    private void actualizarDetalleProducto() {
    int fila = TableDetalleProducto.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un detalle para actualizar.");
        return;
    }

    int idDetalle = (int) TableDetalleProducto.getValueAt(fila, 0);

    int nuevaCantidad;
    try {
        nuevaCantidad = Integer.parseInt(txtCantidadArticulo.getText().trim());
        if (nuevaCantidad <= 0) throw new NumberFormatException();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Cantidad inválida.");
        return;
    }

    boolean actualizado = detalleProductoDAO.actualizar(idDetalle, nuevaCantidad);
    if (actualizado) {
        cargarTablaDetalleProducto(productoActualDetalle.getId());
        limpiarFormularioDetalleProducto();
        JOptionPane.showMessageDialog(this, "Detalle actualizado correctamente.");
    } else {
        JOptionPane.showMessageDialog(this, "Error al actualizar el detalle.");
    }
}




    //CODIGO PARA LA PESTAÑA ARTICULO
    // Insertar artículo usando ArticuloDAO
    
    private int idArticuloSeleccionado = -1; // para saber qué artículo se está editando/eliminando
    private void guardarArticulo() {
    String nombre = txtNombreArticulo.getText().trim();
    String unidad = txtUnidadArticulo.getText().trim();

    if (nombre.isEmpty() || unidad.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
        return;
    }
    if (articuloDAO.existeNombre(nombre)) {
    JOptionPane.showMessageDialog(this, "Ya existe un artículo con ese nombre.");
    return;
}


    Articulo a = new Articulo();
    a.setNombre(nombre);
    a.setUnidad(unidad);

    if (articuloDAO.insertar(a)) {
    cargarTablaArticulos();
    cargarComboArticulos();
    limpiarFormularioArticulo();
    JOptionPane.showMessageDialog(this, "Artículo guardado correctamente.");

    if (comprasPanel != null) {
        System.out.println("Llamando cargarArticulos desde Administración");
        comprasPanel.recargarArticulos();
    } else {
        System.out.println("comprasPanel es null");
    }
}
    }

// Limpiar formulario de artículo
  private void limpiarFormularioArticulo() {
    txtNombreArticulo.setText("");
    txtUnidadArticulo.setText("");
    TableArticulo.clearSelection();
    idArticuloSeleccionado = -1;
}




    private void cargarTablaArticulos() {
    DefaultTableModel modelo = (DefaultTableModel) TableArticulo.getModel();
    modelo.setRowCount(0);

    List<Articulo> lista = articuloDAO.listar();
    for (Articulo a : lista) {
        modelo.addRow(new Object[]{
            a.getIdArticulo(),
            a.getNombre(),
            a.getUnidad()
        });
    }
}


  private void eliminarArticulo() {
    if (idArticuloSeleccionado == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un artículo para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro de eliminar este artículo?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        // Verificar si el artículo tiene detalles asociados
        boolean tieneDetalles = articuloDAO.tieneDetallesAsociados(idArticuloSeleccionado);
        if (tieneDetalles) {
            JOptionPane.showMessageDialog(this,
                    "No se puede eliminar el artículo porque tiene registros asociados en ventas o inventario.",
                    "Error de integridad", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Intentar eliminar
        if (articuloDAO.eliminarArticulo(idArticuloSeleccionado)) {
            cargarTablaArticulos();
            limpiarFormularioArticulo();
            cargarComboArticulos();

            // Actualizar combo en ventana de Compras
            if (comprasPanel != null) {
        System.out.println("Llamando cargarArticulos desde Administracion");
        comprasPanel.recargarArticulos();
    } else {
        System.out.println(" comprasPanel es null");
    }

    JOptionPane.showMessageDialog(this, "Artículo eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
} else {
            JOptionPane.showMessageDialog(this, "Error al eliminar artículo.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al eliminar artículo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel15 = new javax.swing.JLabel();
        btnRegresar = new javax.swing.JButton();
        Articulo = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNombreCategoria = new javax.swing.JTextField();
        btnGuardarCategoria = new javax.swing.JButton();
        btnLimpiarFormularioCategoria = new javax.swing.JButton();
        btnEliminarCategoria = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        TableCategoria = new javax.swing.JTable();
        btnEditarCategoria = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtCodigoProducto = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtNombreProducto = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtPrecioDeVentaProducto = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        ComboBoxSelecCategoria = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        TableProductos = new javax.swing.JTable();
        btnGuardarProducto = new javax.swing.JButton();
        btnLimpiarFormularioProducto = new javax.swing.JButton();
        btnEliminarProducto = new javax.swing.JButton();
        btnEditarProductos = new javax.swing.JButton();
        txtIdProductos = new javax.swing.JTextField();
        btnSeleccionarImagen = new javax.swing.JButton();
        txtRutaImagen = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtCodigoDetalleProducto = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        TableCodigoNombreDetalleProducto = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        ComboSeleccArtDetalleProducto = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        txtCantidadArticulo = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        TableDetalleProducto = new javax.swing.JTable();
        btnAgregarDetalleProducto = new javax.swing.JButton();
        btnEliminarDetalleProducto = new javax.swing.JButton();
        btnLimpiarFormularioDetalleProducto = new javax.swing.JButton();
        btnEditarDetalleProducto = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtNombreArticulo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtUnidadArticulo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableArticulo = new javax.swing.JTable();
        btnGuardarArticulo = new javax.swing.JButton();
        btnLimpiarFormularioArticulo = new javax.swing.JButton();
        btnEliminarArticulo = new javax.swing.JButton();
        btnEditarArticulo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Administración:");

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRegresar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(214, 214, 214)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnRegresar)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19))))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 890, 90));

        jLabel1.setText("Nueva Categoria ");

        jLabel2.setText("Nombre:");

        btnGuardarCategoria.setText("Guardar");
        btnGuardarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarCategoriaActionPerformed(evt);
            }
        });

        btnLimpiarFormularioCategoria.setText("Limpiar");
        btnLimpiarFormularioCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarFormularioCategoriaActionPerformed(evt);
            }
        });

        btnEliminarCategoria.setText("Eliminar");
        btnEliminarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarCategoriaActionPerformed(evt);
            }
        });

        TableCategoria.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombre"
            }
        ));
        TableCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableCategoriaMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(TableCategoria);
        if (TableCategoria.getColumnModel().getColumnCount() > 0) {
            TableCategoria.getColumnModel().getColumn(1).setPreferredWidth(50);
        }

        btnEditarCategoria.setText("Actualizar");
        btnEditarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarCategoriaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(txtNombreCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnGuardarCategoria)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimpiarFormularioCategoria))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEditarCategoria)
                            .addComponent(btnEliminarCategoria))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(46, 46, 46)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombreCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGuardarCategoria)
                            .addComponent(btnLimpiarFormularioCategoria))
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarCategoria)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditarCategoria)))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        Articulo.addTab("Categoría", jPanel2);

        jLabel6.setText("Nuevo Producto");

        jLabel7.setText("Código:");

        jLabel8.setText("Nombre del Producto");

        jLabel9.setText("Precio de Venta:");

        jLabel10.setText("Categoria:");

        TableProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nombre", "Precio", "Categoria"
            }
        ));
        TableProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableProductosMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(TableProductos);
        if (TableProductos.getColumnModel().getColumnCount() > 0) {
            TableProductos.getColumnModel().getColumn(1).setPreferredWidth(70);
            TableProductos.getColumnModel().getColumn(2).setPreferredWidth(50);
            TableProductos.getColumnModel().getColumn(3).setPreferredWidth(50);
        }

        btnGuardarProducto.setText("Guardar");
        btnGuardarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProductoActionPerformed(evt);
            }
        });

        btnLimpiarFormularioProducto.setText("Limpiar");
        btnLimpiarFormularioProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarFormularioProductoActionPerformed(evt);
            }
        });

        btnEliminarProducto.setText("Eliminar");
        btnEliminarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProductoActionPerformed(evt);
            }
        });

        btnEditarProductos.setText("Actualizar");
        btnEditarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarProductosActionPerformed(evt);
            }
        });

        btnSeleccionarImagen.setText("Seleccionar Imagen");
        btnSeleccionarImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarImagenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(txtCodigoProducto)
                            .addComponent(jLabel8)
                            .addComponent(txtNombreProducto)
                            .addComponent(jLabel9)
                            .addComponent(txtPrecioDeVentaProducto)
                            .addComponent(jLabel10)
                            .addComponent(ComboBoxSelecCategoria, 0, 195, Short.MAX_VALUE)
                            .addComponent(txtIdProductos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnGuardarProducto)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiarFormularioProducto)
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarProducto)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditarProductos)
                        .addGap(18, 18, 18)
                        .addComponent(btnSeleccionarImagen)
                        .addGap(18, 18, 18)
                        .addComponent(txtRutaImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(22, 22, 22)
                        .addComponent(txtIdProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPrecioDeVentaProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(ComboBoxSelecCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardarProducto)
                    .addComponent(btnLimpiarFormularioProducto)
                    .addComponent(btnEliminarProducto)
                    .addComponent(btnEditarProductos)
                    .addComponent(btnSeleccionarImagen)
                    .addComponent(txtRutaImagen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        Articulo.addTab("Productos ", jPanel3);

        jLabel11.setText("Articulos que Contiene el Producto");

        jLabel12.setText("Codigo");

        TableCodigoNombreDetalleProducto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Nombre"
            }
        ));
        jScrollPane4.setViewportView(TableCodigoNombreDetalleProducto);
        if (TableCodigoNombreDetalleProducto.getColumnModel().getColumnCount() > 0) {
            TableCodigoNombreDetalleProducto.getColumnModel().getColumn(1).setPreferredWidth(60);
        }

        jLabel13.setText("Selecciones los Articulos que Contiene");

        jLabel14.setText("Cantidad");

        TableDetalleProducto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Codigo", "Producto", "Artículo", "Cantidad"
            }
        ));
        jScrollPane5.setViewportView(TableDetalleProducto);
        if (TableDetalleProducto.getColumnModel().getColumnCount() > 0) {
            TableDetalleProducto.getColumnModel().getColumn(0).setPreferredWidth(80);
            TableDetalleProducto.getColumnModel().getColumn(1).setPreferredWidth(80);
            TableDetalleProducto.getColumnModel().getColumn(3).setPreferredWidth(80);
            TableDetalleProducto.getColumnModel().getColumn(4).setPreferredWidth(60);
        }

        btnAgregarDetalleProducto.setText("Agregar");
        btnAgregarDetalleProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarDetalleProductoActionPerformed(evt);
            }
        });

        btnEliminarDetalleProducto.setText("Eliminar");
        btnEliminarDetalleProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarDetalleProductoActionPerformed(evt);
            }
        });

        btnLimpiarFormularioDetalleProducto.setText("Limpiar");
        btnLimpiarFormularioDetalleProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarFormularioDetalleProductoActionPerformed(evt);
            }
        });

        btnEditarDetalleProducto.setText("Modificar");
        btnEditarDetalleProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarDetalleProductoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12)
                    .addComponent(txtCodigoDetalleProducto)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel13)
                    .addComponent(ComboSeleccArtDetalleProducto, 0, 266, Short.MAX_VALUE)
                    .addComponent(jLabel14)
                    .addComponent(txtCantidadArticulo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnAgregarDetalleProducto)
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarDetalleProducto)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiarFormularioDetalleProducto)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditarDetalleProducto)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(29, 29, 29)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCodigoDetalleProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ComboSeleccArtDetalleProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCantidadArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarDetalleProducto)
                    .addComponent(btnEliminarDetalleProducto)
                    .addComponent(btnLimpiarFormularioDetalleProducto)
                    .addComponent(btnEditarDetalleProducto))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        Articulo.addTab("Detalle Producto ", jPanel4);

        jLabel3.setText("Nuevo Articulo");

        jLabel4.setText("Nombre:");

        jLabel5.setText("Unidad:");

        TableArticulo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombre", "Unidad"
            }
        ));
        TableArticulo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableArticuloMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TableArticulo);
        if (TableArticulo.getColumnModel().getColumnCount() > 0) {
            TableArticulo.getColumnModel().getColumn(0).setPreferredWidth(60);
            TableArticulo.getColumnModel().getColumn(2).setPreferredWidth(80);
        }

        btnGuardarArticulo.setText("Guardar ");
        btnGuardarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarArticuloActionPerformed(evt);
            }
        });

        btnLimpiarFormularioArticulo.setText("Limpiar ");
        btnLimpiarFormularioArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarFormularioArticuloActionPerformed(evt);
            }
        });

        btnEliminarArticulo.setText("Eliminar");
        btnEliminarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarArticuloActionPerformed(evt);
            }
        });

        btnEditarArticulo.setText("Actualizar");
        btnEditarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarArticuloActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(txtNombreArticulo)
                        .addComponent(jLabel5)
                        .addComponent(txtUnidadArticulo, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnEditarArticulo)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(btnGuardarArticulo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLimpiarFormularioArticulo)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEliminarArticulo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNombreArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUnidadArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGuardarArticulo)
                            .addComponent(btnLimpiarFormularioArticulo)
                            .addComponent(btnEliminarArticulo))
                        .addGap(18, 18, 18)
                        .addComponent(btnEditarArticulo)))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        Articulo.addTab("Articulo", jPanel5);

        getContentPane().add(Articulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 890, 460));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarCategoriaActionPerformed
        // TODO add your handling code here:

    String nombre = txtNombreCategoria.getText().trim();
    if (nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.");
        return;
    }
    if (categoriaDAO.existeNombre(nombre)) {
        JOptionPane.showMessageDialog(this, "Ya existe una categoría con ese nombre.");
        return;
    }
    
    if (categoriaDAO.insertar(nombre)) {
        cargarTablaCategoria();
        limpiarFormularioCategoria();
        JOptionPane.showMessageDialog(this, "Categoría guardada correctamente.");
    }

    }//GEN-LAST:event_btnGuardarCategoriaActionPerformed

    private void btnEditarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarCategoriaActionPerformed
        // TODO add your handling code here:
        
    int fila = TableCategoria.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona una categoría para actualizar.");
        return;
    }
    int id = (int) TableCategoria.getValueAt(fila, 0);
    String nuevoNombre = txtNombreCategoria.getText().trim();
    if (nuevoNombre.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.");
        return;
    }
    if (categoriaDAO.actualizar(id, nuevoNombre)) {
        cargarTablaCategoria();
        limpiarFormularioCategoria();
        JOptionPane.showMessageDialog(this, "Categoría actualizada.");
    }


    }//GEN-LAST:event_btnEditarCategoriaActionPerformed

    private void btnEliminarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCategoriaActionPerformed
        // TODO add your handling code here:
     
    int fila = TableCategoria.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona una categoría para eliminar.");
        return;
    }
    
    int id = (int) TableCategoria.getValueAt(fila, 0);

    if (productoDAO.existenProductosConCategoria(id)) {
        JOptionPane.showMessageDialog(this, "No puedes eliminar esta categoría porque tiene productos asociados.");
        return;
    }

   
    int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar esta categoría?", "Confirmar", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        if (categoriaDAO.eliminar(id)) {
            cargarTablaCategoria();
            cargarComboBoxCategorias(); // 🔁 sincroniza ComboBox
            limpiarFormularioCategoria();
            JOptionPane.showMessageDialog(this, "Categoría eliminada correctamente.");
        }
    }

    }//GEN-LAST:event_btnEliminarCategoriaActionPerformed

    private void btnLimpiarFormularioCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarFormularioCategoriaActionPerformed
        // TODO add your handling code here:
      limpiarFormularioCategoria();

    }//GEN-LAST:event_btnLimpiarFormularioCategoriaActionPerformed

    private void TableCategoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableCategoriaMouseClicked
        // TODO add your handling code here:
                                               
    int fila = TableCategoria.rowAtPoint(evt.getPoint());
    if (fila != -1) {
        String nombre = TableCategoria.getValueAt(fila, 1).toString();
        txtNombreCategoria.setText(nombre);
    }


    }//GEN-LAST:event_TableCategoriaMouseClicked

    private void TableProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableProductosMouseClicked
        // TODO add your handling code here:
                                             
    int fila = TableProductos.getSelectedRow();
    if (fila != -1 && fila < productos.size()) {
        Producto p = productos.get(fila);
        idProductoSeleccionado = p.getId();
        txtIdProductos.setText(String.valueOf(p.getId()));
        txtCodigoProducto.setText(p.getCodigo());
        txtNombreProducto.setText(p.getNombre());
        txtPrecioDeVentaProducto.setText(String.valueOf(p.getPrecio()));
        ComboBoxSelecCategoria.setSelectedItem(p.getCategoria());
    }
    
    


    }//GEN-LAST:event_TableProductosMouseClicked

    private void TableArticuloMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableArticuloMouseClicked
        // TODO add your handling code here:
   // Variables necesarias
                                        
    int fila = TableArticulo.getSelectedRow();
    if (fila != -1) { // Si hay una fila seleccionada
        // Obtenemos los datos de la fila seleccionada
        idArticuloSeleccionado = (int) TableArticulo.getValueAt(fila, 0); // idArticulo
        String nombre = (String) TableArticulo.getValueAt(fila, 1);
        String unidad = (String) TableArticulo.getValueAt(fila, 2);

        // Colocamos los datos en los campos de texto para editar
        txtNombreArticulo.setText(nombre);
        txtUnidadArticulo.setText(unidad);
    }
    


    }//GEN-LAST:event_TableArticuloMouseClicked

    private void btnEditarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarArticuloActionPerformed
        // TODO add your handling code here:
        
     if (idArticuloSeleccionado == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un artículo primero.");
        return;
    }

    String nuevoNombre = txtNombreArticulo.getText().trim();
    String nuevaUnidad = txtUnidadArticulo.getText().trim();

    if (nuevoNombre.isEmpty() || nuevaUnidad.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Completa todos los campos.");
        return;
    }

    // Crear objeto Articulo con los datos nuevos
    Articulo articulo = new Articulo();
    articulo.setIdArticulo(idArticuloSeleccionado);
    articulo.setNombre(nuevoNombre);
    articulo.setUnidad(nuevaUnidad);

    // Llamar al DAO para actualizar
    if (articuloDAO.actualizar(articulo)) {
        cargarTablaArticulos(); // recarga la tabla
        cargarComboArticulos();
        JOptionPane.showMessageDialog(this, "Artículo actualizado correctamente.");
        limpiarFormularioArticulo();
    } else {
        JOptionPane.showMessageDialog(this, "Error al actualizar el artículo.");
    }

    }//GEN-LAST:event_btnEditarArticuloActionPerformed

    private void btnEliminarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarArticuloActionPerformed
        // TODO add your handling code here:
                                                       
    eliminarArticulo();
    }//GEN-LAST:event_btnEliminarArticuloActionPerformed

    private void btnGuardarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarArticuloActionPerformed
        // TODO add your handling code here:
        guardarArticulo();
    }//GEN-LAST:event_btnGuardarArticuloActionPerformed

    private void btnLimpiarFormularioArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarFormularioArticuloActionPerformed
        // TODO add your handling code here:
        limpiarFormularioArticulo();
    }//GEN-LAST:event_btnLimpiarFormularioArticuloActionPerformed

    private void btnGuardarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProductoActionPerformed
        // TODO add your handling code here:
        guardarProducto();
    }//GEN-LAST:event_btnGuardarProductoActionPerformed

    private void btnLimpiarFormularioProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarFormularioProductoActionPerformed
        // TODO add your handling code here:
        limpiarFormularioProducto();
    }//GEN-LAST:event_btnLimpiarFormularioProductoActionPerformed

    private void btnEliminarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProductoActionPerformed
        // TODO add your handling code here:
        eliminarProducto();
    }//GEN-LAST:event_btnEliminarProductoActionPerformed

    private void btnEditarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarProductosActionPerformed
        // TODO add your handling code here:
        actualizarProducto();
    }//GEN-LAST:event_btnEditarProductosActionPerformed

    private void btnLimpiarFormularioDetalleProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarFormularioDetalleProductoActionPerformed
        // TODO add your handling code here:
        limpiarFormularioDetalleProducto();
    }//GEN-LAST:event_btnLimpiarFormularioDetalleProductoActionPerformed

    private void btnEditarDetalleProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarDetalleProductoActionPerformed
        // TODO add your handling code here:
        actualizarDetalleProducto();
    }//GEN-LAST:event_btnEditarDetalleProductoActionPerformed

    private void btnAgregarDetalleProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarDetalleProductoActionPerformed
        // TODO add your handling code here:
        agregarDetalleProducto();
    }//GEN-LAST:event_btnAgregarDetalleProductoActionPerformed

    private void btnRegresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegresarActionPerformed
        // TODO add your handling code here:
        SistemaPrincipal sis = new SistemaPrincipal();
        sis.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnRegresarActionPerformed

    private void btnEliminarDetalleProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarDetalleProductoActionPerformed
        // TODO add your handling code here:
        eliminarDetalleSeleccionado();
    }//GEN-LAST:event_btnEliminarDetalleProductoActionPerformed

    private void btnSeleccionarImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarImagenActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png"));

    int resultado = fileChooser.showOpenDialog(this);
    if (resultado == JFileChooser.APPROVE_OPTION) {
        File archivo = fileChooser.getSelectedFile();
        rutaImagenSeleccionada = archivo.getAbsolutePath();
        txtRutaImagen.setText(rutaImagenSeleccionada); // si tienes un textfield para mostrar la ruta
    }
    }//GEN-LAST:event_btnSeleccionarImagenActionPerformed

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
            java.util.logging.Logger.getLogger(Administración.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Administración.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Administración.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Administración.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Administración().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane Articulo;
    private javax.swing.JComboBox<String> ComboBoxSelecCategoria;
    private javax.swing.JComboBox<String> ComboSeleccArtDetalleProducto;
    private javax.swing.JTable TableArticulo;
    private javax.swing.JTable TableCategoria;
    private javax.swing.JTable TableCodigoNombreDetalleProducto;
    private javax.swing.JTable TableDetalleProducto;
    private javax.swing.JTable TableProductos;
    private javax.swing.JButton btnAgregarDetalleProducto;
    private javax.swing.JButton btnEditarArticulo;
    private javax.swing.JButton btnEditarCategoria;
    private javax.swing.JButton btnEditarDetalleProducto;
    private javax.swing.JButton btnEditarProductos;
    private javax.swing.JButton btnEliminarArticulo;
    private javax.swing.JButton btnEliminarCategoria;
    private javax.swing.JButton btnEliminarDetalleProducto;
    private javax.swing.JButton btnEliminarProducto;
    private javax.swing.JButton btnGuardarArticulo;
    private javax.swing.JButton btnGuardarCategoria;
    private javax.swing.JButton btnGuardarProducto;
    private javax.swing.JButton btnLimpiarFormularioArticulo;
    private javax.swing.JButton btnLimpiarFormularioCategoria;
    private javax.swing.JButton btnLimpiarFormularioDetalleProducto;
    private javax.swing.JButton btnLimpiarFormularioProducto;
    private javax.swing.JButton btnRegresar;
    private javax.swing.JButton btnSeleccionarImagen;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextField txtCantidadArticulo;
    private javax.swing.JTextField txtCodigoDetalleProducto;
    private javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtIdProductos;
    private javax.swing.JTextField txtNombreArticulo;
    private javax.swing.JTextField txtNombreCategoria;
    private javax.swing.JTextField txtNombreProducto;
    private javax.swing.JTextField txtPrecioDeVentaProducto;
    private javax.swing.JTextField txtRutaImagen;
    private javax.swing.JTextField txtUnidadArticulo;
    // End of variables declaration//GEN-END:variables
}
