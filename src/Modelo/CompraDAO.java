
package Modelo;

import java.sql.*;
import java.util.*;



public class CompraDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    Conexion cn = new Conexion();

    // Registrar compra
   public int registrarCompra(Compra compra) {
        int idCompraGenerada = 0;
        String sql = "INSERT INTO compras (comprobante, numero, metodo_pago, proveedor, fecha_compra, estado, total) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, compra.getComprobante());
            ps.setString(2, compra.getNumero());
            ps.setString(3, compra.getMetodoPago());
            ps.setString(4, compra.getProveedor()); // <-- aquí guardamos el nombre del proveedor
            ps.setDate(5, new java.sql.Date(compra.getFechaCompra().getTime()));
            ps.setString(6, compra.getEstado());
            ps.setDouble(7, compra.getTotal());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idCompraGenerada = generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar compra: " + e.getMessage());
        }

        return idCompraGenerada;
        // Después de guardar la compra
       

    }

    // Registrar detalle

    public boolean registrarDetalle(CompraDetalle detalle) {
    String sql = "INSERT INTO detalle_compras "
               + "(idCompras, idProducto, articulo, cantidad, precio, subtotal, proveedor, comprobante, metodoPago, fecha, estado, unidad) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    boolean exito = false;
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setInt(1, detalle.getIdCompra());
        ps.setInt(2, detalle.getIdProducto());
        ps.setString(3, detalle.getArticulo());
        ps.setInt(4, detalle.getCantidad());
        ps.setDouble(5, detalle.getPrecio());
        ps.setDouble(6, detalle.getSubtotal());
        ps.setString(7, detalle.getProveedor());
        ps.setString(8, detalle.getComprobante());
        ps.setString(9, detalle.getMetodoPago());
        ps.setDate(10, new java.sql.Date(detalle.getFecha().getTime()));
        ps.setString(11, detalle.getEstado());
        ps.setString(12, detalle.getUnidad());  // ← Solo unidad, stockMin eliminado

        exito = ps.executeUpdate() > 0;

        if (exito) {
            InventarioDAO inventarioDAO = new InventarioDAO();
            inventarioDAO.actualizarInventarioDesdeCompra(detalle);
        }

    } catch (SQLException e) {
        System.err.println("Error al registrar detalle: " + e.getMessage());
        exito = false;
    } finally {
        try { if (ps != null) ps.close(); if (con != null) con.close(); } catch (SQLException ex) {}
    }
    return exito;
}

    // Listar todas las compras
    public List<Compra> listarCompras() {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT * FROM compras";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Compra c = new Compra();
                c.setId(rs.getInt("id"));
                c.setComprobante(rs.getString("comprobante"));
                c.setNumero(rs.getString("numero"));
                c.setMetodoPago(rs.getString("metodo_pago"));
                c.setProveedor(rs.getString("proveedor"));
                c.setFechaCompra(rs.getDate("fecha_compra"));
                c.setEstado(rs.getString("estado"));
                c.setTotal(rs.getDouble("total"));
                lista.add(c);
            }
        } catch (Exception e) {
            System.err.println("Error al listar compras: " + e.getMessage());
        }
        return lista;
    }

    // Eliminar compra
    public boolean eliminarCompra(int id) {
        String sql = "DELETE FROM compras WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar compra: " + e.getMessage());
        }
        return false;
    }
    
    public boolean actualizarCompra(Compra c) {
    String sql = "UPDATE compras SET comprobante=?, numero=?, metodo_pago=?, proveedor=?, fecha_compra=?, estado=?, total=? WHERE idCompras=?";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setString(1, c.getComprobante());
        ps.setString(2, c.getNumero());
        ps.setString(3, c.getMetodoPago());
        ps.setString(4, c.getProveedor());
        ps.setDate(5, new java.sql.Date(c.getFechaCompra().getTime()));
        ps.setString(6, c.getEstado());
        ps.setDouble(7, c.getTotal());
        ps.setInt(8, c.getId()); // esto sigue siendo correcto si c.getId() devuelve idCompras
        ps.executeUpdate();
        return true;
    } catch (Exception e) {
        System.err.println("Error al actualizar compra: " + e.getMessage());
        return false;
    }
}
    // LISTAR TODAS LAS COMPRAS
    public List<Compra> listar() {
    List<Compra> lista = new ArrayList<>();
    String sql = "SELECT c.idCompras, p.nombre AS proveedor, c.comprobante, c.numero, " +
                 "c.metodo_pago, c.fecha_compra, c.estado, c.total " +
                 "FROM compras c " +
                 "INNER JOIN proveedor p ON c.idProveedor = p.idProveedor " +
                 "ORDER BY c.idCompras DESC";

    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();

        while (rs.next()) {
            Compra c = new Compra();
            c.setId(rs.getInt("idCompras"));
            c.setProveedor(rs.getString("proveedor"));
            c.setComprobante(rs.getString("comprobante"));
            c.setNumero(rs.getString("numero"));
            c.setMetodoPago(rs.getString("metodo_pago"));
            c.setFechaCompra(rs.getDate("fecha_compra"));
            c.setEstado(rs.getString("estado"));
            c.setTotal(rs.getDouble("total"));
            lista.add(c);
        }

    } catch (SQLException e) {
        System.err.println("Error al listar compras: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            System.err.println("Error al cerrar conexión: " + ex.getMessage());
        }
    }
    return lista;
}

    public int obtenerIdProductoPorNombre(String nombre) {
    int idProducto = -1;
    String sql = "SELECT id FROM productos WHERE nombre = ?";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            idProducto = rs.getInt("id");
        }

    } catch (SQLException e) {
        System.err.println("Error al buscar idProducto: " + e.getMessage());
    }
    return idProducto;
}

    public List<CompraDetalle> listarDetalles() {
    List<CompraDetalle> lista = new ArrayList<>();
    // Asegúrate de que la consulta incluya unidad y numero
    String sql = "SELECT dc.*, c.numero FROM detalle_compras dc " +
                 "INNER JOIN compras c ON dc.idCompras = c.idCompras " +
                 "ORDER BY dc.idCompras";
    
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();

        while (rs.next()) {
            CompraDetalle d = new CompraDetalle();
            
            d.setIdProducto(rs.getInt("idProducto"));
            d.setArticulo(rs.getString("articulo"));
            d.setIdCompra(rs.getInt("idCompras"));
            d.setCantidad(rs.getInt("cantidad"));
            d.setPrecio(rs.getDouble("precio"));
            d.setSubtotal(rs.getDouble("subtotal"));
            d.setProveedor(rs.getString("proveedor"));
            d.setComprobante(rs.getString("comprobante"));
            d.setMetodoPago(rs.getString("metodoPago"));
            d.setFecha(rs.getDate("fecha"));
            d.setEstado(rs.getString("estado"));
            d.setUnidad(rs.getString("unidad"));  // ← ¿Está en la tabla?
            d.setNumero(rs.getString("numero"));  // ← ¿Está en compras?
            
            // Debug
            System.out.println("DAO - Unidad: " + rs.getString("unidad") + 
                             ", Numero: " + rs.getString("numero"));
            
            lista.add(d);        
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

    public Compra obtenerCompraPorId(int idCompra) {
    Compra compra = null;
    String sql = "SELECT * FROM compras WHERE idCompras = ?";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setInt(1, idCompra);
        rs = ps.executeQuery();
        if (rs.next()) {
            compra = new Compra();
            compra.setId(rs.getInt("idCompras"));
            compra.setComprobante(rs.getString("comprobante"));
            compra.setNumero(rs.getString("numero"));
            compra.setMetodoPago(rs.getString("metodo_pago"));
            compra.setProveedor(rs.getString("proveedor"));
            compra.setFechaCompra(rs.getDate("fecha_compra"));
            compra.setEstado(rs.getString("estado"));
            compra.setTotal(rs.getDouble("total"));
        }
    } catch (Exception e) {
        System.err.println("Error al obtener compra por ID: " + e.getMessage());
    }
    return compra;
}

    public boolean actualizarDetalle(CompraDetalle detalle) {
    String sql = "UPDATE detalle_compras SET "
               + "articulo=?, cantidad=?, precio=?, subtotal=?, proveedor=?, comprobante=?, metodoPago=?, fecha=?, estado=? "
               + "WHERE idCompras=? AND idProducto=?";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setString(1, detalle.getArticulo());
        ps.setInt(2, detalle.getCantidad());
        ps.setDouble(3, detalle.getPrecio());
        ps.setDouble(4, detalle.getSubtotal());
        ps.setString(5, detalle.getProveedor());
        ps.setString(6, detalle.getComprobante());
        ps.setString(7, detalle.getMetodoPago());
        ps.setDate(8, new java.sql.Date(detalle.getFecha().getTime()));
        ps.setString(9, detalle.getEstado());
        ps.setInt(10, detalle.getIdCompra());
        ps.setInt(11, detalle.getIdProducto());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error al actualizar detalle: " + e.getMessage());
        return false;
    } finally {
        try { if (ps != null) ps.close(); if (con != null) con.close(); } catch (SQLException ex) {}
    }
}

    // Obtener la compra pendiente
    public Compra obtenerCompraPendiente() {
        Compra compra = null;
        String sql = "SELECT * FROM compras WHERE estado = 'PENDIENTE' ORDER BY idCompras ASC LIMIT 1"; // tomamos la primera pendiente
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                compra = new Compra();
                compra.setId(rs.getInt("idCompras"));
                compra.setComprobante(rs.getString("comprobante"));
                compra.setNumero(rs.getString("numero"));
                compra.setMetodoPago(rs.getString("metodo_pago"));
                compra.setProveedor(rs.getString("proveedor"));
                compra.setFechaCompra(rs.getDate("fecha_compra"));
                compra.setEstado(rs.getString("estado"));
                compra.setTotal(rs.getDouble("total"));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener compra pendiente: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); if (con != null) con.close(); } catch (SQLException ex) {}
        }
        return compra;
    }

        // Listar detalles por compra
   // Listar detalles por compra - VERSIÓN CORREGIDA
    public List<CompraDetalle> listarDetallesPorCompra(int idCompra) {
    List<CompraDetalle> lista = new ArrayList<>();
    String sql = "SELECT dc.*, c.numero " +  // ¡IMPORTANTE: Agregar c.numero!
                 "FROM detalle_compras dc " +
                 "INNER JOIN compras c ON dc.idCompras = c.idCompras " +
                 "WHERE dc.idCompras = ?";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setInt(1, idCompra);
        rs = ps.executeQuery();
        while (rs.next()) {
            CompraDetalle d = new CompraDetalle();
            d.setIdCompra(rs.getInt("idCompras"));
            d.setIdProducto(rs.getInt("idProducto"));
            d.setArticulo(rs.getString("articulo"));
            d.setCantidad(rs.getInt("cantidad"));
            d.setPrecio(rs.getDouble("precio"));
            d.setSubtotal(rs.getDouble("subtotal"));
            d.setProveedor(rs.getString("proveedor"));
            d.setComprobante(rs.getString("comprobante"));
            d.setMetodoPago(rs.getString("metodoPago"));
            d.setFecha(rs.getDate("fecha"));
            d.setEstado(rs.getString("estado"));
            d.setUnidad(rs.getString("unidad"));
            d.setNumero(rs.getString("numero")); // ¡IMPORTANTE: Establecer el número!
            
            lista.add(d);
        }
    } catch (Exception e) {
        System.err.println("Error al listar detalles por compra: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try { 
            if (rs != null) rs.close(); 
            if (ps != null) ps.close(); 
            if (con != null) con.close(); 
        } catch (SQLException ex) {}
    }
    return lista;
}

    // ? Obtener idCompra a partir del número de comprobante
    public int obtenerIdCompraPorNumero(String numero) {
    int idCompra = 0;
    String sql = "SELECT idCompras FROM compras WHERE numero = ?";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setString(1, numero);
        rs = ps.executeQuery();
        if (rs.next()) {
            idCompra = rs.getInt("idCompras");
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener idCompra por número: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            System.err.println("Error al cerrar conexión en obtenerIdCompraPorNumero: " + ex.getMessage());
        }
    }
    return idCompra;
}

   public boolean registrarDetalleCompra(CompraDetalle detalle) {
    String sql = "INSERT INTO detalle_compras "
               + "(idCompras, idProducto, cantidad, unidad, precio, subtotal, proveedor, comprobante, metodoPago, fecha, estado) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    boolean exito = false;

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, detalle.getIdCompra());
        ps.setInt(2, detalle.getIdProducto());
        ps.setInt(3, detalle.getCantidad());
        ps.setString(4, detalle.getUnidad());
        ps.setDouble(5, detalle.getPrecio());
        ps.setDouble(6, detalle.getSubtotal());
        ps.setString(7, detalle.getProveedor());
        ps.setString(8, detalle.getComprobante());
        ps.setString(9, detalle.getMetodoPago());
        ps.setDate(10, new java.sql.Date(detalle.getFecha().getTime()));
        ps.setString(11, detalle.getEstado());

        exito = ps.executeUpdate() > 0;

        if (exito) {
            InventarioDAO inventarioDAO = new InventarioDAO();
            inventarioDAO.actualizarInventarioDesdeCompra(detalle);
        }

    } catch (SQLException e) {
        System.err.println("Error al registrar detalle de compra: " + e.getMessage());
    }

    return exito;
}

    public boolean actualizarCompraCompleta(Compra compra, List<CompraDetalle> detalles) {
    String sqlCompra = "UPDATE compras SET comprobante=?, numero=?, metodo_pago=?, proveedor=?, fecha_compra=?, estado=?, total=? WHERE idCompras=?";
    String sqlDeleteDetalles = "DELETE FROM detalle_compras WHERE idCompras=?";
    String sqlInsertDetalle = "INSERT INTO detalle_compras (idCompras, idProducto, articulo, cantidad, precio, subtotal, proveedor, comprobante, metodoPago, fecha, estado, unidad) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    try {
        con = cn.getConnection();
        con.setAutoCommit(false); // Iniciar transacción

        // 1. Actualizar la compra principal
        ps = con.prepareStatement(sqlCompra);
        ps.setString(1, compra.getComprobante());
        ps.setString(2, compra.getNumero());
        ps.setString(3, compra.getMetodoPago());
        ps.setString(4, compra.getProveedor());
        ps.setDate(5, new java.sql.Date(compra.getFechaCompra().getTime()));
        ps.setString(6, compra.getEstado());
        ps.setDouble(7, compra.getTotal());
        ps.setInt(8, compra.getId());
        ps.executeUpdate();

        // 2. Eliminar detalles existentes
        ps = con.prepareStatement(sqlDeleteDetalles);
        ps.setInt(1, compra.getId());
        ps.executeUpdate();

        // 3. Insertar nuevos detalles
        ps = con.prepareStatement(sqlInsertDetalle);
        for (CompraDetalle detalle : detalles) {
            ps.setInt(1, compra.getId());
            ps.setInt(2, detalle.getIdProducto());
            ps.setString(3, detalle.getArticulo());
            ps.setInt(4, detalle.getCantidad());
            ps.setDouble(5, detalle.getPrecio());
            ps.setDouble(6, detalle.getSubtotal());
            ps.setString(7, detalle.getProveedor());
            ps.setString(8, detalle.getComprobante());
            ps.setString(9, detalle.getMetodoPago());
            ps.setDate(10, new java.sql.Date(detalle.getFecha().getTime()));
            ps.setString(11, detalle.getEstado());
            ps.setString(12, detalle.getUnidad());
            ps.addBatch();
        }
        ps.executeBatch();

        con.commit(); // Confirmar transacción
        return true;

    } catch (SQLException e) {
        try {
            if (con != null) con.rollback(); // Rollback en caso de error
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.err.println("Error al actualizar compra completa: " + e.getMessage());
        return false;
    } finally {
        try {
            if (con != null) con.setAutoCommit(true);
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException ex) {}
    }
}
    
    public Compra obtenerCompraConDetalles(int idCompra) {
    Compra compra = null;
    String sqlCompra = "SELECT * FROM compras WHERE idCompras = ?";
    String sqlDetalles = "SELECT * FROM detalle_compras WHERE idCompras = ?";
    
    try {
        con = cn.getConnection();
        
        // Obtener compra principal
        ps = con.prepareStatement(sqlCompra);
        ps.setInt(1, idCompra);
        rs = ps.executeQuery();
        
        if (rs.next()) {
            compra = new Compra();
            compra.setId(rs.getInt("idCompras"));
            compra.setComprobante(rs.getString("comprobante"));
            compra.setNumero(rs.getString("numero"));
            compra.setMetodoPago(rs.getString("metodo_pago"));
            compra.setProveedor(rs.getString("proveedor"));
            compra.setFechaCompra(rs.getDate("fecha_compra"));
            compra.setEstado(rs.getString("estado"));
            compra.setTotal(rs.getDouble("total"));
        }
        
        // Obtener detalles
        if (compra != null) {
            ps = con.prepareStatement(sqlDetalles);
            ps.setInt(1, idCompra);
            rs = ps.executeQuery();
            
            // Aquí podrías cargar los detalles si tu clase Compra los tuviera
            // Por ahora, solo obtenemos la compra principal
        }
        
    } catch (Exception e) {
        System.err.println("Error al obtener compra con detalles: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); if (ps != null) ps.close(); if (con != null) con.close(); } catch (SQLException ex) {}
    }
    return compra;
}
    
    public void verificarDatosCompra(int idCompra) {
    String sql = "SELECT * FROM detalle_compras WHERE idCompras = ?";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setInt(1, idCompra);
        rs = ps.executeQuery();
        
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        System.out.println("=== ESTRUCTURA DE detalle_compras ===");
        for (int i = 1; i <= columnCount; i++) {
            System.out.println("Columna " + i + ": " + metaData.getColumnName(i));
        }
        
        System.out.println("=== DATOS de compra " + idCompra + " ===");
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String columna = metaData.getColumnName(i);
                Object valor = rs.getObject(i);
                System.out.println(columna + ": " + (valor != null ? "'" + valor + "'" : "NULL"));
            }
            System.out.println("---");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
}



