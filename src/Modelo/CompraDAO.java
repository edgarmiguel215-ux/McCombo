
package Modelo;

import java.sql.*;
import java.util.*;




public class CompraDAO {


    Conexion cn = new Conexion();

    public int registrarCompra(Compra c) {
    int idGenerado = 0;
    String sql = "INSERT INTO compras(id_articulo, idProveedor, cantidad, unidad, precio_unitario, total, comprobante, metodo_pago, datos_pago, fecha, estado, nombre_articulo) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, c.getIdArticulo());   //  ID del artículo
        ps.setInt(2, c.getIdProveedor());  //  ID del proveedor
        ps.setInt(3, c.getCantidad());
        ps.setString(4, c.getUnidad());
        ps.setDouble(5, c.getPrecioUnitario());
        ps.setDouble(6, c.getTotal());
        ps.setString(7, c.getComprobante());
        ps.setString(8, c.getMetodoPago());
        ps.setString(9, c.getDatosPago());
        ps.setDate(10, new java.sql.Date(c.getFecha().getTime()));
        ps.setString(11, c.getEstado());
        ps.setString(12, c.getNombreArticulo());

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) idGenerado = rs.getInt(1);

    } catch (SQLException e) {
        System.err.println("Error registrarCompra: " + e.getMessage());
    }
    return idGenerado;
}

    public void actualizarInventarioDespuesDeCompra(Compra c) {
    InventarioDAO daoInventario = new InventarioDAO();

    // 🔹 Primero, insertar si no existe
    daoInventario.insertarInventarioSiNoExiste(c);

    // 🔹 Luego, actualizar el inventario
    String sql = "UPDATE inventario SET nombre = ?, stock_actual = stock_actual + ?, costo_unitario = ?, valor_inventario = (stock_actual + ?) * ? , estado = ? WHERE id_articulo = ?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, c.getNombreArticulo());
        ps.setInt(2, c.getCantidad());
        ps.setDouble(3, c.getPrecioUnitario());
        ps.setInt(4, c.getCantidad());
        ps.setDouble(5, c.getPrecioUnitario());
        ps.setString(6, c.getEstado());
        ps.setInt(7, c.getIdArticulo());

        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error al actualizar inventario: " + e.getMessage());
    }
}


    public List<Compra> listarCompras() {
    List<Compra> lista = new ArrayList<>();
    String sql = "SELECT c.idCompras, c.nombre_articulo, p.nombre AS proveedor, " +
                 "c.cantidad, c.unidad, c.precio_unitario, c.total, c.comprobante, " +
                 "c.metodo_pago, c.datos_pago, c.fecha, c.estado " +
                 "FROM compras c " +
                 "JOIN proveedor p ON c.idProveedor = p.id " +
                 "ORDER BY c.idCompras DESC";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Compra c = new Compra();
            c.setId(rs.getInt("idCompras"));
            c.setNombreArticulo(rs.getString("nombre_articulo")); // ← ahora se usa el campo histórico
            c.setProveedor(rs.getString("proveedor"));
            c.setCantidad(rs.getInt("cantidad"));
            c.setUnidad(rs.getString("unidad"));
            c.setPrecioUnitario(rs.getDouble("precio_unitario"));
            c.setTotal(rs.getDouble("total"));
            c.setComprobante(rs.getString("comprobante"));
            c.setMetodoPago(rs.getString("metodo_pago"));
            c.setDatosPago(rs.getString("datos_pago"));
            c.setFecha(rs.getDate("fecha"));
            c.setEstado(rs.getString("estado"));
            lista.add(c);
        }

    } catch (SQLException e) {
        System.err.println("Error listarCompras: " + e.getMessage());
    }
    return lista;
}

    public boolean editarCompra(Compra c) {
    String sql = "UPDATE compras SET id_articulo=?, idProveedor=?, cantidad=?, unidad=?, precio_unitario=?, total=?, comprobante=?, metodo_pago=?, datos_pago=?, fecha=?, estado=?, nombre_articulo=? WHERE idCompras=?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, c.getIdArticulo());
        ps.setInt(2, c.getIdProveedor());
        ps.setInt(3, c.getCantidad());
        ps.setString(4, c.getUnidad());
        ps.setDouble(5, c.getPrecioUnitario());
        ps.setDouble(6, c.getTotal());
        ps.setString(7, c.getComprobante());
        ps.setString(8, c.getMetodoPago());
        ps.setString(9, c.getDatosPago());
        ps.setDate(10, new java.sql.Date(c.getFecha().getTime()));
        ps.setString(11, c.getEstado());
        ps.setString(12, c.getNombreArticulo()); // ← nuevo campo
        ps.setInt(13, c.getId());

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error editarCompra: " + e.getMessage());
        return false;
    }
}

    
    public boolean eliminarCompra(int idCompra) {
    String sql = "UPDATE compras SET estado = 'Cancelado' WHERE idCompras = ?";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idCompra);
        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error eliminarCompra (soft delete): " + e.getMessage());
        return false;
    }
}


    public Compra obtenerCompraDesdeBD(int idCompra) {
    String sql = "SELECT c.*, a.nombre AS nombre_articulo, p.nombre AS proveedor " +
                 "FROM compras c " +
                 "JOIN articulo a ON c.id_articulo = a.id_articulo " +
                 "JOIN proveedor p ON c.idProveedor = p.id " +
                 "WHERE c.idCompras = ?";
    Compra c = new Compra();

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idCompra);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            c.setId(rs.getInt("idCompras"));
            c.setIdArticulo(rs.getInt("id_articulo"));
            c.setIdProveedor(rs.getInt("idProveedor"));
            c.setCantidad(rs.getInt("cantidad"));
            c.setUnidad(rs.getString("unidad"));
            c.setPrecioUnitario(rs.getDouble("precio_unitario"));
            c.setTotal(rs.getDouble("total"));
            c.setComprobante(rs.getString("comprobante"));
            c.setMetodoPago(rs.getString("metodo_pago"));
            c.setDatosPago(rs.getString("datos_pago"));
            c.setFecha(rs.getDate("fecha"));
            c.setEstado(rs.getString("estado"));
            c.setNombreArticulo(rs.getString("nombre_articulo")); // ← ahora sí
            c.setProveedor(rs.getString("proveedor"));            // ← ahora sí
        }

    } catch (SQLException e) {
        System.err.println("Error obtenerCompraDesdeBD: " + e.getMessage());
    }

    return c;
}

    public Compra obtenerCompraOriginal(int idCompra) {
    String sql = "SELECT * FROM compras WHERE idCompras = ?";
    Compra c = null;
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idCompra);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            c = new Compra();
            c.setId(rs.getInt("idCompras"));
            c.setIdArticulo(rs.getInt("id_articulo"));
            c.setIdProveedor(rs.getInt("idProveedor"));
            c.setCantidad(rs.getInt("cantidad"));
            c.setUnidad(rs.getString("unidad"));
            c.setPrecioUnitario(rs.getDouble("precio_unitario"));
            c.setTotal(rs.getDouble("total"));
            c.setComprobante(rs.getString("comprobante"));
            c.setMetodoPago(rs.getString("metodo_pago"));
            c.setDatosPago(rs.getString("datos_pago"));
            c.setFecha(rs.getDate("fecha"));
            c.setEstado(rs.getString("estado"));
        }
    } catch (SQLException e) {
        System.err.println("Error obtenerCompraOriginal: " + e.getMessage());
    }
    return c;
}

    public void actualizarInventarioDespuesDeEditar(Compra original, Compra editada) {
    String sqlSelect = "SELECT stock_actual, costo_unitario FROM inventario WHERE id_articulo = ?";
    String sqlUpdate = "UPDATE inventario SET nombre = ?, stock_actual = ?, valor_inventario = ?, costo_unitario = ?, unidad = ?, estado = ? WHERE id_articulo = ?";

    try (Connection con = cn.getConnection()) {
        int stockActual = 0;
        double costoUnitario = 0;

        // 1️ Obtener stock_actual y costo_unitario
        try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
            psSelect.setInt(1, editada.getIdArticulo());
            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    stockActual = rs.getInt("stock_actual");
                    costoUnitario = rs.getDouble("costo_unitario");
                } else {
                    System.err.println("No existe inventario para el artículo: " + editada.getIdArticulo());
                    return;
                }
            }
        }

        // 2️⃣ Ajustar stock según estados
        int nuevoStock = stockActual;

        // Si la compra estaba recibida, restamos la cantidad original
        if ("Recibido".equalsIgnoreCase(original.getEstado())) {
            nuevoStock -= original.getCantidad();
        }

        // Si la nueva compra está recibida, sumamos la cantidad editada
        if ("Recibido".equalsIgnoreCase(editada.getEstado())) {
            nuevoStock += editada.getCantidad();
        }

        // Evitar stock negativo
        nuevoStock = Math.max(nuevoStock, 0);

        // 3️⃣ Calcular valor inventario
        double nuevoValor = nuevoStock * editada.getPrecioUnitario();

        // 4️⃣ Actualizar inventario
        try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
            psUpdate.setString(1, editada.getNombreArticulo()); // ✅ nombre actualizado
            psUpdate.setInt(2, nuevoStock);
            psUpdate.setDouble(3, nuevoValor);
            psUpdate.setDouble(4, editada.getPrecioUnitario());
            psUpdate.setString(5, editada.getUnidad());
            psUpdate.setString(6, editada.getEstado());
            psUpdate.setInt(7, editada.getIdArticulo());
            psUpdate.executeUpdate();

            psUpdate.executeUpdate();
        }

    } catch (SQLException e) {
        System.err.println("Error actualizarInventarioDespuesDeEditar: " + e.getMessage());
        e.printStackTrace();
    }
}


    public boolean actualizarInventarioDespuesDeEliminar(Compra compra) {
    String sqlSelect = "SELECT stock_actual, costo_unitario FROM inventario WHERE id_articulo = ?";
    String sqlUpdate = "UPDATE inventario SET nombre = ?, stock_actual = ?, valor_inventario = ? WHERE id_articulo = ?";

    try (Connection con = cn.getConnection()) {

        int stockActual = 0;
        double costoUnitario = 0;

        // 1️⃣ Obtener stock_actual y costo_unitario
        try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
            psSelect.setInt(1, compra.getIdArticulo());
            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    stockActual = rs.getInt("stock_actual");
                    costoUnitario = rs.getDouble("costo_unitario");
                } else {
                    System.err.println("No existe inventario para el artículo: " + compra.getIdArticulo());
                    return false;
                }
            }
        }

        // 2️⃣ Calcular nuevo stock y valor
        int nuevoStock = Math.max(stockActual - compra.getCantidad(), 0);
        double nuevoValor = nuevoStock * costoUnitario;

        // 3️⃣ Actualizar solo stock_actual y valor_inventario
        try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
            psUpdate.setString(1, compra.getNombreArticulo()); // ✅ mantener nombre
            psUpdate.setInt(2, nuevoStock);
            psUpdate.setDouble(3, nuevoValor);
            psUpdate.setInt(4, compra.getIdArticulo());
            return psUpdate.executeUpdate() > 0;
        }

    } catch (SQLException e) {
        System.err.println("Error actualizarInventarioDespuesDeEliminar: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    public List<Compra> buscarComprasPorArticuloOProveedor(String criterio) {
    List<Compra> lista = new ArrayList<>();
    String sql = "SELECT c.idCompras, c.nombre_articulo, c.cantidad, c.unidad, " +
                 "c.precio_unitario, c.total, c.comprobante, c.metodo_pago, " +
                 "c.datos_pago, c.fecha, c.estado, p.nombre AS proveedor_nombre " +
                 "FROM compras c " +
                 "JOIN proveedor p ON c.idProveedor = p.id " +
                 "WHERE c.nombre_articulo LIKE ? OR p.nombre LIKE ?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, "%" + criterio + "%");
        ps.setString(2, "%" + criterio + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Compra c = new Compra();
            c.setId(rs.getInt("idCompras"));
            c.setNombreArticulo(rs.getString("nombre_articulo"));
            c.setCantidad(rs.getInt("cantidad"));
            c.setUnidad(rs.getString("unidad"));
            c.setPrecioUnitario(rs.getDouble("precio_unitario"));
            c.setTotal(rs.getDouble("total"));
            c.setDatosPago(rs.getString("datos_pago"));
            c.setProveedor(rs.getString("proveedor_nombre")); // ⚡ ahora sí existe
            c.setComprobante(rs.getString("comprobante"));
            c.setMetodoPago(rs.getString("metodo_pago"));
            c.setFecha(rs.getDate("fecha"));
            c.setEstado(rs.getString("estado"));
            lista.add(c);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return lista;
}

}
